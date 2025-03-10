package com.bqy.common.common.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final GlobalUncaughtExceptionHandler instance = new GlobalUncaughtExceptionHandler();
    private GlobalUncaughtExceptionHandler(){

    }
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      log.error("Exception in Thread{}",t.getName(),e);
    }
    public static GlobalUncaughtExceptionHandler getInstance(){
        return instance;
    }
}
