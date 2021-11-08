package com.metevs.dcache.loader.listen;

import com.metevs.dcache.loader.canal.filter.CanalFilter;
import com.metevs.dcache.loader.canal.protocol.FlatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 消息过滤
 */
@Slf4j
public abstract class AbstractCanalKafkaService implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private ApplicationContext context;

    private List<CanalFilter> canalFilters = new ArrayList<>(4);

    @PostConstruct
    public void init() {
        Map<String, CanalFilter> parsers = context.getBeansOfType(CanalFilter.class);
        for (CanalFilter canalFilter : parsers.values()) {
            canalFilters.add(canalFilter);
        }
    }

    public boolean filter(FlatMessage flatMessage) {
        for (CanalFilter canalFilter : canalFilters) {
            if (canalFilter.filter(flatMessage)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }


}
