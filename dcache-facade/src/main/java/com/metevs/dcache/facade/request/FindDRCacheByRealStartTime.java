package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <select id="findDrivingRecordCacheByRealStartTime" resultMap="driverRecordMap">
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
 *         <if test="realStartTime != null">
 *             and real_start_time &gt; #{realStartTime}
 *         </if>
 *         <if test="realArriveTime != null">
 *             and real_start_time &lt; #{realArriveTime}
 *         </if>
 *         <if test="startStationId != null">
 *             and start_station_id = #{startStationId}
 *         </if>
 *         and (real_start_time is null or real_start_time='0001-01-01 00:00:00')
 *         order by real_start_time asc
 *     </select>
 */
@Data
public class FindDRCacheByRealStartTime extends FindSort {
    private Long merchantId;
    private Long routeId;
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime realStartTime; //不能为空
    private LocalDateTime realArriveTime;
    private Long startStationId;

}

