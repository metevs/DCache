package com.metevs.dcache.loader.config;


import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@DubboComponentScan(basePackages = {"com.ibuscloud.dtp.access.loader.providers"})
public class DubboConfig {

    @Autowired
    private CuratorConfig curatorConfig;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("data-loader");
        return applicationConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(curatorConfig.getHost());
        registryConfig.setProtocol("zookeeper");
//        if (applicationContext.getEnvironment().getActiveProfiles()[0].equals("local")) {
//            registryConfig.setRegister(false);
//        } else {
            registryConfig.setRegister(true);
//        }

        return registryConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(3000);
        return consumerConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPayload(883886080);
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setDispatcher("message");
        protocolConfig.setThreads(300);
        protocolConfig.setQueues(300);
        return protocolConfig;
    }

}
