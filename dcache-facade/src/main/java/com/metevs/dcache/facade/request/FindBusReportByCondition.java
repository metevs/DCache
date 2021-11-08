package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * >actTime and execType in ()  order by actTime asc
 */
@Data
public class FindBusReportByCondition extends FindSort {
    private LocalDateTime actTime;
    private List<Integer> execType;
    private Long merchantId;
}
