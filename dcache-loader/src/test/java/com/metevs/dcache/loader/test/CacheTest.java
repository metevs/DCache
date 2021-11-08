package com.metevs.dcache.loader.test;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.facade.domain.LendRecordCache;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.service.LendRecordCacheService;
import com.metevs.dcache.facade.util.QueryType;
import com.metevs.dcache.loader.DataLoaderApplication;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest(classes = DataLoaderApplication.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class CacheTest {

    @Autowired
    private TransportClient client;

     @Autowired
    private LendRecordCacheService lendRecordCacheService;



    public void clearIndex() {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchAllQuery())
                        .source("metevs")
                        .get();
        response.getDeleted();
    }

    public Boolean isIndexExist(String indexName) {
        Boolean flag = false;
        try {
            IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indexName);
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(indicesExistsRequest).actionGet();
            if (indicesExistsResponse.isExists()) {
                flag = true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }



    @Test
    public void testLendRecord () {
        DCacheTeamQueryParam drTeamQueryParam = DCacheTeamQueryParam.newLendRecordParamBuilder()
                .setCityCode("330100")
                .appendModifiedTime(QueryType.CompareType.GT, LocalDateTime.of(2020, 3, 4, 0, 0, 0))
                .build();
        List<LendRecordCache> caches = lendRecordCacheService.findLendRecordTeamQuery(drTeamQueryParam);
        System.out.println("lend record json is :" + JSON.toJSONString(caches));
    }


}
