package com.metevs.dcache.facade.query;

/**
 *
 * 借调表字段枚举
 *
 * file LendRecordField.java
 * author liumapp
 * github https://github.com/liumapp
 * email liumapp.com@gmail.com
 * homepage http://www.liumapp.com
 * date 2020/3/26
 */
public enum LendRecordField {

    ID("id"),

    ROUTE_ID("routeId"),

    SHIFT_NUM("shiftNum"),

    BUS_ID("busId"),

    EMP_ID("empId"),

    EMP_NAME("empName"),

    PLAN_SEQ_NUM("planSeqNum"),

    INSERT_STATION_ID("insertStationId"),

    LEND_TIME("lendTime"),

    LEND_ROUTE_ID("lendRouteId"),

    ATTR_ID("attrId"),

    PLAN_ON_TIME("planOnTime"),

    CREATE_TIME("createTime"),

    MODIFIED_TIME("modifiedTime"),

    IS_DELETE("isDelete"),

    CITY_CODE("cityCode"),

    OPERATION_ID("operationId"),

    EXEC_DATE("execDate");

    String alias;

    LendRecordField(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }


}



