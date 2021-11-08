package com.metevs.dcache.loader.hanlde;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metevs.dcache.loader.cache.Cache;

import com.metevs.dcache.facade.domain.BaseDO;

/**
 * @param
 */
public abstract class MysqlHandle<T extends BaseDO> implements Cache<T> {

    protected BaseMapper<T> mapper;

    public MysqlHandle(BaseMapper<T> mapper) {
        this.mapper = mapper;

    }
}
