package com.bqy.common.common.thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
@AllArgsConstructor
@Slf4j
public class MyThreadFactory implements ThreadFactory {
    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();
    private final ThreadFactory originalThreadFactory;


    @Override
    public Thread newThread(Runnable r) {
        Thread thread = originalThreadFactory.newThread(r);
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
