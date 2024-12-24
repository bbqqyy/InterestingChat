package com.bqy.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    String key();
    String prefix() default "";
    int waitTime() default -1;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;


}
