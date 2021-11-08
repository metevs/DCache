package com.metevs.dcache.loader.hanlde;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.loader.cache.Level1Cache;
import com.metevs.dcache.facade.domain.BaseDO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 */
@Slf4j
public abstract class RedisCacheHandle<T extends BaseDO> implements Level1Cache<T> {

    @Getter
    protected Class aClass = null;

    public Class supportType() {
        if (aClass == null) {
            Type tp = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            aClass = (Class<T>) tp;
            return aClass;
        }
        return aClass;
    }

    private RedisTemplate redisTemplate;

    public RedisCacheHandle(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @return
     */
    public abstract String topic();


    /**
     * @param priKey
     * @return
     */
    @Override
    public T getCacheObject(String priKey) {
        return (T) JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(topic(), priKey)), supportType());
    }


    /**
     * @param priKey
     * @param o
     * @return
     */
    @Override
    public Boolean storeCacheObject(String priKey, T o) {
        redisTemplate.opsForHash().put(topic(), priKey, JSON.toJSONString(o));
        return true;
    }


    public Boolean storeHashObject(String hashKey, String priKey, String json) {
        redisTemplate.opsForHash().put(hashKey, priKey, json);
        return true;
    }

    public String getHashObject(String hashKey, String priKey) {
        Object obj = redisTemplate.opsForHash().get(hashKey, priKey);
        return obj == null ? null : JSON.toJSONString(obj);
    }


    @Override
    public Boolean invaildCacheObject(String priKey, T t) {
        redisTemplate.opsForHash().delete(topic(), priKey);
        return true;
    }

    @Override
    public void clearData() {
        redisTemplate.delete(topic());
    }


    public void clearCache(String hashkey) {
        redisTemplate.delete(hashkey);
    }
}
