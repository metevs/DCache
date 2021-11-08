package com.metevs.dcache.loader.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metevs.dcache.facade.domain.LendRecordCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *
 * file LendRecordMapper.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/27
 */
@Component
@Mapper
public interface LendRecordMapper extends BaseMapper<LendRecordEntity> {

    /**
     * 全量数据
     * @return
     */
    @Select("SELECT \n" +
            "LR.* \n" +
            "FROM\n" +
            "lend_record LR")
    List<LendRecordCache> cacheBuilder();

    /**
     * 增量数据
     * @return
     */
    @Select("SELECT \n" +
            "LR.*  \n" +
            "FROM \n" +
            "lend_record LR \n" +
            "WHERE \n" +
            "LR.modified_time >= #{startModifyTime} \n" +
            "AND LR.modified_time < #{endModifyTime}")
    List<LendRecordCache> riseCacheBuilder(@Param("startModifyTime") String startModifyTime,
                                           @Param("endModifyTime") String endModifyTime);

    @Select("SELECT \n" +
            "LR.* \n" +
            "FROM  \n" +
            "lend_record LR\n" +
            "WHERE \n" +
            "LR.exec_date = DATE_FORMAT(NOW(),'%Y-%m-%d') AND LR.id = #{id}")
    LendRecordCache selectCacheByPrimaryKey(@Param("id") Long id);


}
