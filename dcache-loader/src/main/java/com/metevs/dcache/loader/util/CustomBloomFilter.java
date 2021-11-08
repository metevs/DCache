package com.metevs.dcache.loader.util;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 定制的过滤器
 */
public class CustomBloomFilter {

    /**
     * 生效时间，用于延迟生效
     */
    private Map<Long, Integer> localMap;
    private RedisTemplate redisTemplate;
    private String hashKey;


    public CustomBloomFilter(String hashKey, RedisTemplate redisTemplate) {
        this.hashKey = hashKey;
        localMap = new HashMap<>();
        this.redisTemplate = redisTemplate;
    }

    /**
     * 新增参数
     *
     * @param param
     */
    public void add(Long param) {
        if (localMap.containsKey(param)) {
            return;
        }
        redisTemplate.opsForHash().put(hashKey, String.valueOf(param), "1");
        localMap.put(param, 1);
    }

    public boolean contain(Long param) {
        if (localMap.containsKey(String.valueOf(param))) {
            return true;
        }
        if (redisTemplate.opsForHash().hasKey(hashKey, String.valueOf(param))) {
            localMap.put(param, 1);
            return true;
        }
        return false;
    }
}
