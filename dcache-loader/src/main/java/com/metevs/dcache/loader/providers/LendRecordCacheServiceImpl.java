package com.metevs.dcache.loader.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.metevs.dcache.loader.biz.LendRecordLoader;
import com.metevs.dcache.loader.constants.SinkModel;
import com.metevs.dcache.loader.constants.TVMsg;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.query.EqualQueryBuilder;
import com.metevs.dcache.facade.query.LendRecordField;
import com.metevs.dcache.facade.service.LendRecordCacheService;
import com.metevs.dcache.loader.runner.LoaderRunner;
import com.metevs.dcache.loader.util.AsyncChannelProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * file LendRecordCacheServiceImpl.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/28
 */
@Service(version = "1.0.0")
public class LendRecordCacheServiceImpl extends AbstractPerformanceHandle implements LendRecordCacheService {

    @Autowired
    private LendRecordLoader lendRecordLoader;

    @Resource
    @Qualifier("asyncChannelProcess")
    private AsyncChannelProcess<TVMsg> asyncChannelProcess;

    @Override
    public List<LendRecordCache> findLendRecordTeamQuery(DCacheTeamQueryParam drTeamQueryParam) {
        Long startTime = System.currentTimeMillis();
        counter.ifPresent(service -> service.increment(Counter.QUERY, Counter.QUERY_LR));
        //优先判断是否命中redis
        if (!CollectionUtils.isEmpty(drTeamQueryParam.getEqualQueryBuilders()) && drTeamQueryParam.getEqualQueryBuilders().size() == 1
                && CollectionUtils.isEmpty(drTeamQueryParam.getRangeQueryBuilders())
                && CollectionUtils.isEmpty(drTeamQueryParam.getMustQueryBuilders())) {
            EqualQueryBuilder equalQueryBuilder = drTeamQueryParam.getEqualQueryBuilders().get(0);
            if (equalQueryBuilder.getFieldName().equals(LendRecordField.ID.name())) {
                return Arrays.asList(lendRecordLoader.findRecordByPriId((Long) equalQueryBuilder.getT()));
            }
        }

        return poTime(startTime, lendRecordLoader.findDrTeamQuerys(drTeamQueryParam));
    }

    @Override
    public Long updateCacheRecordById(LendRecordCache lendRecordCache) {
        counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.MODIFY_SUCCESS));
        asyncChannelProcess.add(new TVMsg(SinkModel.MODIFIED, lendRecordCache), LoaderRunner.crt.incrementAndGet());
        return lendRecordCache.getId();
    }

    @Override
    public Long syncUpdateCacheRecordById(LendRecordCache lendRecordCache) {
        counter.ifPresent(service -> service.increment(Counter.TOTAL_KEY, Counter.MODIFY_SUCCESS));
        lendRecordLoader.consumerMsg(new TVMsg(SinkModel.MODIFIED, lendRecordCache, true));
        return lendRecordCache.getId();
    }
}
