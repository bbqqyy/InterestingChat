package com.bqy.common.common.utils;

import com.bqy.common.common.domain.dto.RequestInfo;

public class RequestHolder {
    private static final ThreadLocal<RequestInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static RequestInfo get() {
        return THREAD_LOCAL.get();
    }

    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
