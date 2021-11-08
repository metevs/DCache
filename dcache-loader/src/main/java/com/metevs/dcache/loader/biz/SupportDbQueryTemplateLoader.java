package com.metevs.dcache.loader.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.facade.domain.SortOrder;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.query.EqualQueryBuilder;
import com.metevs.dcache.facade.query.MustQueryBuilder;
import com.metevs.dcache.facade.query.RangeQueryBuilder;
import com.metevs.dcache.facade.util.QueryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * 支持直接走数据库查询的loader
 *
 * file SupportDbQueryTemplateLoader.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/9/22
 */
@Slf4j
public abstract class SupportDbQueryTemplateLoader<T extends BaseDO, E> extends SimpleTemplateLoader<T> {

    public abstract List<T> entityToCache(List<E> entities);

    /**
     * 转数据库查询语义
     * @param drTeamQueryParam
     * @return
     */
    protected QueryWrapper<E> buildDbParams(DCacheTeamQueryParam drTeamQueryParam) {
        return mybatisParamAnalysis(drTeamQueryParam);
    }

    private QueryWrapper<E> mybatisParamAnalysis(DCacheTeamQueryParam drTeamQueryParam) {

        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(drTeamQueryParam.getEqualQueryBuilders())) {
            for (EqualQueryBuilder equalQueryBuilder: drTeamQueryParam.getEqualQueryBuilders()) {
                if (equalQueryBuilder.getT() != null) {
                    if (equalQueryBuilder.getEqualType().equals(QueryType.EqualType.EQUAL)) {
                        queryWrapper.eq(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, equalQueryBuilder.getFieldName()), equalQueryBuilder.getT()
                        );
                    } else {
                        queryWrapper.ne(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, equalQueryBuilder.getFieldName()), equalQueryBuilder.getT()
                        );
                    }
                }

                if (equalQueryBuilder.getTs() != null && equalQueryBuilder.getTs().size() != 0) {
                    if (equalQueryBuilder.getEqualType().equals(QueryType.EqualType.EQUAL)) {
                        queryWrapper.in(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, equalQueryBuilder.getFieldName()), equalQueryBuilder.getTs()
                        );
                    } else {
                        queryWrapper.notIn(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, equalQueryBuilder.getFieldName()), equalQueryBuilder.getTs()
                        );
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(drTeamQueryParam.getRangeQueryBuilders())) {
            for (RangeQueryBuilder rangeQueryBuilder: drTeamQueryParam.getRangeQueryBuilders()) {
                switch (rangeQueryBuilder.getCompareType()) {
                    case GT:
                        queryWrapper.gt(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rangeQueryBuilder.getFieldName()), rangeQueryBuilder.getT()
                        );
                        break;
                    case GTE:
                        queryWrapper.ge(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rangeQueryBuilder.getFieldName()), rangeQueryBuilder.getT()
                        );
                        break;
                    case LT:
                        queryWrapper.lt(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rangeQueryBuilder.getFieldName()), rangeQueryBuilder.getT()
                        );
                        break;
                    case LTE:
                        queryWrapper.le(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rangeQueryBuilder.getFieldName()), rangeQueryBuilder.getT()
                        );
                        break;
                    case BT:
                        if (rangeQueryBuilder.getTs() == null || rangeQueryBuilder.getTs().size() != 2) {
                            throw new IllegalArgumentException("BT范围查询参数不合法！");
                        }
                        queryWrapper.between(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, rangeQueryBuilder.getFieldName()),
                                rangeQueryBuilder.getTs().get(0), rangeQueryBuilder.getTs().get(1)
                        );
                        break;
                }
            }
        }

        if (!CollectionUtils.isEmpty(drTeamQueryParam.getMustQueryBuilders())) {
            for (MustQueryBuilder mustQueryBuilder: drTeamQueryParam.getMustQueryBuilders()) {
                switch (mustQueryBuilder.getNullType()) {
                    case ISNULL:
                        queryWrapper.isNull(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mustQueryBuilder.getFieldName())
                        );
                        break;
                    case NOTNULL:
                        queryWrapper.isNotNull(
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, mustQueryBuilder.getFieldName())
                        );
                        break;
                }
            }
        }

        if (null != drTeamQueryParam.getFindSort()) {
            if (drTeamQueryParam.getFindSort().getSortOrder().equals(SortOrder.ASC)) {
                queryWrapper.orderByAsc(
                        CaseFormat.LOWER_CAMEL.to(
                                CaseFormat.LOWER_UNDERSCORE, drTeamQueryParam.getFindSort().getSortCell()
                        )
                );
            } else {
                queryWrapper.orderByDesc(
                        CaseFormat.LOWER_CAMEL.to(
                                CaseFormat.LOWER_UNDERSCORE, drTeamQueryParam.getFindSort().getSortCell()
                        )
                );
            }
        }



        return queryWrapper;
    }

}
