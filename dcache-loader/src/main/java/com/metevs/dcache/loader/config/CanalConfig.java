package com.metevs.dcache.loader.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * canal配置
 */
@Configuration
@ConfigurationProperties(prefix = "canal")
@Data
public class CanalConfig {

    private String watchDbs;

    private String watchTables;

    private String watchTypes;

    private String isDDl;

    private String tableMappers;
}
