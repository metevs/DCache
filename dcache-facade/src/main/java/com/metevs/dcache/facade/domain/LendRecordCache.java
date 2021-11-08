package com.metevs.dcache.facade.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * 借调管理缓存实体类
 *
 * file LendRecordCache.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LendRecordCache extends BaseDO {

    /**
     * ID 自增
     */
    private Long id;

    private String empName;

    private Integer planSeqNum;

    private Long insertStationId;

    private String lendTime;

    private Long attrId;

    private LocalDateTime planOnTime;

    private LocalDateTime createTime;

    private LocalDateTime modifiedTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 城市编码
     */
    private String cityCode;

    private Long operationId;

    private LocalDate execDate;


    @Override
    public Long getChannel() {
        return id;
    }
}
