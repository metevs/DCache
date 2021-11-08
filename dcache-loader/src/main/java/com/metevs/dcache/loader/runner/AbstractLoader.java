package com.metevs.dcache.loader.runner;

import com.ibuscloud.dtp.access.loader.cache.*;
import com.metevs.dcache.loader.cache.Cache;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.loader.constants.TVMsg;
import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.loader.hanlde.EventLoopExecutor;
import com.metevs.dcache.loader.hanlde.NodeHandleCacheProxy;
import com.metevs.dcache.loader.hanlde.NodeHandleChain;
import com.metevs.dcache.loader.hanlde.NodeHandleFactory;
import com.metevs.dcache.loader.util.ActionType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
@Slf4j
public abstract class AbstractLoader<T extends BaseDO> implements LoaderInitialize {

    protected Map<ActionType, NodeHandleChain<T>> map;

    public AbstractLoader() {
        nodeIncreaseHandles = new NodeHandleChain<>(this, ActionType.INCREASE);
        nodeModifyHandles = new NodeHandleChain<>(this, ActionType.MODIFY);
        nodeLoadHandles = new NodeHandleChain<>(this, ActionType.LOAD, ActionType.INVALID);
        map = initMap(nodeIncreaseHandles, nodeModifyHandles, nodeLoadHandles);
    }

    private Map<ActionType, NodeHandleChain<T>> initMap(NodeHandleChain<T> nodeIncreaseHandles,
                                                        NodeHandleChain<T> nodeModifyHandles, NodeHandleChain<T> nodeLoadHandles) {
        Map<ActionType, NodeHandleChain<T>> map = new HashMap<>();
        chainToMap(map, nodeIncreaseHandles);
        chainToMap(map, nodeModifyHandles);
        chainToMap(map, nodeLoadHandles);
        return map;
    }

    private void chainToMap(Map<ActionType, NodeHandleChain<T>> map, NodeHandleChain<T> nodeHandleChain) {
        if (nodeHandleChain != null && nodeHandleChain.getActionTypes() != null) {
            for (ActionType actionType : nodeHandleChain.getActionTypes()) {
                map.put(actionType, nodeHandleChain);
            }
        }
    }

    public abstract void cacheAfterSet(CacheTask cacheTask, Boolean bool);

    public abstract AbstractLoader<T> registerExecutor(EventLoopExecutor eventLoopExecutor);

    public abstract Boolean consumerMsg(TVMsg msg);

    public abstract Boolean isFindHistory(CacheTask cacheTask);

    public NodeHandleChain<T> nodeIncreaseHandles;

    public NodeHandleChain<T> nodeModifyHandles;

    public NodeHandleChain<T> nodeLoadHandles;

    protected NodeHandleCacheProxy<T> newNode(Cache<T> cache, NodeHandleFactory nodeHandleFactory) {
        return nodeHandleFactory.createHandle(cache);
    }

    @Getter
    protected Class aClass = null;

    public Class supportType() {
        if (aClass == null) {
            Type tp = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            aClass = (Class<T>) tp;
            return aClass;
        }
        return aClass;
    }


    public void addLastIncreaseHandle(NodeHandleCacheProxy<T> nodeHandle) {
        nodeIncreaseHandles.addLastNodeHandle(nodeHandle);
    }

    public void addLastLoadHandle(NodeHandleCacheProxy<T> nodeHandle) {

        nodeLoadHandles.addLastNodeHandle(nodeHandle);
    }

    public void addLastModifyHandle(NodeHandleCacheProxy<T> nodeHandle) {
        nodeModifyHandles.addLastNodeHandle(nodeHandle);
    }


    /**
     *
     */
    public abstract void initHandle();


    /**
     * @param start
     * @param end
     * @return
     */
    public Boolean riseDataLoad(LocalDateTime start, LocalDateTime end) {
        return null;
    }

    public Boolean normalDataLoad() {
        return null;
    }

    public Boolean scheduleCheckDataLoad() {
        return null;
    }

    public Boolean invalidDataLoad() {
        return null;
    }


    /**
     * @param
     */
    public Boolean modifyMsg(CacheTask<T> t) {
        return map.get(ActionType.MODIFY).getHead().modifyData(t);
    }

    /**
     * @param
     */
    public Boolean invalidMsg(CacheTask<T> t) {
        return map.get(ActionType.INVALID).getHead().invalidData(t);
    }

    /**
     * @param
     */
    public Boolean loadMsg(CacheTask<T> t) {
        return map.get(ActionType.LOAD).getHead().loadData(t);
    }

    /**
     * @return
     */
    public Boolean clearData() {
        if (map.get(ActionType.LOAD) != null && map.get(ActionType.LOAD).getHead() != null)
            map.get(ActionType.LOAD).getHead().clearData();
        return true;
    }

    /**
     *
     */
    public Boolean increaseMsg(CacheTask<T> t) {
        return map.get(ActionType.INCREASE).getHead().increaseData(t);
    }


    public abstract void customBloomFilter(String value);

    public abstract Boolean tryLock(CacheTask cacheTask);

    public abstract Boolean unLock(CacheTask cacheTask);
}
