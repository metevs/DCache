package com.metevs.dcache.facade.query;

import com.ibuscloud.dtp.access.loader.facade.domain.*;
import com.metevs.dcache.facade.domain.LendRecordCache;

public enum QueryEntity {
    DRIVING_RECORD(DrivingRecordCache.class),
    ATTENDANCE(EmpAttendanceCache.class),
    ROUTE_SITE(RouteSiteCache.class),
    BUS_REPORT(BusReportCache.class),
    EMP_ATTENDANCE_HISTORY(EmpAttendanceHistoryCache.class),
    DRIVING_HISTORY_RECORD(DrivingRecordHistoryCache.class),
    LEND_RECORD(LendRecordCache.class),
    BUS_FAULT(BusFaultCache.class),
    SCARCE_SHIFT(ScarceShiftCache.class);

    Class aClass;

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    QueryEntity(Class aClass) {
        this.aClass = aClass;
    }
}
