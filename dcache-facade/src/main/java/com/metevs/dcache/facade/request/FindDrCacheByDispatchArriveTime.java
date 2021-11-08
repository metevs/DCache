package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <select id="findDrivingRecordCacheByDispatchArriveTime" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
 *         <if test="merchantId != null">
 *             and merchant_id = #{merchantId}
 *         </if>
 *         <if test="routeId != null">
 *             and route_id = #{routeId}
 *         </if>
 *         <if test="busId != null">
 *             and bus_id = #{busId}
 *         </if>
 *         <if test="operateStates != null ">
 *             and operate_state in
 *             <foreach collection="operateStates" item ="operateState" open="("
 *                      separator="," close=")">
 *                 #{operateState}
 *             </foreach>
 *         </if>
 *         <if test="dispatchStartTime != null">
 *             and dispatch_arrive_time &gt; #{dispatchStartTime}
 *         </if>
 *         <if test="dispatchArriveTime != null">
 *             and dispatch_arrive_time &lt; #{dispatchArriveTime}
 *         </if>
 *         <if test="endStationId != null">
 *             and end_station_id = #{endStationId}
 *         </if>
 *         and real_start_time is not null
 *         order by dispatch_start_time asc
 *     </select>
 *
 */
@Data
public class FindDrCacheByDispatchArriveTime extends FindSort {
    private Long merchantId;
    private Long routeId;
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime dispatchStartTime;
    private LocalDateTime dispatchArriveTime;
    private Long endStationId;
    private LocalDateTime realStartTime;
}