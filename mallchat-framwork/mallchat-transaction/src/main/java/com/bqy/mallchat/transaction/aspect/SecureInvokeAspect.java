package com.bqy.mallchat.transaction.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bqy.mallchat.transaction.annotation.SecureInvoke;
import com.bqy.mallchat.transaction.domain.dto.SecureInvokeDTO;
import com.bqy.mallchat.transaction.domain.entity.SecureInvokeRecord;
import com.bqy.mallchat.transaction.service.SecureInvokeHolder;
import com.bqy.mallchat.transaction.service.SecureInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE+1)
@Component
@Slf4j
public class SecureInvokeAspect {
    @Resource
    private SecureInvokeService secureInvokeService;

    @Around("@annotation(secureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable{
        boolean async = secureInvoke.async();
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        //非事务状态直接执行，不做任何保证
        if(SecureInvokeHolder.isInvoking()||!inTransaction){
            return joinPoint.proceed();
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO dto = SecureInvokeDTO.builder()
                .args(JSONUtil.toJsonStr(joinPoint.getArgs()))
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JSONUtil.toJsonStr(parameters))
                .build();
        SecureInvokeRecord record = SecureInvokeRecord.builder()
                .secureInvokeDTO(dto)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(),(int) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .build();
        secureInvokeService.invoke(record,async);
        return null;
    }
}
