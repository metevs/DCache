package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.util.List;

@Data
public class DrEqualQeury extends FindSort {
    private Long merchantId;
    private Long routeId;
    private List<Integer> operateStates;
    private Integer type;
    private Long busId;
    private Long endStationId;
    private Long startStationId;
    private Integer isDelete;
    private String cityCode;
    private String noticeSendCode;
    private List<Integer> notInOperateStates;
}
