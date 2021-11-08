package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  <select id="findUnWorkCacheByDispatchStartTime" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
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
 *             and dispatch_start_time &gt; #{dispatchStartTime}
 *         </if>
 *         <if test="dispatchArriveTime != null">
 *             and dispatch_start_time &lt; #{dispatchArriveTime}
 *         </if>
 *         <if test="actTime != null">
 *             and dispatch_arrive_time &gt; #{actTime}
 *         </if>
 *         <if test="startStationId != null">
 *             and start_station_id = #{startStationId}
 *         </if>
 *         and real_start_time is null
 *         and operate_state not in (1,2,3)
 *         order by dispatch_start_time asc
 *     </select>
 */
@Data
public class FindUnWorkCacheByDispatchStartTime extends FindSort {
    private Long merchantId;
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime dispatchStartTime;
    private LocalDateTime dispatchArriveTime;
    private LocalDateTime actTime;
    private Long startStationId;
    private LocalDateTime realStartTime; //不能为空
    private List<Integer> notInOperateStates; //反向
}
