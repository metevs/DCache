package com.metevs.dcache.loader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * zk配置类
 */
@Configuration
@EnableConfigurationProperties(CuratorConfig.class)
@ConfigurationProperties(prefix = "zk")
@Data
public class CuratorConfig {

    private String host;

    /*@Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new RetryNTimes(3, 1000);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(host)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(10000)
                .connectionTimeoutMs(3000)
                .build();
        client.start();
        return client;
    }

    @Bean
    public ZkClientTemplate zkClientTemplate() {
        return new ZkClientTemplate();
    }*/
}
