package com.metevs.dcache.loader.providers;

import com.metevs.dcache.loader.constants.Constants;
import com.metevs.dcache.loader.count.Counter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @Author: looya
 * @Date: 2020/11/5/005 13:50
 * @Description:
 */
public abstract class AbstractPerformanceHandle {

    /**
     * 计数器服务.
     */
    protected Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }

    protected <T> T poTime(Long startTime, T t) {
        long time = System.currentTimeMillis() - startTime;
        if (time < 10) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_10));
            return t;
        }

        if (time < 100) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_100));
            return t;
        }

        if (time < 600) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_600));
            return t;
        }


        if (time < 1000) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_1000));
            return t;
        }

        if (time < 3000) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_3000));
            return t;
        }

        if (time < 10000) {
            counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                    this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_10000));
            return t;
        }

        counter.ifPresent(service -> service.increment(Counter.DUBBO_TOTAL,
                this.getClass().getSimpleName() + Constants.SPACE + Counter.DUBBO_Performance_more));
        return t;
    }


}
