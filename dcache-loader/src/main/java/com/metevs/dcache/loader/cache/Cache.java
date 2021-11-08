package com.metevs.dcache.loader.cache;

import com.metevs.dcache.facade.domain.BaseDO;

/**
 *
 * @param <T>
 */
public interface Cache<T extends BaseDO> {

    /**
     *
     * @param task
     * @return
     */
    Boolean invalidData(CacheTask<T> task);

    /**
     *
     */
    void clearData();

    /**
     *
     * @param task
     * @return
     */
    Boolean increaseData(CacheTask<T> task);

    /**
     *
     * @param task
     * @return
     */
    Boolean modifyData(CacheTask<T> task);

    /**
     *
     * @param task
     * @return
     */
    Boolean loadData(CacheTask<T> task);

}
