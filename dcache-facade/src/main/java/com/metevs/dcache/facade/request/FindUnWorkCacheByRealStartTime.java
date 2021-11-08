package com.metevs.dcache.facade.request;


import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <select id="findUnWorkCacheByRealStartTime" resultMap="driverRecordMap">
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
 *         <if test="realStartTime != null">
 *             and real_start_time &gt; #{realStartTime}
 *         </if>
 *         <if test="realArriveTime != null">
 *             and real_start_time &lt; #{realArriveTime}
 *         </if>
 *         and real_arrive_time is null
 *         and operate_state not in (1,2,3)
 *         order by real_start_time asc
 *     </select>
 */
@Data
public class FindUnWorkCacheByRealStartTime extends FindSort {
    private Long merchantId;
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime realStartTime;
    private LocalDateTime realArriveTime; //不能为空
    private List<Integer> notInOperateStates;
}
