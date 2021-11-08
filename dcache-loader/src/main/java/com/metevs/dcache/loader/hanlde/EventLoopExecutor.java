package com.metevs.dcache.loader.hanlde;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.loader.cache.DepositTask;
import com.metevs.dcache.loader.constants.RepeatedDataException;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.loader.listen.KafkaSendService;
import com.metevs.dcache.loader.util.AsyncChannelProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class EventLoopExecutor implements Runnable {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private AsyncChannelProcess asyncChannelProcess;

    private Integer maxProcessCount;

    private KafkaSendService kafkaSendService;

    public EventLoopExecutor(Integer maxProcessCount, ApplicationContext applicationContext) {
        this.maxProcessCount = maxProcessCount;
        kafkaSendService = applicationContext.getBean(KafkaSendService.class);
    }

    public void saveImpl(CacheTask cacheTask) {
        Long startTime = System.currentTimeMillis();
        //Boolean isLock = false;
        try {
            /*if (!cacheTask.getLoader().tryLock(cacheTask)) {
                throw new RuntimeException("get lock failed");
            }
            isLock = true;*/
            cacheTask.getLoader().customBloomFilter(JSON.toJSONString(cacheTask.getOrigin()));
            cacheTask.run();
        } catch (RepeatedDataException repeatedDataException) {
            log.error("repeated data : {}", JSON.toJSONString(cacheTask.getOrigin().getChannel()));
            cacheTask.getLoader().cacheAfterSet(cacheTask, false);
            return;
        } catch (Exception e) {
            cacheTask.getLoader().cacheAfterSet(cacheTask, false);
            log.error("cache task run error {}", cacheTask.getOrigin(), e);
            int retryCount = cacheTask.getRetry().incrementAndGet();
            if (retryCount > 2) {
                cacheTask.failed();
                if (cacheTask.getSource() == 0)
                    kafkaSendService.sendToRetry(JSON.toJSONString(new CacheMsg(null,
                            JSON.toJSONString(deposit(cacheTask)),
                            cacheTask.getChannel(), null)));
                cacheTask.getLoader().cacheAfterSet(cacheTask, false);
                return;
            }
            this.addHeadTask(cacheTask);
            return;
        } finally {
            /*if (isLock) {
                cacheTask.getLoader().unLock(cacheTask);
            }*/
        }
        cacheTask.success();
        cacheTask.getLoader().cacheAfterSet(cacheTask, true);
        Long endTime = System.currentTimeMillis();
        if (atomicInteger.incrementAndGet() % 10000 == 1) {
            log.info("cost time = {} queue size = {}", endTime - startTime);
        }
    }

    public Boolean addTask(CacheTask cacheTask) {
        asyncChannelProcess.add(cacheTask, cacheTask.getOrigin().getChannel());
        return true;
    }

    public Boolean addHeadTask(CacheTask cacheTask) {
        try {
            asyncChannelProcess.addFirst(cacheTask, cacheTask.getOrigin().getChannel());
        } catch (IllegalStateException illegalStateException) {
            log.error("addHeadTask failed..", illegalStateException);
        }
        return true;
    }

    private DepositTask deposit(CacheTask cacheTask) {
        DepositTask depositTask = DepositTask.builder()
                .actionType(cacheTask.getActionType())
                .executeStep(cacheTask.getExecuteStep())
                .initTime(cacheTask.getInitTime())
                .loader(cacheTask.getLoader().getClass())
                .history(cacheTask.getHistory())
                .transfer(cacheTask.getTransfer())
                .isHistory(cacheTask.getIsHistory())
                .origin(cacheTask.getOrigin()).build();
        return depositTask;
    }

    @Override
    public void run() {
        asyncChannelProcess = AsyncChannelProcess.<CacheTask>newBuilder()
                .setAddBlockTimeout(24, TimeUnit.HOURS)
                .setMaxProcessCount(maxProcessCount)
                .setProcess((Function<CacheTask, Boolean>) msg -> {
                    saveImpl(msg);
                    return true;
                })
                .build();

    }
}
