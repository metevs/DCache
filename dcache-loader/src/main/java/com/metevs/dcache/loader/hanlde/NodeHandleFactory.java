package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.loader.cache.Cache;
import com.metevs.dcache.facade.domain.BaseDO;
import org.springframework.stereotype.Component;

@Component
public class NodeHandleFactory {

    /**
     *
     * @param cache
     * @param <T>
     * @return
     */
    public <T extends BaseDO> NodeHandleCacheProxy<T> createHandle(Cache<T> cache) {
        return new NodeHandleCacheProxy<>(cache);
    }
}
