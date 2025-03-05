package com.bqy.common.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Repeatable(FrequencyControlContainer.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FrequencyControl {
    String prefixKey() default "";
    Target target() default Target.EL;
    String spEl() default "";
    int time();
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    int count();
    enum Target {
        UID, IP, EL
    }
}
