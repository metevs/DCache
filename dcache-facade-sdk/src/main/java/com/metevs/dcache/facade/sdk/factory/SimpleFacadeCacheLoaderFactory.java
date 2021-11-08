package com.metevs.dcache.facade.sdk.factory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.metevs.dcache.facade.sdk.CacheLoaderFacade;
import com.metevs.dcache.facade.sdk.facade.AbstractCacheLoader;
import com.metevs.dcache.facade.sdk.params.FacadeParams;
import com.metevs.dcache.facade.service.LendRecordCacheService;

/**
 * file DubboFacadeCacheLoaderFactory.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/4/8
 */
public class SimpleFacadeCacheLoaderFactory extends AbstractFacadeFactory {

    private static class DubboFacadeCacheLoaderFactoryHolder {
        private static final SimpleFacadeCacheLoaderFactory INSTANCE = new SimpleFacadeCacheLoaderFactory();
    }

    private SimpleFacadeCacheLoaderFactory() {
    }

    public static final CacheLoaderFacade getInstance (FacadeParams facadeParams) {
        return (CacheLoaderFacade) DubboFacadeCacheLoaderFactoryHolder.INSTANCE.getCacheLoader(facadeParams);
    }

    @Override
    protected AbstractCacheLoader getCacheLoader(FacadeParams facadeParams) {
        if (this.cacheLoader == null) {
            ApplicationConfig application = new ApplicationConfig();
            application.setName((String) notNull(facadeParams.getApplicationName(), "应用名不能为空"));

            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress((String) notNull(facadeParams.getZkHost(), "zkHost不能为空"));
            registryConfig.setProtocol((String) notNull(facadeParams.getProtocol(), "protocol不能为空"));

            ReferenceConfig<LendRecordCacheService> lendRecordCacheServiceReferenceConfig = new ReferenceConfig<>();
            lendRecordCacheServiceReferenceConfig.setApplication(application);
            lendRecordCacheServiceReferenceConfig.setRegistry(registryConfig);
            lendRecordCacheServiceReferenceConfig.setVersion((String) notNull(facadeParams.getFacadeVersion(), "Dubbo版本号不能为空"));
            lendRecordCacheServiceReferenceConfig.setTimeout((Integer) notNull(facadeParams.getTimeout(), "超时时间不能为空"));
            lendRecordCacheServiceReferenceConfig.setInterface(LendRecordCacheService.class);
            LendRecordCacheService lendRecordCacheService = lendRecordCacheServiceReferenceConfig.get();

            this.cacheLoader = new CacheLoaderFacade(lendRecordCacheService);
            return this.cacheLoader;
        }
        return this.cacheLoader;
    }


    private Object notNull (Object object, String msg) {
        if (object == null) throw new IllegalArgumentException(msg);
        return object;
    }


}
