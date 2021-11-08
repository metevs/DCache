package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <select id="findCacheRouteRecSngTime" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
 *         <if test="merchantId!=null">
 *             and merchant_id = #{merchantId}
 *         </if>
 *         <if test="routeId!=null">
 *             and route_id = #{routeId}
 *         </if>
 *         <if test="operateStates != null ">
 *             and operate_state in
 *             <foreach collection="operateStates" item ="operateState" open="("
 *                      separator="," close=")">
 *                 #{operateState}
 *             </foreach>
 *         </if>
 *         <if test="dispatchStartTime != null and type == 0">
 *             and dispatch_start_time &gt;= #{dispatchStartTime}
 *         </if>
 *         <if test="dispatchStartTime != null and type == 1">
 *             and dispatch_start_time &lt;= #{dispatchStartTime}
 *         </if>
 *         and dispatch_start_time is not null
 *         and dispatch_arrive_time is not null
 *         order by dispatch_start_time asc
 *     </select>
 */
@Data
public class FindCacheRouteRecSngTime extends FindSort {
    private Long merchantId;
    private Long routeId;
    private List<Integer> operateStates;
    private LocalDateTime dispatchStartTime;
    private Integer type;
}
