package com.metevs.dcache.loader.hanlde;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.metevs.dcache.loader.cache.Level2Cache;
import com.metevs.dcache.loader.constants.Constants;
import com.metevs.dcache.facade.domain.BaseDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
public abstract class EsCacheHandle<T extends BaseDO> implements Level2Cache<T> {

    //比较重，缓存起来
    protected static LoadingCache<String, Boolean> INDEX_EXIST_CACHE = null;

    private TransportClient client;

    public EsCacheHandle(TransportClient transportClient) {
        this.client = transportClient;
        INDEX_EXIST_CACHE = CacheBuilder.newBuilder()
                .maximumSize(10000000)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build(new CacheLoader<String, Boolean>() {

                    @Override
                    public Boolean load(String s) throws Exception {
                        String[] args = s.split(Constants.SPLIT);
                        return true; //TODO isIndexDocumentExit(args[0], args[1], args[2]);
                    }
                });
    }


    /**
     *
     * @param index
     * @param document
     * @param key
     * @return
     */
    public Boolean isIndexDocumentExitCache(String index, String document, String key) {
        Boolean bool = INDEX_EXIST_CACHE.getUnchecked(Constants.joinKey(index, document, key));
        if (bool == null) {
            return false;
        }
        return bool;
    }


    //创建索引库
    public void createIndexResponse(String indexName, String type, String jsonData, String id) {
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, type);
        requestBuilder
                .setId(id)
                .setSource(jsonData)
                .execute().actionGet();
    }

    public void clearIndex(String indexName) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchAllQuery())
                        .source(indexName)
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


    public boolean updateIndex(String indexName, String type, String json, String id) {
        boolean flag = false;
        try {
            UpdateRequest uRequest = new UpdateRequest();
            uRequest.index(indexName);
            uRequest.type(type);
            uRequest.id(id);
            uRequest.doc(json);
            client.update(uRequest).get();
            flag = true;
        } catch (InterruptedException e) {
            log.error("updateIndex failed InterruptedException {}", json, e);
        } catch (ExecutionException e) {
            log.error("updateIndex failed ExecutionException {}", json, e);
        }
        return flag;
    }

    //判断符合条件的索引是否存在
    public Boolean isIndexDocumentExit(String indexName, String type, String key) {
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.existsQuery(key));
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(type);

            searchRequestBuilder.setQuery(boolQueryBuilder);
            SearchResponse searchResponse = searchRequestBuilder.setSize(1).execute().get();
            return searchResponse.getHits().totalHits > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //删除索引
    public Boolean deleteIndex(String indexName, String type, String key, String value) {

        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery(key, value));

            BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(type);

            searchRequestBuilder.setQuery(boolQueryBuilder);
            SearchResponse searchResponse = searchRequestBuilder.execute().get();
            for (SearchHit hit : searchResponse.getHits()) {
                String id = hit.getId();
                bulkRequestBuilder.add(client.prepareDelete(indexName, type, id));
            }
            if (bulkRequestBuilder.numberOfActions() > 0) {
                BulkResponse responses = bulkRequestBuilder.get();
                if (responses.hasFailures()) {
                    for (BulkItemResponse response : responses.getItems()) {
                        log.error(response.getFailureMessage());
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param queryBuilder
     * @param indexName
     * @param type
     * @param sort
     * @return
     */
    public List<T> searchObj(QueryBuilder queryBuilder, String indexName, String type, String sort, SortOrder sortOrder) {
        List<T> list = new ArrayList<>();
        Type tp = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setSize(10000).setTypes(type)
                .setQuery(queryBuilder);
        if (sortOrder != null) {
            searchRequestBuilder.addSort(SortBuilders.fieldSort(sort).order(sortOrder));
        }
        SearchResponse searchResponse =
                searchRequestBuilder
                        .execute()
                        .actionGet();
        if (null != searchResponse) {
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHists = hits.getHits();
            for (SearchHit hit : searchHists) {
                String obj = hit.getSourceAsString();
                T t = JSONObject.parseObject(obj, (Class<T>) tp);
                list.add(t);
            }
        }
        return list;
    }


    /**
     * @param t
     * @return
     */
    public String objToEsJson(T t) {
        return null;
    }

}
