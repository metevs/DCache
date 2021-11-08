package com.metevs.dcache.facade.sdk.facade;

import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.service.LendRecordCacheService;

import java.util.List;

/**
 * file AbstractCacheLoader.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/7
 */
public abstract class AbstractCacheLoader {

     protected LendRecordCacheService lendRecordCacheService = null;

    public AbstractCacheLoader() {
    }

    public AbstractCacheLoader(LendRecordCacheService lendRecordCacheService) {
        this.lendRecordCacheService = lendRecordCacheService;
    }

    public abstract List<LendRecordCache> queryLendRecordCache(DCacheTeamQueryParam drTeamQueryParam);

    public abstract <T extends BaseDO>  Boolean increase (T t);

    public abstract <T extends BaseDO> Boolean syncIncrease (T t);

    public abstract <T extends BaseDO> Long updateCacheById (T t);

    public abstract <T extends BaseDO> Long syncUpdateCacheById (T t);

}
