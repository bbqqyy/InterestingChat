package com.bqy.common.chat.service.strategy.mark;

import com.bqy.common.common.exception.CommonErrorEnum;
import com.bqy.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

public class MsgMarkFactory {
    private static final Map<Integer,AbstractMessageMarkStrategy> map = new HashMap<>();

    public static void register(Integer markType,AbstractMessageMarkStrategy abstractMessageMarkStrategy){
        map.put(markType,abstractMessageMarkStrategy);
    }
    public static AbstractMessageMarkStrategy getStrategyNoNull(Integer markType){
        AbstractMessageMarkStrategy strategy = map.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_INVALID);
        return strategy;
    }
}
