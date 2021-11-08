package com.metevs.dcache.facade.request;


import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <select id="findUnWorkCacheByRealTime" resultMap="driverRecordMap">
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
 *         <if test="actTime != null">
 *             and real_start_time &lt; #{actTime}
 *         </if>
 *         and real_arrive_time is null
 *         and operate_state not in (1,2,3)
 *         order by real_start_time desc
 *     </select>
 */
@Data
public class FindUnWorkCacheByRealTime extends FindSort {
    private Long merchantId;
    private Long busId;
    private List<Integer> operateStates;
    private LocalDateTime actTime;
    private List<Integer> notInOperateStates;
}
