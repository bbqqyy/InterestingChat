package com.bqy.common.common.service.frequencycontrol;

import com.bqy.common.common.domain.dto.FrequencyControlDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FrequencyControlStrategyFactory {
    public static final String TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER = "TotalCountWithInFixTime";
    static Map<String,AbstractFrequencyControlService<?>> frequencyControlServiceMap = new ConcurrentHashMap<>(8);
    public static <K extends FrequencyControlDTO> void registerFrequencyController(String strategyName,AbstractFrequencyControlService<K> abstractFrequencyControlService){
        frequencyControlServiceMap.put(strategyName,abstractFrequencyControlService);
    }
    @SuppressWarnings("unchecked")
    public static <K extends FrequencyControlDTO> AbstractFrequencyControlService<K> getFrequencyControllerByName(String strategyName){
        return (AbstractFrequencyControlService<K>) frequencyControlServiceMap.get(strategyName);
    }
    private FrequencyControlStrategyFactory(){

    }
}
