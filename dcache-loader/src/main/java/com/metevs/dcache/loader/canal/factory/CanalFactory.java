package com.metevs.dcache.loader.canal.factory;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.metevs.dcache.loader.canal.protocol.FlatMessage;
import com.metevs.dcache.loader.config.CanalConfig;
import com.metevs.dcache.facade.domain.CacheMsg;
import com.metevs.dcache.facade.query.QueryEntity;
import com.metevs.dcache.loader.listen.KafkaSendService;
import com.metevs.dcache.loader.runner.LoaderRunner;
import com.metevs.dcache.loader.util.AsyncChannelProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * @author 展昭
 * @date 2020/3/31 14:39
 */
@Slf4j
@Configuration
public class CanalFactory {

    @Autowired
    private LoaderRunner loaderRunner;

    @Autowired
    private CanalConfig canalConfig;

    private static final String split_1 = ",";

    private static final String split_2 = "\\|";

    private final static Map<String, QueryEntity> msgTypeMap = new HashMap<>();
    private final static Map<String, Pattern> patternTables = new ConcurrentHashMap<>(8);
    private final static AtomicLong atomicLong = new AtomicLong(0);
    private final static Map<String, QueryEntity> msgTypeWhiteMap = new ConcurrentHashMap<>(16);
    private final static Set<String> msgTypeBlackMap = new ConcurrentHashSet<>(16);

    @Autowired
    private KafkaSendService kafkaSendService;


    /**
     * @return
     */
    @Bean(name = "canalAsynChannelProcess")
    public AsyncChannelProcess getTvMsgAsyncChannelProcess() {
        return AsyncChannelProcess.<FlatMessage>newBuilder()
                .setAddBlockTimeout(24, TimeUnit.HOURS)
                .setMaxProcessCount(5)
                .setProcess((Function<FlatMessage, Boolean>) msg -> {
                    saveImpl(msg);
                    return true;
                })
                .build();
    }

    /**
     * @param msg
     */
    private void saveImpl(FlatMessage msg) {
        CacheMsg cacheMsg = this.buildCacheMsg(msg);
        if (cacheMsg == null) {
            return;
        }
        QueryEntity queryEntity = cacheMsg.getType();
        List<Map<String, String>> list = msg.getData();
        if (queryEntity == null) {
            log.debug("queryEntity is null {}", JSON.toJSONString(msg));
            return;
        }
        list.stream().forEach(
                object -> {
                    CacheMsg sendMsg = new CacheMsg();
                    sendMsg.setType(queryEntity);
                    sendMsg.setValue(JSONObject.toJSONString(object));
                    sendMsg.setTag(atomicLong.incrementAndGet());
                    loaderRunner.consumer(sendMsg);
                    kafkaSendService.sendToFullCanalNotify(JSON.toJSONString(sendMsg));
                });
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(canalConfig.getTableMappers())) {
            return;
        }
        String[] elements = canalConfig.getTableMappers().split(split_1);
        for (String element : elements) {
            String[] cells = element.split(split_2);
            msgTypeMap.put(cells[0], QueryEntity.valueOf(cells[1]));
            if (cells[0].indexOf("/") > 0 || cells[0].indexOf(".") > 0) {
                patternTables.put(cells[0], Pattern.compile(cells[0]));
            }
        }
    }


    /**
     * @param flatMessage
     * @return
     */
    public CacheMsg buildCacheMsg(FlatMessage flatMessage) {
        CacheMsg cacheMsg = new CacheMsg();
        QueryEntity msgType = msgTypeMap.get(flatMessage.getTable());
        if (msgType == null) {
            //模糊匹配
            msgType = propertiesAfterSet(flatMessage);
            if (msgType == null) {
                log.debug("CanalFactory createCacheMsg tableName get cache model is null, tableName :{}",
                        flatMessage.getTable());
                return null;
            }
        }
        cacheMsg.setType(msgType);
        return cacheMsg;
    }

    /**
     * @param flatMessage
     * @return
     */
    private QueryEntity propertiesAfterSet(FlatMessage flatMessage) {
        QueryEntity msgType = null;
        if (msgTypeWhiteMap.containsKey(flatMessage.getTable())) {
            msgType = msgTypeWhiteMap.get(flatMessage.getTable());
            return msgType;
        }
        if (msgTypeBlackMap.contains(flatMessage.getTable())) {
            return null;
        }

        for (String pattern : patternTables.keySet()) {
            if (patternTables.get(pattern).matcher(flatMessage.getTable()).find()) {
                msgType = msgTypeMap.get(pattern);
                msgTypeWhiteMap.putIfAbsent(flatMessage.getTable(), msgType);
                break;
            }
        }
        if (msgType == null) {
            msgTypeBlackMap.add(flatMessage.getTable());
        }

        return msgType;
    }

}
