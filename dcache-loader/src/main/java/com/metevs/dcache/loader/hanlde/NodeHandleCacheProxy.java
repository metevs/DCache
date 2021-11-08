package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.loader.cache.Cache;
import com.metevs.dcache.loader.cache.CacheTask;
import com.metevs.dcache.facade.domain.BaseDO;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T>
 */
@Slf4j
public class NodeHandleCacheProxy<T extends BaseDO> extends NodeHandle implements Cache<T> {

    private Cache<T> cache;


    public NodeHandleCacheProxy(Cache<T> cache) {
        this.cache = cache;
    }


    /**
     * @return
     */
    private Boolean inCurrentNode(CacheTask<T> cacheTask) {
        if ((cacheTask.getExecuteStep() & this.getPosition()) == 0) {
            if (this.getNext() == null) {
                throw new RuntimeException("cache task un finished..");
            } else {
                return false;
            }
        }
        return true;
    }


    @Override
    public Boolean invalidData(CacheTask<T> cacheTask) {
        if (!inCurrentNode(cacheTask)) {
            return ((NodeHandleCacheProxy) (this.getNext())).invalidData(cacheTask);
        }

        cache.invalidData(cacheTask);
        cacheTask.setExecuteStep(cacheTask.getExecuteStep() << 1);
        if (this.getNext() != null) {
            return ((NodeHandleCacheProxy) (this.getNext())).invalidData(cacheTask);
        }
        return true;
    }

    @Override
    public void clearData() {
        log.info("cache {} clear cache", this.cache);
        cache.clearData();
        if (this.getNext() != null) {
            ((NodeHandleCacheProxy) (this.getNext())).clearData();
        }
    }

    @Override
    public Boolean increaseData(CacheTask<T> cacheTask) {
        if (!inCurrentNode(cacheTask)) {
            return ((NodeHandleCacheProxy) (this.getNext())).increaseData(cacheTask);
        }

        cache.increaseData(cacheTask);
        cacheTask.setExecuteStep(cacheTask.getExecuteStep() << 1);
        if (this.getNext() != null) {
            return ((NodeHandleCacheProxy) (this.getNext())).increaseData(cacheTask);
        }
        return true;
    }

    @Override
    public Boolean modifyData(CacheTask<T> cacheTask) {
        if (!inCurrentNode(cacheTask)) {
            return ((NodeHandleCacheProxy) (this.getNext())).modifyData(cacheTask);
        }
        if (!cache.modifyData(cacheTask)){
            throw new RuntimeException("modify failed");
        }
        cacheTask.setExecuteStep(cacheTask.getExecuteStep() << 1);
        if (this.getNext() != null) {
            return ((NodeHandleCacheProxy) (this.getNext())).modifyData(cacheTask);
        }
        return true;
    }

    @Override
    public Boolean loadData(CacheTask<T> cacheTask) {
        if (!inCurrentNode(cacheTask)) {
            return ((NodeHandleCacheProxy) (this.getNext())).loadData(cacheTask);
        }
        if (cacheTask.getIsHistory()) {
            cache.modifyData(cacheTask);
        } else {
            cache.increaseData(cacheTask);
        }
        cacheTask.setExecuteStep(cacheTask.getExecuteStep() << 1);
        if (this.getNext() != null) {
            return ((NodeHandleCacheProxy) (this.getNext())).loadData(cacheTask);
        }

        return true;
    }
}
