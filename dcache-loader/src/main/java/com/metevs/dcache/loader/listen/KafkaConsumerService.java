package com.metevs.dcache.loader.listen;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.loader.runner.LoaderRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaConsumerService {

    @Value("${core.retry.notify.topic}")
    private String retryTopic;

    @Autowired
    private LoaderRunner loaderRunner;

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }


    /**
     * 接收外部更新
     *
     * @param cr
     */
    /*@KafkaListener(topicPattern = "${core.integration.notify.topic}", containerFactory =
            "kafkaNormalListenerContainerFactory")*/
    private void normalListenEvent(ConsumerRecord<String, String> cr) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_OUTER));
        try {
            CacheMsg cacheMsg = JSON.parseObject(cr.value(), CacheMsg.class);
            if (cacheMsg == null) {
                log.error("cache msg is null,origin:{}", JSON.toJSONString(cr));
            }

            loaderRunner.consumer(cacheMsg);
        } catch (Exception e) {
            log.error("[listen] normalListenEvent handle error", e);
        }

    }

    /**
     * 发布重试
     *
     * @param cr
     */
    @KafkaListener(topicPattern = "${core.retry.notify.topic}", containerFactory =
            "kafkaInnerListenerContainerFactory")
    private void innerListenEvent(ConsumerRecord<String, String> cr, Acknowledgment ack) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_INNER));
        try {
            CacheMsg cacheMsg = JSON.parseObject(cr.value(), CacheMsg.class);
            if (cacheMsg == null) {
                log.error("cache msg is null,origin:{}", JSON.toJSONString(cr));
            }
            loaderRunner.syncConsumer(cacheMsg);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("[listen] innerListenEvent handle error", e);
            //超过30s钟，抛弃策略
            if (System.currentTimeMillis() - cr.timestamp() > 30 * 1000) {
                ack.acknowledge();
                log.error("throw old message in case block {}", JSON.toJSONString(cr));
            }
        }


    }
}
