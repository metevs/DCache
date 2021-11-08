package com.metevs.dcache.loader.canal.filter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public abstract class AbstractCanalFilter implements CanalFilter {

    private List<String> careProperties = new ArrayList<>();

    @Override
    public boolean filter(String fastMatch, String watchs) {
        if (fastMatch == null) {
            return false;
        }
        if (StringUtils.isBlank(watchs)) {
            return false;
        }
        if ("*".equalsIgnoreCase(watchs)) {
            return true;
        }

        if (careProperties.size() == 0) {
            synchronized (careProperties) {
                if (careProperties.size() == 0) {
                    careProperties = Arrays.asList(watchs.split(split));
                }
            }
        }

        if (careProperties.contains(fastMatch)) {
            return true;
        }
        return false;
    }
}
