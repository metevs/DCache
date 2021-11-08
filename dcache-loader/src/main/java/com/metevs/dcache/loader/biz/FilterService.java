package com.metevs.dcache.loader.biz;

import com.metevs.dcache.loader.runner.AbstractLoader;

/**
 *
 */
public interface FilterService {

    void addBloomFilter(Long param, AbstractLoader abstractLoader, BloomFliterService.Type type);

    boolean containsObject(Long param, AbstractLoader abstractLoader, BloomFliterService.Type type);

}
