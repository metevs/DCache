package com.metevs.dcache.loader.count;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class Counter {

    public static final String TOTAL_KEY = "total";
    public static final String QUERY = "query_total";
    public static final String KAFKA_TOTAL = "kafka_total";
    public static final String DUBBO_TOTAL = "dubbo_total";
    public static final String QUERY_LR = "query_lendRecord";
    public static final String BLOOM_FILTER = "query_bloom_filter";

    public static final String KAFKA_BLOOM = "kafka_bloom";
    public static final String KAFKA_CANAL = "kafka_canal";
    public static final String KAFKA_OUTER = "kafka_outer";
    public static final String KAFKA_INNER = "kafka_inner";
    public static final String KAFKA_NOTIFY = "kafka_notify";

    public static final String INSERT_SUCCESS = "insert_success";
    public static final String LOAD_SUCCESS = "load_success";
    public static final String MODIFY_SUCCESS = "modify_success";
    public static final String INVALID_SUCCESS = "invalid_success";
    public static final String MODIFY_FAILED = "send_failed";
    public static final String FAILED = "failed";
    public static final String REPEATED_FAILED = "repeated_failed";

    public static final String DUBBO_Performance_more = "DUBBO_Performance_more";
    public static final String DUBBO_Performance_10000 = "DUBBO_Performance_10000";
    public static final String DUBBO_Performance_3000 = "DUBBO_Performance_3000";
    public static final String DUBBO_Performance_1000 = "DUBBO_Performance_1000";
    public static final String DUBBO_Performance_600 = "DUBBO_Performance_600";
    public static final String DUBBO_Performance_100 = "DUBBO_Performance_100";
    public static final String DUBBO_Performance_10 = "DUBBO_Performance_10";

    private LocalDateTime history = LocalDateTime.now();

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 统计组
     */
    private Map<String, Map<String, LongAdder>> counterGroup = new ConcurrentHashMap<>(8);

    private Map<String, Map<String, LongAdder>> historyGroup = new ConcurrentHashMap<>(8);


    /**
     * 打印所有接收到的报文数量,丢弃的报文数量
     */
    public void logInfo() {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder sb = new StringBuilder("\n============= counter log =============\n");
        sb.append("history time:").append(history.format(DATE_TIME_FORMATTER)).append("\n");
        sb.append("current time:").append(now.format(DATE_TIME_FORMATTER)).append("\n");
        history = now;

        for (Map.Entry<String, Map<String, LongAdder>> counter : historyGroup.entrySet()) {
            sb.append("-----------").append(counter.getKey()).append("-----------\n");
            sb.append("history:");
            int bloom = 0;
            for (Map.Entry<String, LongAdder> count : counter.getValue().entrySet()) {
                sb.append(count.getKey())
                        .append(",count:").append(count.getValue()).append("|| ");
                if (count.getKey().equals(BLOOM_FILTER)) {
                    bloom = count.getValue().intValue();
                }
            }
            int total = counter.getValue().values().stream().mapToInt(LongAdder::intValue).sum();
            sb.append("total:").append(total - bloom)
                    .append(",invalid_count:").append(total - 2 * bloom).append("\n");
        }

        for (Map.Entry<String, Map<String, LongAdder>> counter : counterGroup.entrySet()) {
            sb.append("-----------").append(counter.getKey()).append("-----------\n");
            sb.append("source:");
            int bloom = 0;
            for (Map.Entry<String, LongAdder> count : counter.getValue().entrySet()) {
                sb.append(count.getKey())
                        .append(",count:").append(count.getValue()).append("|| ");
                if (count.getKey().equals(BLOOM_FILTER)) {
                    bloom = count.getValue().intValue();
                }
            }
            int total = counter.getValue().values().stream().mapToInt(LongAdder::intValue).sum();
            sb.append("total:").append(total - bloom)
                    .append(",invalid_count:").append(total - 2 * bloom).append("\n");
        }
        clear();
        sb.append("============= log end =============");
        log.info(sb.toString());
    }

    public long getCount(String key, String key2) {
        return counterGroup.computeIfAbsent(key, s -> new ConcurrentHashMap<>(8))
                .computeIfAbsent(key2, s -> new LongAdder()).longValue();
    }

    /**
     * 添加统计值
     *
     * @param key  一级分类
     * @param key2 二级分类
     */
    public void increment(String key, String key2) {
        historyGroup.computeIfAbsent(key, s -> new ConcurrentHashMap<>(8))
                .computeIfAbsent(key2, s -> new LongAdder()).increment();
        counterGroup.computeIfAbsent(key, s -> new ConcurrentHashMap<>(8))
                .computeIfAbsent(key2, s -> new LongAdder()).increment();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        for (Map<String, LongAdder> counter : counterGroup.values()) {
            counter.clear();
        }
    }
}
