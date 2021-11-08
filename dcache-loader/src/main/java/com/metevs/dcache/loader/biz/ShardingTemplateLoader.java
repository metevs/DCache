package com.metevs.dcache.loader.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.metevs.dcache.facade.domain.SortOrder;
import com.metevs.dcache.facade.query.DCacheTeamQueryParam;
import com.metevs.dcache.facade.query.EqualQueryBuilder;
import com.metevs.dcache.facade.query.MustQueryBuilder;
import com.metevs.dcache.facade.query.RangeQueryBuilder;
import com.metevs.dcache.facade.util.QueryType;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * sharding-jdbc 分库分表抽象loader
 *
 * file ShardingTemplateLoader.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/17
 */
public abstract class ShardingTemplateLoader <T, G> extends SimpleTemplateLoader {

    protected QueryWrapper<T> buildDbParams(DCacheTeamQueryParam drTeamQueryParam) {
        // here should add some pre check
        // todo

        return mybatisParamAnalysis(drTeamQueryParam);
    }

    /**
     * 分析param组装查询
     * @param drTeamQueryParam
     * @return
     */
    public abstract List<G> dbCommonQuery (DCacheTeamQueryParam drTeamQueryParam);

    public abstract List<G> entityToCache (List<T> entities);

    private QueryWrapper<T> mybatisParamAnalysis(DCacheTeamQueryParam drTeamQueryParam) {

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
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
