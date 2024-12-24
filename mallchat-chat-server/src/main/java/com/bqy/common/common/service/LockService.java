package com.bqy.common.common.service;

import com.bqy.common.common.exception.BusinessException;
import com.bqy.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class LockService {
    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    public <T> T executeWithLock(String key, Integer time, TimeUnit timeUnit, Supplier<T> supplier){
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(time, timeUnit);
        if(!success){
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try{
            return supplier.get();
        }finally {
            lock.unlock();
        }
    }
    public <T> T executeWithLock(String key, Supplier<T> supplier){
        return executeWithLock(key,-1,TimeUnit.MILLISECONDS,supplier);
    }
    public <T> T executeWithLock(String key,Runnable runnable){
        return executeWithLock(key,-1,TimeUnit.MILLISECONDS,()->{
            runnable.run();
            return null;
        } );
    }
    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

}
