package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *     <select id="findNextDrivingRecordCache" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
 *         <if test="busId != null">
 *             and bus_id = #{busId}
 *         </if>
 *         <if test="operateState != null">
 *             and operate_state = #{operateState}
 *         </if>
 *         <if test="dispatchStartTime != null">
 *             and dispatch_start_time &gt;= #{dispatchStartTime}
 *         </if>
 *         and scaId is null
 *         and (notice_reply_time is null  or  notice_reply_time = '0001-01-01 00:00:00')
 *         order by dispatch_start_time asc
 *     </select>
 */
@Data
public class FindNextDrivingRecordCache extends FindSort {
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime dispatchStartTime;
    private Long merchantId;
}
