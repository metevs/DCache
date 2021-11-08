package com.metevs.dcache.loader.util;

import java.lang.annotation.*;

@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduleInvalidLoad {

}