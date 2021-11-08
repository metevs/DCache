package com.metevs.dcache.loader.cache;

import com.metevs.dcache.facade.domain.BaseDO;

/**
 * 一级缓存（key value结构）
 */
public interface Level1Cache<T extends BaseDO> extends Cache<T>{

    /**
     *
     * @param priKey
     * @return
     */
    T getCacheObject(String priKey);

    /**
     *
     * @param priKey
     * @param t
     * @return
     */
    Boolean storeCacheObject(String priKey,T t);

    /**
     *
     * @param priKey
     * @param t
     * @return
     */
    Boolean invaildCacheObject(String priKey,T t);

    void clearData() ;
}
