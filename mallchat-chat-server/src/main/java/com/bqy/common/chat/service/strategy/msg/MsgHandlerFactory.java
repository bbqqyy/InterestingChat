package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.common.exception.CommonErrorEnum;
import com.bqy.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

public class MsgHandlerFactory {
    public static final Map<Integer,AbstractMessageHandler> map = new HashMap<>();
    public static void register(Integer code,AbstractMessageHandler messageHandler){
        map.put(code,messageHandler);
    }

    public static AbstractMessageHandler<?> getStrategyNoNull(Integer type) {
        AbstractMessageHandler messageHandler = map.get(type);
        AssertUtil.isNotEmpty(messageHandler, CommonErrorEnum.PARAM_INVALID);
        return messageHandler;
    }
}
