package com.metevs.dcache.loader.constants;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String ZK_UPDATE_PATH = "loader_modify_time";
    public static final String ZK_RISE_SITE_PATH = "loader_site_rise_time";
    public static final String SPACE = " ";
    public static final String ZK_RISE_PATH = "loader_rise_time";
    public static final String ZK_RISE_TIMEOUT = "loader_rise_expire";
    public static final String ZK_RISE_SITE_TIMEOUT = "loader_rise_site_expire";
    public static final int initIntervalSeconds = 120;
    public static final String SPLIT = "&";
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int ONE = 1;
    public static final int THREE = 3;
    public static final int FIVE = 5;

    public static final String DR_INDEX = "metevs";
    public static final String LD_INDEX = "metevs_index";
    public static final String LD_DOCUMENT = "lendRecord";
    public static final String LD_KEY = "id";

    public static String joinKey(String ... key){
        return String.join(SPLIT,key);
    }

}
