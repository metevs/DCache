package com.metevs.dcache.facade.sdk;

import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.sdk.facade.AbstractCacheLoader;
import com.metevs.dcache.facade.service.LendRecordCacheService;

import java.util.List;

/**
 * file CacheLoaderFacade.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/7
 */
public class CacheLoaderFacade extends AbstractCacheLoader {

    private CacheLoaderFacade() {
    }

    public CacheLoaderFacade(LendRecordCacheService lendRecordCacheService) {
        super(lendRecordCacheService);
    }

    @Override
    public List<LendRecordCache> queryLendRecordCache(DCacheTeamQueryParam drTeamQueryParam) {
        return lendRecordCacheService.findLendRecordTeamQuery(drTeamQueryParam);
    }

    @Override
    public <T extends BaseDO> Boolean increase(T t) {

        if (t instanceof LendRecordCache) {
            throw new IllegalArgumentException("lend record cache no increase method, plz check your argument");
        }

        throw new IllegalArgumentException("no detail cache detected, plz check your argument");
    }

    @Override
    public <T extends BaseDO> Boolean syncIncrease(T t) {
        if (t instanceof LendRecordCache) {
            throw new IllegalArgumentException("lend record cache no increase method, plz check your argument");
        }
        throw new IllegalArgumentException("no detail cache detected, plz check your argument");
    }

    @Override
    public <T extends BaseDO> Long updateCacheById(T t) {
        if (t instanceof LendRecordCache) {
            LendRecordCache lendRecordCache = (LendRecordCache) t;
            return lendRecordCacheService.updateCacheRecordById(lendRecordCache);
        }
        throw new IllegalArgumentException("no detail cache detected, plz check your argument");
    }

    @Override
    public <T extends BaseDO> Long syncUpdateCacheById(T t) {
        if (t instanceof LendRecordCache) {
            LendRecordCache lendRecordCache = (LendRecordCache) t;
            return lendRecordCacheService.syncUpdateCacheRecordById(lendRecordCache);
        }
        throw new IllegalArgumentException("no detail cache detected, plz check your argument");
    }
}
