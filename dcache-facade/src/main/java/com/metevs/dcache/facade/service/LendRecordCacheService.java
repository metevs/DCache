package com.metevs.dcache.facade.service;

import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;

import java.util.List;

/**
 * file LendRecordCacheService.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/26
 */
public interface LendRecordCacheService {

    Long updateCacheRecordById(LendRecordCache lendRecordCache);

    Long syncUpdateCacheRecordById(LendRecordCache lendRecordCache);

    List<LendRecordCache> findLendRecordTeamQuery(DCacheTeamQueryParam drTeamQueryParam);

}
