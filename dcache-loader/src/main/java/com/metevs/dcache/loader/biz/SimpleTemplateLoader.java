package com.metevs.dcache.loader.biz;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.loader.constants.SinkModel;
import com.metevs.dcache.loader.constants.TVMsg;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.query.EqualQueryBuilder;
import com.metevs.dcache.facade.query.MustQueryBuilder;
import com.metevs.dcache.facade.query.RangeQueryBuilder;
import com.metevs.dcache.facade.util.QueryType;
import com.metevs.dcache.loader.hanlde.EsCacheHandle;
import com.metevs.dcache.loader.hanlde.EventLoopExecutor;
import com.metevs.dcache.loader.hanlde.MysqlHandle;
import com.metevs.dcache.loader.hanlde.RedisCacheHandle;
import com.metevs.dcache.loader.hanlde.RedisLockUtil;
import com.metevs.dcache.loader.listen.KafkaSendService;
import com.metevs.dcache.loader.runner.AbstractLoader;
import com.metevs.dcache.loader.util.ActionType;
import com.metevs.dcache.loader.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public abstract class SimpleTemplateLoader<T extends BaseDO> extends AbstractLoader<T> {

    protected Function<RedisTemplate, RedisCacheHandle<T>> redisFunction;

    protected Function<TransportClient, EsCacheHandle<T>> esFunction;

    protected Function<BaseMapper, MysqlHandle<T>> mysqlFunction;

    protected RedisCacheHandle<T> redisCacheHandle;

    protected EsCacheHandle<T> esCacheHandle;

    protected MysqlHandle<T> mysqlHandle;

    protected EventLoopExecutor eventLoopExecutor;

    @Autowired
    protected BloomFliterService bloomFliterService;

    @Autowired
    protected KafkaSendService kafkaSendService;

    //@Autowired
    private RedisLockUtil redisLockUtil;

    /**
     * 计数器服务.
     */
    protected Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }

    public AbstractLoader<T> registerExecutor(EventLoopExecutor eventLoopExecutor) {
        this.eventLoopExecutor = eventLoopExecutor;
        return this;
    }

    @Override
    public Boolean isFindHistory(CacheTask cacheTask) {
        if (redisCacheHandle != null) {
            cacheTask.setIsHistory(this.redisCacheHandle.getCacheObject(String.valueOf(cacheTask.getOrigin().getChannel())) != null);
            return cacheTask.getIsHistory();
        }
        return false;
    }

    public Boolean consumerMsg(TVMsg msg) {
        try {
            if (msg.getIsSync()) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                AtomicInteger atomicInteger = new AtomicInteger(0);
                CacheTask cacheTask = null;
                try {
                    if (msg.getT() instanceof CacheTask) {
                        cacheTask = (CacheTask) msg.getT();
                    } else if (msg.getSinkModel().equals(SinkModel.CREATE)) {
                        cacheTask = gmtCacheTask(atomicInteger, countDownLatch, (T) msg.getT(), ActionType.INCREASE,
                                this);
                    } else if (msg.getSinkModel().equals(SinkModel.MODIFIED)) {
                        cacheTask = gmtCacheTask(atomicInteger, countDownLatch, (T) msg.getT(), ActionType.MODIFY,
                                this);
                    } else if (msg.getSinkModel().equals(SinkModel.LOAD)) {
                        cacheTask = gmtCacheTask(atomicInteger, countDownLatch, supportLoad((T) msg.getT()),
                                ActionType.MODIFY,
                                this);
                    } else {
                        throw new IllegalArgumentException("un support sink model");
                    }
                    eventLoopExecutor.addTask(cacheTask);
                } finally {
                    countDownLatch.await();
                }
                return atomicInteger.get() == 1;
            } else {
                if (msg.getT() instanceof CacheTask) {
                    return eventLoopExecutor.addTask((CacheTask) msg.getT());
                } else if (msg.getSinkModel().equals(SinkModel.CREATE)) {
                    return eventLoopExecutor.addTask(new CacheTask<>((T) msg.getT(), ActionType.INCREASE, this));
                } else if (msg.getSinkModel().equals(SinkModel.MODIFIED)) {
                    return eventLoopExecutor.addTask(new CacheTask<>((T) msg.getT(), ActionType.MODIFY, this));
                } else if (msg.getSinkModel().equals(SinkModel.LOAD)) {
                    return eventLoopExecutor.addTask(new CacheTask<>((T) msg.getT(), ActionType.LOAD, this));
                } else {
                    throw new IllegalArgumentException("un support sink model");
                }
            }
        } catch (Exception e) {
            log.error("modify failed..", e);
        }
        return false;
    }


    /**
     * 提供额外重写补充loader方法
     *
     * @param t
     * @return
     */
    protected T supportLoad(T t) {
        return t;
    }

    protected String toJavaString(String position) {
        position = position.substring(1, position.length() - 1);
        return position.replaceAll("\\\\", "");
    }

    private CacheTask<T> gmtCacheTask(AtomicInteger atomicInteger, CountDownLatch countDownLatch, T t, ActionType actionType,
                                      AbstractLoader<T> abstractLoader) {
        return new CacheTask<>(t, actionType, abstractLoader,
                (Function<Boolean, Object>) result -> {
                    if (result == true) {
                        atomicInteger.incrementAndGet();
                    }
                    countDownLatch.countDown();
                    return null;
                });
    }

    /**
     * @param drTeamQueryParam
     * @param index
     * @param docment
     * @return
     */
    protected List<T> findCommonQuery(DCacheTeamQueryParam drTeamQueryParam, String index, String docment) {
        SortOrder sortOrder = null;
        String cellName = null;

        if (null != drTeamQueryParam.getFindSort()) {
            cellName = drTeamQueryParam.getFindSort().getSortCell();
            if (drTeamQueryParam.getFindSort().getSortOrder().equals(com.metevs.dcache.facade.domain.SortOrder.ASC)) {
                sortOrder = SortOrder.ASC;
            } else {
                sortOrder = SortOrder.DESC;
            }
        }
        return esCacheHandle.searchObj(esParamAnalysis(drTeamQueryParam), index, docment, cellName, sortOrder);
    }

    @Override
    public void cacheAfterSet(CacheTask cacheTask, Boolean bool) {
        if (!bool) {
            if (cacheTask.getCheck()) {
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.REPEATED_FAILED));
            } else {
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.FAILED));
            }
            return;
        }
        switch (cacheTask.getActionType()) {
            case LOAD:
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.LOAD_SUCCESS));
                break;
            case INCREASE:
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.INSERT_SUCCESS));
                break;
            case MODIFY:
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.MODIFY_SUCCESS));
                break;
            case INVALID:
                counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.INVALID_SUCCESS));
                break;
        }
        if (cacheTask.getCheck()) {
            counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.INVALID_SUCCESS));
        }
    }

    @Override
    public void customBloomFilter(String value) {
    }


    /**
     * @param cacheTask
     * @return
     */
    private static String getCacheLockKey(CacheTask cacheTask) {
        return cacheTask.getLoader().hashCode() + "&" + cacheTask.getOrigin().getChannel();
    }


    @Override
    public Boolean tryLock(CacheTask cacheTask) {
        String lockName = getCacheLockKey(cacheTask);
        return redisLockUtil.tryLock(lockName);
    }

    @Override
    public Boolean unLock(CacheTask cacheTask) {
        String lockName = getCacheLockKey(cacheTask);
        redisLockUtil.unlock(lockName);
        return true;
    }

    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected QueryBuilder esParamAnalysis(DCacheTeamQueryParam drTeamQueryParam) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());
        if (!CollectionUtils.isEmpty(drTeamQueryParam.getEqualQueryBuilders())) {
            for (EqualQueryBuilder equalQueryBuilder : drTeamQueryParam.getEqualQueryBuilders()) {
                if (equalQueryBuilder.getT() != null) {
                    if (equalQueryBuilder.getEqualType().equals(QueryType.EqualType.EQUAL)) {
                        if (equalQueryBuilder.getT() instanceof LocalDate) {
                            queryBuilder.must(QueryBuilders.termQuery(equalQueryBuilder.getFieldName(),
                                    ((LocalDate) equalQueryBuilder.getT()).format(dateFormatter)));
                        } else {
                            queryBuilder.must(QueryBuilders.termQuery(equalQueryBuilder.getFieldName(),
                                    equalQueryBuilder.getT()));
                        }

                    } else {
                        if (equalQueryBuilder.getT() instanceof LocalDate) {
                            queryBuilder.mustNot(QueryBuilders.termQuery(equalQueryBuilder.getFieldName(),
                                    ((LocalDate) equalQueryBuilder.getT()).format(dateFormatter)));
                        } else {
                            queryBuilder.mustNot(QueryBuilders.termQuery(equalQueryBuilder.getFieldName(),
                                    equalQueryBuilder.getT()));
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(equalQueryBuilder.getTs())) {
                    if (equalQueryBuilder.getEqualType().equals(QueryType.EqualType.EQUAL)) {
                        queryBuilder.must(QueryBuilders.termsQuery(equalQueryBuilder.getFieldName(),
                                equalQueryBuilder.getTs()));
                    } else {
                        queryBuilder.mustNot(QueryBuilders.termsQuery(equalQueryBuilder.getFieldName(),
                                equalQueryBuilder.getTs()));
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(drTeamQueryParam.getRangeQueryBuilders())) {
            for (RangeQueryBuilder rangeQueryBuilder : drTeamQueryParam.getRangeQueryBuilders()) {
                if (rangeQueryBuilder.getT() instanceof LocalDateTime) {
                    if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gt(((LocalDateTime) rangeQueryBuilder.getT()).toInstant(ZoneOffset.of("+0")).toEpochMilli()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gte(((LocalDateTime) rangeQueryBuilder.getT()).toInstant(ZoneOffset.of("+0")).toEpochMilli()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lt(((LocalDateTime) rangeQueryBuilder.getT()).toInstant(ZoneOffset.of("+0")).toEpochMilli()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lte(((LocalDateTime) rangeQueryBuilder.getT()).toInstant(ZoneOffset.of("+0")).toEpochMilli()));
                } else if (rangeQueryBuilder.getT() instanceof LocalDate) {
                    if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gt(((LocalDate) rangeQueryBuilder.getT()).format(dateFormatter)));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gte(((LocalDate) rangeQueryBuilder.getT()).format(dateFormatter)));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lt(((LocalDate) rangeQueryBuilder.getT()).format(dateFormatter)));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lte(((LocalDate) rangeQueryBuilder.getT()).format(dateFormatter)));
                } else {
                    if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gt(rangeQueryBuilder.getT()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.GTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).gte(rangeQueryBuilder.getT()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LT))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lt(rangeQueryBuilder.getT()));
                    else if (rangeQueryBuilder.getCompareType().equals(QueryType.CompareType.LTE))
                        queryBuilder.must(QueryBuilders.rangeQuery(rangeQueryBuilder.getFieldName()).lte(rangeQueryBuilder.getT()));
                }
            }
        }

        if (!CollectionUtils.isEmpty(drTeamQueryParam.getMustQueryBuilders())) {
            for (MustQueryBuilder mustQueryBuilder : drTeamQueryParam.getMustQueryBuilders()) {
                if (mustQueryBuilder.getNullType().equals(QueryType.NullType.NOTNULL)) {
                    queryBuilder.must(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName()));
                } else if (mustQueryBuilder.getNullType().equals(QueryType.NullType.ISNULL)) {
                    queryBuilder.mustNot(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName()));
                } else if (mustQueryBuilder.getNullType().equals(QueryType.NullType.NULL_OR_DEFAULT)) {
                    if (mustQueryBuilder.getDefaultValue() instanceof LocalDateTime) {
                        queryBuilder.mustNot(new BoolQueryBuilder().should(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName())).should(
                                QueryBuilders.termsQuery(mustQueryBuilder.getFieldName(),
                                        ((LocalDateTime) mustQueryBuilder.getDefaultValue()).format(Constants.DATE_TIME_FORMATTER))));
                    } else {
                        queryBuilder.mustNot(new BoolQueryBuilder().should(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName())).should(
                                QueryBuilders.termsQuery(mustQueryBuilder.getFieldName(), mustQueryBuilder.getDefaultValue())));
                    }

                } else if (mustQueryBuilder.getNullType().equals(QueryType.NullType.NOTNULL_OR_DEFAULT)) {
                    if (mustQueryBuilder.getDefaultValue() instanceof LocalDateTime) {
                        queryBuilder.must(new BoolQueryBuilder().should(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName())).should(
                                QueryBuilders.termsQuery(mustQueryBuilder.getFieldName(),
                                        ((LocalDateTime) mustQueryBuilder.getDefaultValue()).format(Constants.DATE_TIME_FORMATTER))));
                    } else {
                        queryBuilder.must(new BoolQueryBuilder().should(QueryBuilders.existsQuery(mustQueryBuilder.getFieldName())).should(
                                QueryBuilders.termsQuery(mustQueryBuilder.getFieldName(), mustQueryBuilder.getDefaultValue())));
                    }
                }
            }
        }
        return queryBuilder;
    }
}
