package com.metevs.dcache.loader.listen;

import com.alibaba.fastjson.JSON;
import com.metevs.dcache.loader.count.Counter;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.loader.runner.AbstractLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class BloomKafkaService extends AbstractCanalKafkaService {

    private Map<Class, AbstractLoader> loadersMap;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 计数器服务.
     */
    private Optional<Counter> counter = Optional.empty();

    @Autowired(required = false)
    public void setCounter(Counter counter) {
        this.counter = Optional.ofNullable(counter);
    }

    @PostConstruct
    public void initLoaders() {
        Map<String, AbstractLoader> map = applicationContext.getBeansOfType(AbstractLoader.class);
        loadersMap = map.values().stream().collect(Collectors.toMap(AbstractLoader::supportType, x -> x));
    }

    @KafkaListener(topics = KafkaSendService.INVALID_NOTIFY_TOPIC, containerFactory =
            "kafkaFullCanalListenerContainerFactory")
    private void listenFullCanalDate(String msg) {
        counter.ifPresent(service -> service.increment(Counter.KAFKA_TOTAL, Counter.KAFKA_BLOOM));
        CacheMsg cacheMsg = JSON.parseObject(msg, CacheMsg.class);
        if (cacheMsg == null || cacheMsg.getType() == null)
            return;
        if(cacheMsg.getValue().startsWith("{")){
            loadersMap.get(cacheMsg.getType().getaClass()).customBloomFilter(cacheMsg.getValue());
            return;
        }
        loadersMap.get(cacheMsg.getType().getaClass()).customBloomFilter(JSON.toJSONString(cacheMsg.getValue()));
    }

}
