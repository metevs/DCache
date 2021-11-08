package com.metevs.dcache.facade.request;


import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *  <select id="findRecordByReplyTime" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
 *         and is_delete=0
 *         and notice_reply_time is null
 *         and real_start_time is null
 *         <if test="actTime != null">
 *             and dispatch_start_time &lt;= #{actTime}
 *             and dispatch_arrive_time &gt;= #{actTime}
 *         </if>
 *         order by dispatch_start_time asc
 *     </select>
 */
@Data
public class FindRecordByReplyTime extends FindSort {
    private Integer isDelete;
    private LocalDateTime dispatchStartTime; //不能为空
    private LocalDateTime dispatchArriveTime; //不能为空
    private Long merchantId;
}
