package com.metevs.dcache.loader.config;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.ComplexShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.NoneShardingStrategyConfiguration;
import io.shardingsphere.core.keygen.DefaultKeyGenerator;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 历史表分表配置
 *
 * file ShardingDataSourceConfig.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/18
 */
@EnableConfigurationProperties(ShardingDataSourceConfig.class)
@ConfigurationProperties(prefix = "sharding")
@Data
@Slf4j
@Configuration
public class ShardingDataSourceConfig {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private String dbName;

    private String column;

    private String showSql;

    private String validationQuery;

    private Integer validationQueryTimeout;

    private Integer connectionTimeout;

    private Integer maxActive;

    private Integer minIdle;

    private String empHistoryTable = "attendance_history";

    private String drivingHistoryTable = "record_history";

    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        shardingRuleConfig.getBindingTableGroups().add(empHistoryTable + "," + drivingHistoryTable);
        shardingRuleConfig.getTableRuleConfigs().add(empTableRule());
        shardingRuleConfig.getTableRuleConfigs().add(drivingTableRule());
        shardingRuleConfig.setDefaultDataSourceName(dbName);
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());

        Properties properties = new Properties();
        properties.setProperty("sql.show", showSql);
        return ShardingDataSourceFactory.createDataSource(dataSourceMap(), shardingRuleConfig, new ConcurrentHashMap<>(16), properties);
    }

    private TableRuleConfiguration empTableRule() {
        TableRuleConfiguration tableRule = new TableRuleConfiguration();
        tableRule.setLogicTable(empHistoryTable);
        tableRule.setActualDataNodes(generateDataNodes(empHistoryTable));
        tableRule.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration(column, new CustomComplextSharding()));
        tableRule.setKeyGenerator(new DefaultKeyGenerator());
        tableRule.setKeyGeneratorColumnName("id");
        return tableRule;
    }

    private TableRuleConfiguration drivingTableRule() {
        TableRuleConfiguration tableRule = new TableRuleConfiguration();
        tableRule.setLogicTable(drivingHistoryTable);
        tableRule.setActualDataNodes(generateDataNodes(drivingHistoryTable));
        tableRule.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration(column, new CustomComplextSharding()));
        tableRule.setKeyGenerator(new DefaultKeyGenerator());
        tableRule.setKeyGeneratorColumnName("id");
        return tableRule;
    }

    private String generateDataNodes(String logicTableName) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 1; i < 32; i++) {
            if (i < 10) {
                stringBuffer.append(dbName + "." + logicTableName + "0" + i + ",");
            } else {
                stringBuffer.append(dbName + "." + logicTableName + i + ",");
            }
        }

        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    private Map<String, DataSource> dataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>(16);
        HikariDataSource ds0 = new HikariDataSource();
        ds0.setDriverClassName(driverClassName);
        ds0.setJdbcUrl(url);
        ds0.setUsername(username);
        ds0.setPassword(password);
        ds0.setConnectionTestQuery(validationQuery);
        ds0.setValidationTimeout(validationQueryTimeout);
        ds0.setConnectionTimeout(connectionTimeout);
        ds0.setMaximumPoolSize(maxActive);
        ds0.setMinimumIdle(minIdle);
        dataSourceMap.put(dbName, ds0);
        return dataSourceMap;
    }

    public static class CustomComplextSharding implements ComplexKeysShardingAlgorithm {

        public CustomComplextSharding() {

        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {
            Collection<String> shardingResults = new LinkedHashSet<>();
            Object[] targetNameArr = availableTargetNames.toArray();
            Iterator valueIterator = shardingValues.iterator();
            int day = 0;
            Collection execDateColl = null;
            Calendar calendar = Calendar.getInstance();

            while (valueIterator.hasNext()) {
                ShardingValue shardingValue = (ShardingValue) valueIterator.next();

                if (shardingValue instanceof ListShardingValue) {
                    ListShardingValue<Date> lists = (ListShardingValue) shardingValue;
                    execDateColl = lists.getValues();
                    continue;
                }

                if (shardingValue instanceof PreciseShardingValue) {
                    PreciseShardingValue<Date> precises = (PreciseShardingValue) shardingValue;
                    calendar.setTime(precises.getValue());
                    day = calendar.get(5);
                    shardingResults.add( (String) targetNameArr[day > 0 ? day - 1 : 0]);
                    return shardingResults;
                }

                if (shardingValue instanceof RangeShardingValue) {
                    RangeShardingValue<Date> ranges = (RangeShardingValue) shardingValue;
                    Range<Date> range = ranges.getValueRange();
                    Date lowerDate = null, upperDate = null;

                    if (range.hasLowerBound()) {
                        if (range.lowerBoundType() == BoundType.CLOSED) {
                            lowerDate = range.lowerEndpoint();
                        } else {
                            calendar.setTime(range.lowerEndpoint());
                            calendar.add(5, 1);
                            lowerDate = calendar.getTime();
                        }
                    }

                    if (range.hasUpperBound()) {
                        if (range.upperBoundType() == BoundType.CLOSED) {
                            upperDate = range.upperEndpoint();
                        } else {
                            calendar.setTime(range.upperEndpoint());
                            calendar.add(5, -1);
                            upperDate = calendar.getTime();
                        }
                    }

                    if (lowerDate != null && upperDate != null) {
                        while (lowerDate.compareTo(upperDate) < 1) {
                            calendar.setTime(lowerDate);
                            day = calendar.get(5);
                            shardingResults.add((String) targetNameArr[day > 0 ? day - 1 : 0]);
                            if (shardingResults.size() == availableTargetNames.size()) {
                                break;
                            }

                            calendar.setTime(lowerDate);
                            calendar.add(5, 1);
                            lowerDate =calendar.getTime();
                        }
                    } else {
                        shardingResults.addAll(availableTargetNames);
                    }
                    return shardingResults;
                }

            }

            Iterator dateIterator = execDateColl.iterator();
            while (dateIterator.hasNext()) {
                Date execDate = (Date) dateIterator.next();
                calendar.setTime(execDate);
                day = calendar.get(5);
                shardingResults.add((String) targetNameArr[day > 0 ? day - 1 : 0]);
            }
            return shardingResults;
        }
    }

}
