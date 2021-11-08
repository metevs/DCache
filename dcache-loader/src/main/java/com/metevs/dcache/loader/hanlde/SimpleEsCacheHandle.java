package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.facade.domain.BaseDO;
import org.elasticsearch.client.transport.TransportClient;

import java.util.List;

public class SimpleEsCacheHandle<T extends BaseDO> extends EsCacheHandle<T> {

    public SimpleEsCacheHandle(TransportClient transportClient) {
        super(transportClient);
    }

    @Override
    public List getCacheObjectList(Object o) {
        return null;
    }

    @Override
    public Boolean storeCacheObject(T t) {
        return null;
    }


    @Override
    public Boolean invalidData(CacheTask<T> task) {
        return null;
    }

    @Override
    public void clearData() {

    }

    @Override
    public Boolean increaseData(CacheTask<T> task) {
        return null;
    }

    @Override
    public Boolean modifyData(CacheTask<T> task) {
        return null;
    }

    @Override
    public Boolean loadData(CacheTask<T> task) {
        return null;
    }
}
