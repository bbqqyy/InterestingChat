package com.bqy.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.service.LockService;
import com.bqy.common.common.utils.SpElUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(0)
public class RedissonLockAspect {
    @Resource
    private LockService lockService;
    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock){
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefix())? SpElUtil.getSpEl(method) :redissonLock.prefix();
        String key = SpElUtil.parseSpEl(method,joinPoint.getArgs(),redissonLock.key());
        return lockService.executeWithLock(prefix+":"+key, redissonLock.waitTime(),redissonLock.timeUnit(),joinPoint::proceed);
    }
}
