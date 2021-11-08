package com.metevs.dcache.facade.request;

import com.metevs.dcache.facade.util.FindSort;
import lombok.Data;

/**
 * <select id="findRecordByNoticeCode" resultMap="driverRecordMap">
 *         SELECT
 *         <include refid="queryColumn"/>
 *         FROM
 *         <include refid="needTable"/>
 *         where 1=1
 *         and is_delete=0
 *         <if test="busId != null">
 *             and bus_id = #{busId}
 *         </if>
 *         <if test="cityCode != null">
 *             and city_code = #{cityCode}
 *         </if>
 *         <if test="noticeSendCode != null">
 *             and notice_send_code = #{noticeSendCode}
 *         </if>
 *         order by notice_send_time desc
 *     </select>
 */

@Data
public class FindRecordByNoticeCode extends FindSort {
    private Long busId;
    private Integer isDelete;
    private String cityCode;
    private String noticeSendCode;
    private Long merchantId;
}
