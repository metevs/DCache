package com.metevs.dcache.loader.hanlde;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metevs.dcache.loader.cache.CacheTask;

import com.metevs.dcache.facade.domain.BaseDO;


public class SimpleMysqlHandle<T extends BaseDO> extends MysqlHandle<T> {

    public SimpleMysqlHandle(BaseMapper<T> mapper) {
        super(mapper);
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
