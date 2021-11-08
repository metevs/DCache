package com.metevs.dcache.facade.query;

import com.metevs.dcache.facade.domain.SortOrder;
import com.metevs.dcache.facade.util.FindSort;
import com.metevs.dcache.facade.util.QueryType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * file LendRecordParamBuilder.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/26
 */
public class LendRecordParamBuilder {

    private DCacheTeamQueryParam param;

    protected LendRecordParamBuilder() {
        this.param = new DCacheTeamQueryParam();
    }

    protected LendRecordParamBuilder(DCacheTeamQueryParam param) {
        this.param = param;
    }

    public LendRecordParamBuilder sortFind(SortOrder sortOrder, LendRecordField lendRecordField) {
        FindSort findSort = new FindSort();
        findSort.setSortCell(lendRecordField.alias);
        findSort.setSortOrder(sortOrder);
        param.findSort = findSort;
        return this;
    }

    // ============================ equal and some not equal query begin ============================

    public LendRecordParamBuilder setId(Long id) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ID.alias, id, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setIds (List<Long> ids) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ID.alias, null, ids,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setRouteId(Long routeId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ROUTE_ID.alias, routeId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setRouteIds(List<Long> routeIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ROUTE_ID.alias, null, routeIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setShiftNum(Integer shiftNum) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.SHIFT_NUM.alias, shiftNum, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setBusId(Long busId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.BUS_ID.alias, busId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setBusIds(List<Long> busIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.BUS_ID.alias, null, busIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setEmpId(Long empId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_ID.alias, empId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setEmpIds(List<Long> empIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_ID.alias, null, empIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setEmpName(String empName) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_NAME.alias, empName, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setEmpNames(List<String> empNames) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_NAME.alias, null, empNames,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setPlanSeqNum(Integer planSeqNum) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_SEQ_NUM.alias, planSeqNum, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setInsertStationId(Long insertStationId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.INSERT_STATION_ID.alias, insertStationId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setInsertStationIds(List<Long> insertStationIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.INSERT_STATION_ID.alias, null, insertStationIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setLendTime(String lendTime) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_TIME.alias, lendTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setLendTimes(List<String> lendTimes) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_TIME.alias, null, lendTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setLendRouteId(Long lendRouteId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_ROUTE_ID.alias, lendRouteId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setLendRouteIds(List<Long> lendRouteIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_ROUTE_ID.alias, null, lendRouteIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAttrId(Long attrId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ATTR_ID.alias, attrId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAttrIds(List<Long> attrIds) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ATTR_ID.alias, null, attrIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setPlanOnTime(LocalDateTime planOnTime) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_ON_TIME.alias, planOnTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setPlanOnTimes(List<LocalDateTime> planOnTimes) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_ON_TIME.alias, null, planOnTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setCreateTime(LocalDateTime createTime) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CREATE_TIME.alias, createTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setCreateTimes(List<LocalDateTime> createTimes) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CREATE_TIME.alias, null, createTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setModifiedTime(LocalDateTime modifiedTime) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.MODIFIED_TIME.alias, modifiedTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setModifiedTimes(List<LocalDateTime> modifiedTimes) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.MODIFIED_TIME.alias, null, modifiedTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setIsDelete(Integer isDelete) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.IS_DELETE.alias, isDelete, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setCityCode(String cityCode) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CITY_CODE.alias, cityCode, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setCityCodes(List<String> cityCodes) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CITY_CODE.alias, null, cityCodes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setOperationId(Long operationId) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.OPERATION_ID.alias, operationId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setExecDate(LocalDate execDate) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EXEC_DATE.alias, execDate, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setExecDates(List<LocalDate> execDates) {
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EXEC_DATE.alias, null, execDates,
                QueryType.EqualType.EQUAL));
        return this;
    }

    // ============================ equal and some not equal query end ============================

    // ============================ allow null equal query begin ============================

    public LendRecordParamBuilder setAllowNullId(Long id) {
        if (null == id) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ID.alias, id, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullIds (List<Long> ids) {
        if (null == ids) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ID.alias, null, ids,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullRouteId(Long routeId) {
        if (null == routeId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ROUTE_ID.alias, routeId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullRouteIds(List<Long> routeIds) {
        if (null == routeIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ROUTE_ID.alias, null, routeIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullShiftNum(Integer shiftNum) {
        if (null == shiftNum) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.SHIFT_NUM.alias, shiftNum, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullBusId(Long busId) {
        if (null == busId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.BUS_ID.alias, busId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullBusIds(List<Long> busIds) {
        if (null == busIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.BUS_ID.alias, null, busIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullEmpId(Long empId) {
        if (null == empId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_ID.alias, empId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullEmpIds(List<Long> empIds) {
        if (null == empIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_ID.alias, null, empIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullEmpName(String empName) {
        if (null == empName) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_NAME.alias, empName, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullEmpNames(List<String> empNames) {
        if (null == empNames) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EMP_NAME.alias, null, empNames,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullPlanSeqNum(Integer planSeqNum) {
        if (null == planSeqNum) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_SEQ_NUM.alias, planSeqNum, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullInsertStationId(Long insertStationId) {
        if (null == insertStationId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.INSERT_STATION_ID.alias, insertStationId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullInsertStationIds(List<Long> insertStationIds) {
        if (null == insertStationIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.INSERT_STATION_ID.alias, null, insertStationIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullLendTime(String lendTime) {
        if (null == lendTime) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_TIME.alias, lendTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullLendTimes(List<String> lendTimes) {
        if (null == lendTimes) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_TIME.alias, null, lendTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullLendRouteId(Long lendRouteId) {
        if (null == lendRouteId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_ROUTE_ID.alias, lendRouteId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullLendRouteIds(List<Long> lendRouteIds) {
        if (null == lendRouteIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.LEND_ROUTE_ID.alias, null, lendRouteIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullAttrId(Long attrId) {
        if (null == attrId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ATTR_ID.alias, attrId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullAttrIds(List<Long> attrIds) {
        if (null == attrIds) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.ATTR_ID.alias, null, attrIds,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullPlanOnTime(LocalDateTime planOnTime) {
        if (null == planOnTime) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_ON_TIME.alias, planOnTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullPlanOnTimes(List<LocalDateTime> planOnTimes) {
        if (null == planOnTimes) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.PLAN_ON_TIME.alias, null, planOnTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullCreateTime(LocalDateTime createTime) {
        if (null == createTime) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CREATE_TIME.alias, createTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullCreateTimes(List<LocalDateTime> createTimes) {
        if (null == createTimes) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CREATE_TIME.alias, null, createTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullModifiedTime(LocalDateTime modifiedTime) {
        if (null == modifiedTime) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.MODIFIED_TIME.alias, modifiedTime, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullModifiedTimes(List<LocalDateTime> modifiedTimes) {
        if (null == modifiedTimes) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.MODIFIED_TIME.alias, null, modifiedTimes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullIsDelete(Integer isDelete) {
        if (null == isDelete) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.IS_DELETE.alias, isDelete, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullCityCode(String cityCode) {
        if (null == cityCode) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CITY_CODE.alias, cityCode, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullCityCodes(List<String> cityCodes) {
        if (null == cityCodes) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.CITY_CODE.alias, null, cityCodes,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullOperationId(Long operationId) {
        if (null == operationId) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.OPERATION_ID.alias, operationId, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullExecDate(LocalDate execDate) {
        if (null == execDate) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EXEC_DATE.alias, execDate, null,
                QueryType.EqualType.EQUAL));
        return this;
    }

    public LendRecordParamBuilder setAllowNullExecDates(List<LocalDate> execDates) {
        if (null == execDates) return this;
        param.appendEqualQueryBuilder(new EqualQueryBuilder<>(LendRecordField.EXEC_DATE.alias, null, execDates,
                QueryType.EqualType.EQUAL));
        return this;
    }

    // ============================ allow null equal query end ============================

    // ============================ range time query begin ============================

    public LendRecordParamBuilder appendPlanOnTime(QueryType.CompareType compareType, LocalDateTime time) {
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.PLAN_ON_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendCreateTime(QueryType.CompareType compareType, LocalDateTime time) {
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.CREATE_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendModifiedTime(QueryType.CompareType compareType, LocalDateTime time) {
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.MODIFIED_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendExecDate(QueryType.CompareType compareType, LocalDate time) {
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.EXEC_DATE.alias, compareType, time, null));
        return this;
    }

    // ============================ range time query end ============================

    // ============================ allow null range time query begin ============================

    public LendRecordParamBuilder appendAllowNullPlanOnTime(QueryType.CompareType compareType, LocalDateTime time) {
        if (time == null) return this;
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.PLAN_ON_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendAllowNullCreateTime(QueryType.CompareType compareType, LocalDateTime time) {
        if (time == null) return this;
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.CREATE_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendAllowNullModifiedTime(QueryType.CompareType compareType, LocalDateTime time) {
        if (time == null) return this;
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.MODIFIED_TIME.alias, compareType, time, null));
        return this;
    }

    public LendRecordParamBuilder appendAllowNullExecDate(QueryType.CompareType compareType, LocalDate time) {
        if (time == null) return this;
        param.appendRangeQueryBuilder(new RangeQueryBuilder(LendRecordField.EXEC_DATE.alias, compareType, time, null));
        return this;
    }

    // ============================ allow null range time query end ============================

    // ============================ is null and not null or default value query begin ============================

    public LendRecordParamBuilder isNull(LendRecordField lendRecordField) {
        param.appendMustQueryBuilder(new MustQueryBuilder<Integer>(lendRecordField.alias, QueryType.NullType.ISNULL, null));
        return this;
    }

    public LendRecordParamBuilder notNull(LendRecordField lendRecordField) {
        param.appendMustQueryBuilder(new MustQueryBuilder<Integer>(lendRecordField.alias, QueryType.NullType.NOTNULL, null));
        return this;
    }

    // ============================ is null and not null or defalut value query end ============================

    public DCacheTeamQueryParam build() {
        DCacheTeamQueryParam.build(param);
        param.queryEntity = QueryEntity.LEND_RECORD;
        return param;
    }

}
