package com.metevs.dcache.facade.query;

import com.google.common.base.Preconditions;
import com.metevs.dcache.facade.util.FindSort;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DCacheTeamQueryParam implements Serializable {

    protected DCacheTeamQueryParam() {
    }

    public static LendRecordParamBuilder newLendRecordParamBuilder() {
        return new LendRecordParamBuilder();
    }

    public static LendRecordParamBuilder newLendRecordParamBuilder(DCacheTeamQueryParam param) {
        return new LendRecordParamBuilder(param);
    }

    @Getter
    private List<RangeQueryBuilder> rangeQueryBuilders = new ArrayList<>();
    @Getter
    private List<MustQueryBuilder> mustQueryBuilders = new ArrayList<>();
    @Getter
    private List<EqualQueryBuilder> equalQueryBuilders = new ArrayList<>();
    @Getter
    protected FindSort findSort;
    @Getter
    protected QueryEntity queryEntity;


    protected void appendRangeQueryBuilder(RangeQueryBuilder rangeQueryBuilder) {
        rangeQueryBuilders.add(rangeQueryBuilder);
    }

    protected void appendMustQueryBuilder(MustQueryBuilder mustQueryBuilder) {
        mustQueryBuilders.add(mustQueryBuilder);
    }

    protected void appendEqualQueryBuilder(EqualQueryBuilder equalQueryBuilder) {
        equalQueryBuilders.add(equalQueryBuilder);
    }

    protected static DCacheTeamQueryParam build(DCacheTeamQueryParam param) {
        Preconditions.checkArgument(!(param.rangeQueryBuilders.size() == 0 && param.equalQueryBuilders.size() == 0 &&
                param.mustQueryBuilders.size() == 0), "查询条件为空，请校验查询参数");
        //Preconditions.checkArgument(param.equalQueryBuilders.size() != 0, "查询条件非法，请校验查询参数");

        if (param.rangeQueryBuilders.size() != 0) {
            for (RangeQueryBuilder rangeQueryBuilder : param.rangeQueryBuilders) {
                Preconditions.checkArgument(rangeQueryBuilder.t != null || rangeQueryBuilder.getTs() != null, rangeQueryBuilder.fieldName + "不能为空");
            }
        }
        if (param.equalQueryBuilders.size() != 0) {
            for (EqualQueryBuilder equalQueryBuilder : param.equalQueryBuilders) {
                Preconditions.checkArgument(equalQueryBuilder.getT() != null || equalQueryBuilder.getTs() != null,
                        equalQueryBuilder.fieldName +
                                "不能为空");
            }
        }

        return param;
    }

}
