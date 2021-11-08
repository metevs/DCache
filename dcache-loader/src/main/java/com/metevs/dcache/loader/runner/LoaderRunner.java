package com.metevs.dcache.loader.runner;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.loader.cache.DepositTask;
import com.metevs.dcache.loader.constants.SinkModel;
import com.metevs.dcache.loader.constants.TVMsg;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.loader.hanlde.EventLoopExecutor;
import com.metevs.dcache.loader.util.AsyncChannelProcess;
import com.metevs.dcache.loader.util.ScheduleInvalidLoad;
import com.metevs.dcache.loader.util.ScheduleRiseLoad;
import com.metevs.dcache.loader.util.ScheduleRiseSiteLoad;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.metevs.dcache.loader.constants.Constants.*;

/**
 *
 */
@Component
@Slf4j
@Configuration
@EnableScheduling
public class LoaderRunner implements CommandLineRunner, ApplicationContextAware {

    public static AtomicLong crt = new AtomicLong(0);

    @Setter
    @Getter
    private ApplicationContext applicationContext;

    @Autowired
    private TransportClient client;

    private List<AbstractLoader> loaders = new ArrayList<>();

    @Value("${zk.force.update}")
    private Boolean forceUpdate = false;

    @Value("${log.debug}")
    private Boolean logDebug = false;

    @Resource
    @Qualifier("asyncChannelProcess")
    private AsyncChannelProcess<TVMsg> asyncChannelProcess;

    @Autowired
    private StringRedisTemplate loaderRedisTemplate;

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }


    @Bean(name = "asyncChannelProcess")
    public AsyncChannelProcess getTvMsgAsyncChannelProcess() {
        return AsyncChannelProcess.<TVMsg>newBuilder()
                .setAddBlockTimeout(24, TimeUnit.HOURS)
                .setMaxProcessCount(10)
                .setProcess((Function<TVMsg, Boolean>) msg -> {
                    saveImpl(msg);
                    return true;
                })
                .build();
    }

    /**
     * @param msg
     */
    private void saveImpl(TVMsg msg) {
        if (logDebug)
            log.info("recv data {}", JSON.toJSONString(msg));
        if (CollectionUtils.isEmpty(loaders)) {
            return;
        }
        for (AbstractLoader abstractLoader : loaders) {
            if (abstractLoader.supportType().isInstance(msg.getT())) {
                abstractLoader.consumerMsg(msg);
                return;
            }
        }
        log.info("unKnow type {}", JSON.toJSONString(msg));
    }

    @PostConstruct
    public void init() {
        Map<String, AbstractLoader> map = applicationContext.getBeansOfType(AbstractLoader.class);
        ExecutorService executorService = Executors.newFixedThreadPool(map.size() - 1);
        EventLoopExecutor[] eventLoopExcutors = new EventLoopExecutor[map.size() - 1];
        //缺班特殊情况，将缺班与形成记录放到一个线程池里面处理
        if (!map.isEmpty()) {
            ScarceShiftLoader scarceShiftLoader = null;
            List<AbstractLoader> waitingInitLoader = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                AbstractLoader loader = ((AbstractLoader) map.values().toArray()[i]);
                if (loader instanceof ScarceShiftLoader) {
                    scarceShiftLoader = (ScarceShiftLoader) map.values().toArray()[i];
                    continue;
                }
                waitingInitLoader.add(loader);
            }
            for (int i = 0; i < waitingInitLoader.size(); i++) {
                eventLoopExcutors[i] = new EventLoopExecutor(30, applicationContext);
                loaders.add(waitingInitLoader.get(i).registerExecutor(eventLoopExcutors[i]));
                waitingInitLoader.get(i).initHandle();
                waitingInitLoader.get(i).initCache();
                if (waitingInitLoader.get(i) instanceof DrivingRecordLoader) {
                    loaders.add(scarceShiftLoader.registerExecutor(eventLoopExcutors[i]));
                    scarceShiftLoader.initHandle();
                    scarceShiftLoader.registerExecutor(eventLoopExcutors[i]);
                    scarceShiftLoader.initCache();
                    log.info("scare shift loader init {}", i);
                }
                executorService.execute(eventLoopExcutors[i]);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        //强制更新一次
        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String hash = String.valueOf(UUID.randomUUID().toString().hashCode());
        String nowValue = loaderRedisTemplate.opsForValue().getAndSet(ZK_UPDATE_PATH, now + hash);
        //loaderRedisTemplate.expire(ZK_UPDATE_PATH, 120, TimeUnit.SECONDS);
        if (!StringUtils.isEmpty(nowValue)) {
            log.info("LoaderRunner init already ..{}..{}", nowValue, now);
            return;
        } else {
            log.info("LoaderRunner initing data..{}", now);
            initDate();
        }
    }

    /**
     * 将外部任务转换为内部队列
     *
     * @param cacheMsg
     */
    public void consumer(CacheMsg cacheMsg) {
        Class cls = cacheMsg.getType().getaClass();
        if (cls == null) {
            throw new IllegalArgumentException("un support consumer type");
        }
        TVMsg tvMsg = new TVMsg(SinkModel.LOAD, JSON.parseObject(cacheMsg.getValue(), cls), false);
        asyncChannelProcess.add(tvMsg, ((BaseDO) tvMsg.getT()).getChannel());
    }

    /**
     * 内部协议
     *
     * @param cacheMsg
     */
    public void syncConsumer(CacheMsg cacheMsg) {
        Class cls = cacheMsg.getType() == null ? null : cacheMsg.getType().getaClass();
        if (cls != null) {
            throw new IllegalArgumentException("un support sync consumer type");
        }
        DepositTask depositTask = JSON.parseObject(cacheMsg.getValue(), DepositTask.class);
        AbstractLoader abstractLoader = (AbstractLoader) applicationContext.getBean(depositTask.getLoader());
        CacheTask cacheTask = deposit(depositTask, abstractLoader);
        TVMsg tvMsg = new TVMsg(SinkModel.LOAD, cacheTask, true);
        abstractLoader.consumerMsg(tvMsg);
    }

    private CacheTask deposit(DepositTask depositTask, AbstractLoader abstractLoader) {
        return CacheTask.newBuilder()
                .loader(abstractLoader)
                .origin(depositTask.getOrigin())
                .history(depositTask.getHistory())
                .source(1)
                .isHistory(depositTask.getIsHistory())
                .transfer(depositTask.getTransfer())
                .actionType(depositTask.getActionType())
                .executeStep(depositTask.getExecuteStep())
                .initTime(depositTask.getInitTime()).build();
    }

    private void initDate() {
        if (isIndexExist(DR_INDEX))
            clearIndex(DR_INDEX);
        if (isIndexExist(EMP_INDEX))
            clearIndex(EMP_INDEX);

        if (!CollectionUtils.isEmpty(loaders)) {
            for (AbstractLoader loader : loaders) {
                try {
                    loader.clearData();
                    loader.normalDataLoad();
                } catch (Exception e) {
                    log.error("loader init failed {}", loader.getClass(), e);
                }
            }
        }
    }

    @Scheduled(cron = "${loader.schedule.normal}")
    public void normalSchedule() {
    }

    @Scheduled(cron = "${loader.schedule.normal}")
    public void riseSiteSchedule() {
        log.info("rise invalid schedule load ..");
        //强制更新一次
        _riseSiteSchedule();
//        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
//        String hash = String.valueOf(UUID.randomUUID().toString().hashCode());
//        String nowValue = loaderRedisTemplate.opsForValue().getAndSet(ZK_RISE_SITE_PATH, now + hash);
//        String riseTimeOut = loaderRedisTemplate.opsForValue().get(ZK_RISE_SITE_TIMEOUT);
//        if (!StringUtils.isEmpty(nowValue)) {
//            log.info("LoaderRunner site rise already ..{}..{}", nowValue, now);
//            return;
//        } else {
//            log.info("LoaderRunner site rise start..{}", now);
//            loaderRedisTemplate.expire(ZK_RISE_SITE_PATH, StringUtils.isEmpty(riseTimeOut) ? 60 * 12 :
//                    Integer.valueOf(riseTimeOut), TimeUnit.MINUTES);
//
//        }
    }

    @Scheduled(fixedDelayString = "${loader.schedule.rise.site}")
    public void riseSchedule() {
        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String hash = String.valueOf(UUID.randomUUID().toString().hashCode());

        String riseNowValue = loaderRedisTemplate.opsForValue().get(ZK_RISE_PATH);
        if (!StringUtils.isEmpty(riseNowValue)) {
            log.info("LoaderRunner rise already ..{}..{}", riseNowValue, now);
            return;
        }

        String riseTimeOut = loaderRedisTemplate.opsForValue().get(ZK_RISE_TIMEOUT);
        loaderRedisTemplate.opsForValue().set(ZK_RISE_PATH,
                now + hash);
        loaderRedisTemplate.expire(ZK_RISE_PATH, StringUtils.isEmpty(riseTimeOut) ? 30 :
                Integer.valueOf(riseTimeOut), TimeUnit.MINUTES);

        log.info("LoaderRunner rise start..{}", now);
        _riseSchedule();
    }


    private void _riseSchedule() {
        Map<String, Object> beansWithAnnotationMap = this.applicationContext.getBeansWithAnnotation(ScheduleRiseLoad.class);
        if (!beansWithAnnotationMap.isEmpty()) {
            for (Object object : beansWithAnnotationMap.values()) {
                ((AbstractLoader) object).scheduleCheckDataLoad();
            }
        }
    }

    private void _riseSiteSchedule() {
        Map<String, Object> beansWithAnnotationMap = this.applicationContext.getBeansWithAnnotation(ScheduleRiseSiteLoad.class);
        if (!beansWithAnnotationMap.isEmpty()) {
            for (Object object : beansWithAnnotationMap.values()) {
                ((AbstractLoader) object).clearData();
                ((AbstractLoader) object).normalDataLoad();
            }
        }
    }

//    @Scheduled(fixedDelayString = "${loader.schedule.rise}")
//    public void riseSchedule() {
//        log.info("rise schedule load ..");
//        Map<String, Object> beansWithAnnotationMap = this.applicationContext.getBeansWithAnnotation(ScheduleRiseLoad.class);
//        LocalDateTime now = LocalDateTime.now();
//        //判断今天是否全量加载过
//        String time = loaderMonitor.getMonitorMapValue(ZK_UPDATE_PATH);
//        if (StringUtils.isEmpty(time) || LocalDateTime.parse(time, DATE_TIME_FORMATTER).isBefore(LocalDateTime.of(LocalDate.now(),
//                LocalTime.MIN))) {
//            loaderMonitor.register(ZK_RISE_PATH, now.format(DATE_TIME_FORMATTER));
//            return;
//        }
//        String lastRiseTime = loaderMonitor.getMonitorMapValue(ZK_RISE_PATH);
//        if (!beansWithAnnotationMap.isEmpty()) {
//            for (Object object : beansWithAnnotationMap.values()) {
//                ((AbstractLoader) object).riseDataLoad(LocalDateTime.parse(lastRiseTime,
//                        DATE_TIME_FORMATTER), now);
//            }
//        }
//        loaderMonitor.register(ZK_RISE_PATH, now.format(DATE_TIME_FORMATTER));
//    }

    @Scheduled(fixedDelayString = "${loader.schedule.rise}")
    public void riseInvalidSchedule() {
        log.info("rise invalid schedule load ..");
        Map<String, Object> beansWithAnnotationMap = this.applicationContext.getBeansWithAnnotation(ScheduleInvalidLoad.class);
        if (!beansWithAnnotationMap.isEmpty()) {
            for (Object object : beansWithAnnotationMap.values()) {
                ((AbstractLoader) object).invalidDataLoad();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void clearIndex(String indexName) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchAllQuery())
                        .source(indexName)
                        .get();
        response.getDeleted();
    }

    public Boolean isIndexExist(String indexName) {
        Boolean flag = false;
        try {
            IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(indicesExistsRequest).actionGet();
            flag = indicesExistsResponse.isExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}


