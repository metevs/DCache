package com.metevs.dcache.loader.listen;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.loader.canal.protocol.FlatMessage;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.loader.util.AsyncChannelProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * canal kafka消费
 *
 * @author 展昭
 * @date 2020/3/31 11:25
 **/
@Component
@Slf4j
public class CanalKafkaService extends AbstractCanalKafkaService {

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }

    @Resource
    @Qualifier("canalAsynChannelProcess")
    private AsyncChannelProcess<FlatMessage> canalAsynChannelProcess;

    private final static AtomicLong ato_dr = new AtomicLong(0);

    private final static AtomicLong ato_ep = new AtomicLong(0);

    private final static AtomicLong ato_extra = new AtomicLong(0);

    @KafkaListener(topics = "${canal.mq.topic.record}", containerFactory =
            "kafkaNormalListenerContainerFactory")
    private void listenData(String flatMessageJson) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_CANAL));
        if(ato_dr.longValue()%3000 ==0){
            log.info("canalKafkaService listen driving record {}",flatMessageJson);
        }
        try {
            FlatMessage flatMessage = JSON.parseObject(flatMessageJson, FlatMessage.class);
            if (!super.filter(flatMessage))
                return;

            //log.info("canalKafkaService listen driving record {}",flatMessage.getData());
            canalAsynChannelProcess.add(flatMessage, ato_dr.incrementAndGet());
        } catch (Exception e) {
            log.error("CanalKafkaService listenData is error, detail :{}", e);
        }
    }

    @KafkaListener(topics = "${canal.mq.topic.empatt}", containerFactory =
            "kafkaNormalListenerContainerFactory")
    private void listenEmpDate(String flatMessageJson) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_CANAL));
        if(ato_ep.longValue()%500 ==0){
            log.info("canalKafkaService listen emp {}",flatMessageJson);
        }
        try {
            FlatMessage flatMessage = JSON.parseObject(flatMessageJson, FlatMessage.class);
            if (!super.filter(flatMessage))
                return;

            //log.info("canalKafkaService listen emp {}",flatMessage.getData());
            canalAsynChannelProcess.add(flatMessage, ato_ep.incrementAndGet());
        } catch (Exception e) {
            log.error("CanalKafkaService listenData is error, detail :{}", e);
        }
    }

    @KafkaListener(topics = "${canal.mq.topic.extra}", containerFactory =
            "kafkaNormalListenerContainerFactory")
    private void listenExtraDate(String flatMessageJson) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_CANAL));
        if(ato_extra.longValue()%1000 ==0){
            log.info("canalKafkaService listen extra {}",flatMessageJson);
        }
        try {
            FlatMessage flatMessage = JSON.parseObject(flatMessageJson, FlatMessage.class);
            if (!super.filter(flatMessage))
                return;
            canalAsynChannelProcess.add(flatMessage, ato_extra.incrementAndGet());
        } catch (Exception e) {
            log.error("CanalKafkaService listenData is error, detail :{}", e);
        }
    }

}
