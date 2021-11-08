package com.metevs.dcache.loader.cache;

public interface Future {
    void success();
    void failed();
    void timeOut();
}
