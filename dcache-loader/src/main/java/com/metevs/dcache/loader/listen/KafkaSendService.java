package com.metevs.dcache.loader.listen;

import com.metevs.dcache.loader.count.Counter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@Slf4j
public class KafkaSendService {

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }


    @Autowired
    @Qualifier("kafkaProducer")
    private KafkaTemplate<String, String> cacheKafkaProducer;

    public void sendToRetry(String msg) {
        cacheKafkaProducer.send(new ProducerRecord("CACHE_RETRY_TOPIC", msg));
    }

    public void sendToSouthNotify(String msg, String merchantId) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_NOTIFY));
        cacheKafkaProducer.send(new ProducerRecord(getIdsTopic("CACHE_S_NOTIFY", merchantId), msg));
    }

    public static final String INVALID_NOTIFY_TOPIC = "IDS_CANAL_DATA_NOTIFY";

    public void sendToFullCanalNotify(String msg) {
        cacheKafkaProducer.send(new ProducerRecord(INVALID_NOTIFY_TOPIC, msg));
    }

    public String getIdsTopic(String... parts) {
        StringBuffer sb = new StringBuffer("IDS");
        for (int i = 0; i < parts.length; i++) {
            sb.append("_").append(parts[i]);
        }
        return sb.toString();
    }
}
