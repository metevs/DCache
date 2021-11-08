package com.metevs.dcache.facade.sdk;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.fastjson.JSON;
import com.metevs.dcache.facade.domain.SortOrder;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.util.QueryType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * file InvokeDemo.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/3
 */
public class InvokeDemo {

    @Test
    public void test () {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("demo-consumer");

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("192.168.110.11:2181");
        registryConfig.setProtocol("zookeeper");

        ReferenceConfig<DrivingRecordHistoryCacheService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registryConfig);
        reference.setVersion("1.0.0");
        reference.setTimeout(30000);
        reference.setInterface(DrivingRecordHistoryCacheService.class);

        DrivingRecordHistoryCacheService drivingRecordHistoryCacheService = reference.get();
        DCacheTeamQueryParam drTeamQueryParam = DCacheTeamQueryParam.newDrivingHistoryBuilder()
                .setMerchantId(33010001L)
                .appendExecDates(QueryType.CompareType.BT, Arrays.asList(
                        LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 3)
                ))
                .sortFind(SortOrder.ASC, DrivingRecordHistoryField.DISPATCH_ARRIVE_TIME)
                .build();
        List<DrivingRecordHistoryCache> drivingRecordCaches = drivingRecordHistoryCacheService.findByTeamQuery(drTeamQueryParam);
        System.out.println("driving records is :{}" + JSON.toJSONString(drivingRecordCaches));
    }

}
