package com.metevs.dcache.loader.canal.filter;

import com.metevs.dcache.loader.canal.protocol.FlatMessage;

/**
 * 消息过滤器
 */
public interface CanalFilter {

    String split = ",";

    /**
     *
     * @param fastMatch
     * @return
     */
    boolean filter(String fastMatch,String watchs);

    /**
     *
     * @param flatMessage
     * @return
     */
    boolean filter(FlatMessage flatMessage);
}
