package com.metevs.dcache.loader.canal.filter;

import com.metevs.dcache.loader.canal.protocol.FlatMessage;
import com.metevs.dcache.loader.config.CanalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CanalTableFilter extends AbstractCanalFilter {

    @Autowired
    private CanalConfig canalConfig;

    @Override
    public boolean filter(FlatMessage flatMessage) {
        if (flatMessage == null) {
            return false;
        }
        return super.filter(flatMessage.getTable(),canalConfig.getWatchTables());
    }
}
