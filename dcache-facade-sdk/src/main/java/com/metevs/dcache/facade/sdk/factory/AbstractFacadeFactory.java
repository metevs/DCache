package com.metevs.dcache.facade.sdk.factory;

import com.metevs.dcache.facade.sdk.facade.AbstractCacheLoader;
import com.metevs.dcache.facade.sdk.params.FacadeParams;

/**
 * file AbstractFacadeFactory.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/8
 */
public abstract class AbstractFacadeFactory {

    protected AbstractCacheLoader cacheLoader = null;

    protected abstract AbstractCacheLoader getCacheLoader (FacadeParams facadeParams);

}
