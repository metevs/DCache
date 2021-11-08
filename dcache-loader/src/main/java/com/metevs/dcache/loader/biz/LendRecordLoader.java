package com.metevs.dcache.loader.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.loader.constants.Constants;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.query.EqualQueryBuilder;
import com.metevs.dcache.facade.query.LendRecordField;
import com.metevs.dcache.facade.query.QueryEntity;
import com.metevs.dcache.loader.hanlde.EsCacheHandle;
import com.metevs.dcache.loader.hanlde.NodeHandleFactory;
import com.metevs.dcache.loader.hanlde.RedisCacheHandle;
import com.metevs.dcache.loader.hanlde.SimpleEsCacheHandle;
import com.metevs.dcache.loader.hanlde.SimpleMysqlHandle;
import com.metevs.dcache.loader.hanlde.SimpleRedisCacheHandle;
import com.metevs.dcache.loader.mapper.LendRecordEntity;
import com.metevs.dcache.loader.mapper.LendRecordMapper;
import com.metevs.dcache.loader.util.ActionType;
import com.metevs.dcache.loader.util.BeanCopyUtils;
import com.metevs.dcache.loader.util.ScheduleRiseLoad;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * file LendRecordLoader.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/28
 */
@Component
@Slf4j
@ScheduleRiseLoad
public class LendRecordLoader extends SimpleTemplateLoader<LendRecordCache> {

    @Resource
    public RedisTemplate loaderRedisTemplate;

    @Autowired
    private NodeHandleFactory nodeHandleFactory;

    @Resource
    private TransportClient transportClient;

    @Autowired
    private LendRecordMapper lendRecordMapper;

    public final static String REDIS_KEY = "DATA_LOADER_LR";

    @Override
    public void initHandle() {
        redisCacheHandle = redisFunction.apply(loaderRedisTemplate);
        esCacheHandle = esFunction.apply(transportClient);
        mysqlHandle = mysqlFunction.apply(lendRecordMapper);
        this.addLastIncreaseHandle(newNode(redisCacheHandle, nodeHandleFactory));
        this.addLastIncreaseHandle(newNode(esCacheHandle, nodeHandleFactory));
        this.addLastLoadHandle(newNode(redisCacheHandle, nodeHandleFactory));
        this.addLastLoadHandle(newNode(esCacheHandle, nodeHandleFactory));
        this.addLastModifyHandle(newNode(redisCacheHandle, nodeHandleFactory));
        this.addLastModifyHandle(newNode(esCacheHandle, nodeHandleFactory));
        this.addLastModifyHandle(newNode(mysqlHandle, nodeHandleFactory));
        bloomFliterService.register(this, BloomFliterService.Type.ROUTE, loaderRedisTemplate);
        bloomFliterService.register(this, BloomFliterService.Type.BUS, loaderRedisTemplate);
    }

    @Override
    public void customBloomFilter(String value) {
        LendRecordCache lendRecordCache = JSONObject.parseObject(value, LendRecordCache.class);
        this.customBloomFilter(lendRecordCache);
    }

    public void customBloomFilter(LendRecordCache lendRecordCache) {
        if (lendRecordCache != null) {
            bloomFliterService.addBloomFilter(lendRecordCache.getBusId(), this, BloomFliterService.Type.BUS);
            bloomFliterService.addBloomFilter(lendRecordCache.getRouteId(), this, BloomFliterService.Type.ROUTE);
            bloomFliterService.addBloomFilter(lendRecordCache.getLendRouteId(), this, BloomFliterService.Type.ROUTE);
        }
    }


    @Override
    public void initCache() {

    }

    @PostConstruct
    public void runnerInit() {
        redisFunction = new Function<RedisTemplate, RedisCacheHandle<LendRecordCache>>() {
            @Override
            public RedisCacheHandle<LendRecordCache> apply(RedisTemplate loaderRedisTemplate) {
                return new SimpleRedisCacheHandle<LendRecordCache>(loaderRedisTemplate, REDIS_KEY) {

                    @Override
                    public Boolean invalidData(CacheTask<LendRecordCache> cacheTask) {
                        LendRecordCache lendRecordCache = cacheTask.getOrigin();
                        return this.invaildCacheObject(String.valueOf(lendRecordCache.getId()), lendRecordCache);
                    }

                    @Override
                    public Boolean increaseData(CacheTask<LendRecordCache> cacheTask) {
                        LendRecordCache lendRecordCache = cacheTask.getOrigin();
                        return this.storeCacheObject(String.valueOf(lendRecordCache.getId()), lendRecordCache);
                    }

                    @Override
                    public Boolean modifyData(CacheTask<LendRecordCache> cacheTask) {
                        LendRecordCache lendRecordCache = cacheTask.getOrigin();
                        LendRecordCache history = this.getCacheObject(String.valueOf(lendRecordCache.getId()));
                        if (history == null) {
                            throw new IllegalArgumentException("更新数据不存在");
                        }

                        //直接使用新值覆盖旧制 包括null
                        cacheTask.setTransfer(lendRecordCache);
                        return this.storeCacheObject(String.valueOf(lendRecordCache.getId()), lendRecordCache);
                    }
                };

            }

        };

        esFunction = new Function<TransportClient, EsCacheHandle<LendRecordCache>>() {
            @Override
            public EsCacheHandle<LendRecordCache> apply(TransportClient transportClient) {
                return new SimpleEsCacheHandle<LendRecordCache>(transportClient) {

                    @Override
                    public Boolean increaseData(CacheTask<LendRecordCache> cacheTask) {
                        LendRecordCache lendRecordCache = cacheTask.getOrigin();
                        this.createIndexResponse(Constants.LD_INDEX, Constants.LD_DOCUMENT, objToEsJson(lendRecordCache),
                                String.valueOf(lendRecordCache.getId()));
                        return true;
                    }

                    @Override
                    public Boolean modifyData(CacheTask<LendRecordCache> cacheTask) {
                        if (cacheTask.getTransfer() != null) {
                            return this.updateIndex(Constants.LD_INDEX, Constants.LD_DOCUMENT,
                                    objToEsJson(cacheTask.getTransfer()),
                                    String.valueOf(cacheTask.getOrigin().getId()));
                        } else {
                            log.error("threadLocal is null.. todo");
                        }
                        return false;
                    }

                    @Override
                    public String objToEsJson(LendRecordCache o) {
                        return lendRecordToEsJson(o);
                    }
                };
            }
        };

        /**
         *
         */
        mysqlFunction = mapper -> new SimpleMysqlHandle<LendRecordCache>(mapper) {
            @Override
            public Boolean modifyData(CacheTask<LendRecordCache> task) {
                LendRecordEntity lendRecordEntity = new LendRecordEntity();
                BeanCopyUtils.copy(task.getOrigin(), lendRecordEntity);
                return lendRecordMapper.updateById(lendRecordEntity) == 1;
            }
        };
    }

    private String lendRecordToEsJson(LendRecordCache lendRecordCache) {
        String jsonList = null;
        try {
            XContentBuilder jsonData = XContentFactory.jsonBuilder();
            jsonData.startObject().field(Constants.LD_KEY, lendRecordCache.getId())
                    .field("routeId", lendRecordCache.getRouteId())
                    .field("shiftNum", lendRecordCache.getShiftNum())
                    .field("busId", lendRecordCache.getBusId())
                    .field("empId", lendRecordCache.getEmpId())
                    .field("empName", lendRecordCache.getEmpName())
                    .field("planSeqNum", lendRecordCache.getPlanSeqNum())
                    .field("insertStationId", lendRecordCache.getInsertStationId())
                    .field("lendTime", lendRecordCache.getLendTime())
                    .field("lendRouteId", lendRecordCache.getLendRouteId())
                    .field("attrId", lendRecordCache.getAttrId())
                    .field("planOnTime", lendRecordCache.getPlanOnTime())
                    .field("createTime", lendRecordCache.getCreateTime())
                    .field("modifiedTime", lendRecordCache.getModifiedTime())
                    .field("isDelete", lendRecordCache.getIsDelete())
                    .field("cityCode", lendRecordCache.getCityCode())
                    .field("operationId", lendRecordCache.getOperationId())
                    .field("execDate", lendRecordCache.getExecDate()).endObject();

            jsonList = jsonData.string();
        } catch (Exception e) {
            log.warn("json serialize failed for exception {}", e);
        }
        return jsonList;
    }

    @Override
    public void cacheAfterSet(CacheTask cacheTask, Boolean bool) {
        super.cacheAfterSet(cacheTask, bool);
        if (bool) {
            LendRecordCache lendRecordCache = (LendRecordCache) cacheTask.getOrigin();
            JSONObject jsonObject = (JSONObject) JSON.toJSON(lendRecordCache);
            jsonObject.put("dataType", 3);
            //TODO 缺少商户字段
            kafkaSendService.sendToSouthNotify(jsonObject.toJSONString(), lendRecordCache.getCityCode() + 01);
        }
    }

    @Override
    public Boolean riseDataLoad(LocalDateTime start, LocalDateTime end) {
        List<LendRecordCache> lendRecordCaches = lendRecordMapper.riseCacheBuilder(start.format(Constants.DATE_TIME_FORMATTER),
                end.format(Constants.DATE_TIME_FORMATTER));
        if (CollectionUtils.isEmpty(lendRecordCaches)) {
            return true;
        }

        for (LendRecordCache lendRecordCache : lendRecordCaches) {
            counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.LOAD_SUCCESS));
            LendRecordCache history = this.redisCacheHandle.getCacheObject(String.valueOf(lendRecordCache.getId()));
            CacheTask cacheTask = new CacheTask<>(lendRecordCache, ActionType.LOAD, this);
            cacheTask.setHistory(history);
            this.eventLoopExecutor.addTask(cacheTask);
        }
        log.info("riseDataLoad  lend record modify.. size = {}", lendRecordCaches.size());
        return true;
    }

    @Override
    public Boolean normalDataLoad() {
        List<LendRecordCache> lendRecordCaches = lendRecordMapper.cacheBuilder();
        if (CollectionUtils.isEmpty(lendRecordCaches)) {
            return true;
        }
        for (LendRecordCache lendRecordCache : lendRecordCaches) {
            counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.LOAD_SUCCESS));
            this.eventLoopExecutor.addTask(new CacheTask<>(lendRecordCache, ActionType.LOAD, this));
            kafkaSendService.sendToFullCanalNotify(JSON.toJSONString(new CacheMsg(QueryEntity.LEND_RECORD,
                    JSON.toJSONString(lendRecordCache), null, null)));
        }
        return true;
    }

    @Override
    public Boolean scheduleCheckDataLoad() {
        List<LendRecordCache> lendRecordCaches = lendRecordMapper.cacheBuilder();
        if (CollectionUtils.isEmpty(lendRecordCaches)) {
            return true;
        }
        for (LendRecordCache lendRecordCache : lendRecordCaches) {
            counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.LOAD_SUCCESS));
            this.eventLoopExecutor.addTask(new CacheTask<>(lendRecordCache, ActionType.LOAD, this, true));
            kafkaSendService.sendToFullCanalNotify(JSON.toJSONString(new CacheMsg(QueryEntity.LEND_RECORD,
                    JSON.toJSONString(lendRecordCache), null, null)));
        }
        return true;
    }


    @Override
    protected LendRecordCache supportLoad(LendRecordCache lendRecordCache) {
        return this.lendRecordMapper.selectCacheByPrimaryKey(lendRecordCache.getId());
    }

    public LendRecordCache findRecordByPriId(Long recordId) {
        return redisCacheHandle.getCacheObject(String.valueOf(recordId));
    }

    public List<LendRecordCache> findDrTeamQuerys(DCacheTeamQueryParam drTeamQueryParam) {

        //判断是否有通过线路、车辆来查询的
        if (!CollectionUtils.isEmpty(drTeamQueryParam.getEqualQueryBuilders())) {
            for (EqualQueryBuilder equalQueryBuilder : drTeamQueryParam.getEqualQueryBuilders()) {
                if (equalQueryBuilder.getFieldName().equals(LendRecordField.BUS_ID.getAlias())) {
                    if (!bloomFliterService.containsObject(Long.valueOf(equalQueryBuilder.getT().toString()), this,
                            BloomFliterService.Type.BUS)) {
                        return new ArrayList<>();
                    }
                }
                if (equalQueryBuilder.getFieldName().equals(LendRecordField.ROUTE_ID.getAlias())) {
                    if (equalQueryBuilder.getT() != null && !bloomFliterService.containsObject(Long.valueOf(equalQueryBuilder.getT().toString()), this,
                            BloomFliterService.Type.ROUTE)) {
                        return new ArrayList<>();
                    }
                }
                if (equalQueryBuilder.getFieldName().equals(LendRecordField.LEND_ROUTE_ID.getAlias())) {
                    if (equalQueryBuilder.getT() != null && !bloomFliterService.containsObject(Long.valueOf(equalQueryBuilder.getT().toString()), this,
                            BloomFliterService.Type.ROUTE)) {
                        return new ArrayList<>();
                    }
                }
            }
        }

        //判断是否存在文档
        if (!this.esCacheHandle.isIndexDocumentExitCache(Constants.LD_INDEX, Constants.LD_DOCUMENT, Constants.LD_KEY)) {
            return null;
        }

        return this.findCommonQuery(drTeamQueryParam, Constants.LD_INDEX, Constants.LD_DOCUMENT);
    }
}
