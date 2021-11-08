package com.metevs.dcache.loader.canal.filter;

import com.metevs.dcache.loader.canal.protocol.FlatMessage;
import com.metevs.dcache.loader.config.CanalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CanalDDlFilter extends AbstractCanalFilter {

    @Autowired
    private CanalConfig canalConfig;

    @Override
    public boolean filter(FlatMessage flatMessage) {
        if (flatMessage == null) {
            return false;
        }
        return super.filter(String.valueOf(flatMessage.getIsDdl()),canalConfig.getIsDDl());
    }
}
