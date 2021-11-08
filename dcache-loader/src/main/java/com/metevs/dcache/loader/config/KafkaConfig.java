package com.metevs.dcache.loader.config;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(KafkaConfig.class)
@ConfigurationProperties(value = "kafka")
@Data
public class KafkaConfig {
    private String servers;
    private String groupId;
    private String autoOffsetReset;
    private Integer concurrency;

    public Map<String, Object> getNormalConsumerProperties() {
        Map<String, Object> params = new HashMap<>();
        params.put(ConsumerConfig.GROUP_ID_CONFIG, groupId+"-normal");
        params.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        params.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        params.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 13000);
        params.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 4000);
        params.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        params.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 512 * 1024);
        params.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        params.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return params;
    }

    //不自动提交/手动提交OFFSET
    public Map<String, Object> getInnerConsumerProperties() {
        Map<String, Object> params = new HashMap<>();
        params.put(ConsumerConfig.GROUP_ID_CONFIG, groupId+"-inner");
        params.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        params.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        params.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        params.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        params.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        params.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return params;
    }

/*    public Map<String, Object> getFullCanalConsumerProperties() {
        Map<String, Object> params = new HashMap<>();
        params.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-bloom-" + UUID.randomUUID().hashCode());
        params.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        params.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        params.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        params.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 13000);
        params.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 4000);
        params.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 128 * 1024);
        params.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        params.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        params.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return params;
    }*/

    private Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put("buffer.memory", 33554432);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean(name = "kafkaNormalListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaNormalListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(this.getNormalConsumerProperties()));
        factory.setConcurrency(concurrency);
        return factory;
    }

    @Bean(name = "kafkaInnerListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaInnerListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(this.getInnerConsumerProperties()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(concurrency);
        return factory;
    }

    /*@Bean(name = "kafkaFullCanalListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaFullCanalListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(this.getFullCanalConsumerProperties()));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConcurrency(concurrency);
        return factory;
    }*/


    @Bean(name = "kafkaProducer")
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate(kafkaProducerFactory());
        kafkaTemplate.setProducerListener(kafkaProducerListener());
        return kafkaTemplate;
    }

    private ProducerListener<String, String> kafkaProducerListener() {
        return new LoggingProducerListener();
    }

    private ProducerFactory<String, String> kafkaProducerFactory() {
        DefaultKafkaProducerFactory<String, String> factory = new DefaultKafkaProducerFactory(this.getProducerProps());
        return factory;
    }
}

