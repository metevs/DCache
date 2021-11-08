package com.metevs.dcache.loader.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * file LendRecordEntity.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/27
 */
@TableName(value = "lend_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LendRecordEntity {

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

}
