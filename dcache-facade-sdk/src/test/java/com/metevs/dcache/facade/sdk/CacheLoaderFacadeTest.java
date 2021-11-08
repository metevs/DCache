package com.metevs.dcache.facade.sdk;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.facade.domain.SortOrder;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.sdk.factory.SimpleFacadeCacheLoaderFactory;
import com.metevs.dcache.facade.sdk.params.FacadeParams;
import org.junit.Test;

import java.util.List;

/**
 * file CacheLoaderFacadeTest.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/7
 */
public class CacheLoaderFacadeTest {

    @Test
    public void testQueryDrivingRecord () {
        FacadeParams facadeParams = new FacadeParams();
        facadeParams.setApplicationName("your-application-name")
                .setFacadeVersion("1.0.0")
                .setProtocol("zookeeper")
                .setZkHost("192.168.110.11:2181")
                .setTimeout(10000);
        CacheLoaderFacade cacheLoaderFacade = SimpleFacadeCacheLoaderFactory.getInstance(facadeParams);

        List<DrivingRecordCache> drivingRecordCaches = cacheLoaderFacade.queryDrivingRecordCache(DCacheTeamQueryParam.newDrivingRecordBuilder()
                .setCityCode("330100")
                .sortFind(SortOrder.ASC, DrivingRecordField.DISPATCH_ARRIVE_TIME)
                .build());

        //...
        System.out.println("driving record caches {} :" + JSON.toJSONString(drivingRecordCaches));
    }


}