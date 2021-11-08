package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FindPreDrivingRecordCache extends FindSort {
    private Long merchantId;
    private Long routeId;
    private List<Integer> operateState;
    private LocalDateTime dispatchStartTime;
}
