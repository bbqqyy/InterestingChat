package com.bqy.mallchat.transaction.service;

import cn.hutool.core.util.ObjectUtil;

public class SecureInvokeHolder {
    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();
    public static boolean isInvoking(){
        return ObjectUtil.isNotNull(INVOKE_THREAD_LOCAL.get());
    }
    public static void setInvoking(){
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }
    public static void removeInvoke(){
        INVOKE_THREAD_LOCAL.remove();
    }
}
