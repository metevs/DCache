package com.metevs.dcache.loader.config;

import com.metevs.dcache.loader.count.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@ConditionalOnProperty(prefix = "counter", name = "enable", havingValue = "true")
@EnableScheduling
public class CounterConfig {
    private static final Logger LOG = LoggerFactory.getLogger(CounterConfig.class);
    private LocalDateTime lastLogTime;


    @Bean
    public Counter counterService() {
        return new Counter();
    }

    @Scheduled(cron = "${counter.cron}")
    public void counter() {
        counterService().logInfo();
        if (lastLogTime != null && LocalDateTime.now().minusDays(1).toLocalDate().equals(lastLogTime.toLocalDate())) {
            //如果现在减去一天的日期与最后打印日志的日期一致,那么说明跨日了.清空
            LOG.info("今日统计发送查询数:{},更新成功数:{}",
                    counterService().getCount(Counter.TOTAL_KEY, Counter.QUERY),
                    counterService().getCount(Counter.TOTAL_KEY, Counter.MODIFY_SUCCESS));
            LOG.info("今日更新失败次数:{}", counterService().getCount(Counter.TOTAL_KEY, Counter.MODIFY_FAILED));
            counterService().clear();
        }
        lastLogTime = LocalDateTime.now();
    }

}
