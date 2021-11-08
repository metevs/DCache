package com.metevs.dcache.facade.domain;

import java.io.Serializable;

/**
 *
 */
public abstract class BaseDO implements Serializable {
    //用于任务hash取模
    public abstract Long getChannel();
}
