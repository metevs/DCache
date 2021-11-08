package com.metevs.dcache.loader.config;

import jodd.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 */
@EnableConfigurationProperties(RedisConfig.class)
@ConfigurationProperties(prefix = "redis")
@Configuration
@Data
@Slf4j
public class RedisConfig {
    private String host;
    private int port;
    private int database;
    private int timeout;
    private int maxPool;
    private String password;
    private String masterAddress;
    private String slaveAddress;

    @Value("${spring.profiles.active}")
    private String serverEnv;


    @Bean(name = "loaderRedisConnectionFactory")
    public RedisConnectionFactory predictRedisConnectionFactory() {

        if (StringUtil.isBlank(this.host)) {
            log.info("No need to initialize redis.");
            return null;
        }

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(this.host);
        factory.setPort(this.port);
        factory.setDatabase(this.database);
        factory.setTimeout(this.timeout);
        factory.setUsePool(true);
        if (StringUtil.isNotBlank(this.password)) {
            factory.setPassword(this.password);
            log.info("loaderRedisConnectionFactory setPassword {}", this.password);
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.maxPool);
        factory.setPoolConfig(jedisPoolConfig);

        return factory;
    }

    @Bean(name = "loaderRedisTemplate")
    public StringRedisTemplate predictRedisTemplate(@Qualifier("loaderRedisConnectionFactory") RedisConnectionFactory predictRedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(predictRedisConnectionFactory);
        return template;
    }

    /*@Bean
    public RedissonClient getRedisson() {
        Config config = new Config();

        if (StringUtils.isEmpty(serverEnv) && serverEnv.equals("prod")) {
            config.useMasterSlaveServers().setMasterAddress(masterAddress).setPassword(password)
                    .addSlaveAddress(slaveAddress);
        } else {
            config.useSingleServer().setAddress(masterAddress).setPassword(password);
        }
        return Redisson.create(config);
    }*/

}
