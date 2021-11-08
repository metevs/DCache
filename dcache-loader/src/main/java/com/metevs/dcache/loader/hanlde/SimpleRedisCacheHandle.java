package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.facade.domain.BaseDO;
import org.springframework.data.redis.core.RedisTemplate;

public class SimpleRedisCacheHandle<T extends BaseDO> extends RedisCacheHandle<T> {

    private String topic;

    public SimpleRedisCacheHandle(RedisTemplate redisTemplate, String topic) {
        super(redisTemplate);
        this.topic = topic;
    }

    public SimpleRedisCacheHandle(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public String topic() {
        return topic;
    }


    @Override
    public Boolean invalidData(CacheTask<T> task) {
        return null;
    }

    @Override
    public Boolean increaseData(CacheTask<T> task) {
        return null;
    }

    @Override
    public Boolean modifyData(CacheTask<T> task) {
        return null;
    }

    @Override
    public Boolean loadData(CacheTask<T> task) {
        return null;
    }
}
