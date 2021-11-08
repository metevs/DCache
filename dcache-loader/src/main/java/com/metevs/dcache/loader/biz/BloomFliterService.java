package com.metevs.dcache.loader.biz;


import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.loader.runner.AbstractLoader;
import com.metevs.dcache.loader.util.CustomBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BloomFliterService implements FilterService {

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();


    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }


    @Override
    public void addBloomFilter(Long param, AbstractLoader abstractLoader, Type type) {
        if (param == null) return;
        LoaderFilterProxy loaderFilterProxy = loaderFilterProxies.get(abstractLoader + type.toString());
        if (loaderFilterProxy != null) loaderFilterProxy.customBloomFilter.add(param);
    }

    @Override
    public boolean containsObject(Long param, AbstractLoader abstractLoader, Type type) {
        LoaderFilterProxy loaderFilterProxy = loaderFilterProxies.get(abstractLoader + type.toString());
        if (loaderFilterProxy != null) {
            if (loaderFilterProxy.customBloomFilter.contain(param)) {
                return true;
            } else {
                counter.ifPresent(service -> service.increment(Counter.QUERY, Counter.BLOOM_FILTER));
                return false;
            }
        }
        return true;
    }


    static enum Type {
        ROUTE,
        BUS,
        EMP
    }

    private Map<String, LoaderFilterProxy> loaderFilterProxies = new HashMap<>();

    public void register(AbstractLoader abstractLoader, Type type, RedisTemplate redisTemplate) {
        LoaderFilterProxy loaderFilterProxy = new LoaderFilterProxy(abstractLoader, type, redisTemplate);
        loaderFilterProxies.put(loaderFilterProxy.toString(), loaderFilterProxy);
    }

    static class LoaderFilterProxy {
        private AbstractLoader abstractLoader;
        private CustomBloomFilter customBloomFilter;
        private String singleName;
        Type type;

        public LoaderFilterProxy(AbstractLoader abstractLoader, Type type, RedisTemplate redisTemplate) {
            this.abstractLoader = abstractLoader;
            singleName = "Bloom_"+abstractLoader.getClass().getSimpleName() + "_" + type;
            this.customBloomFilter = new CustomBloomFilter(singleName, redisTemplate);
            this.type = type;
        }

        @Override
        public String toString() {
            return abstractLoader + type.toString();
        }
    }

}
