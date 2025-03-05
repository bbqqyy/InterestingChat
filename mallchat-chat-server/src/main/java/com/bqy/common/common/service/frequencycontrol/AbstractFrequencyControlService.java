package com.bqy.common.common.service.frequencycontrol;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.annotation.FrequencyControl;
import com.bqy.common.common.domain.dto.FrequencyControlDTO;
import com.bqy.common.common.exception.CommonErrorEnum;
import com.bqy.common.common.exception.FrequencyControlException;
import com.bqy.common.common.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public abstract class AbstractFrequencyControlService<K extends FrequencyControlDTO> {
    @PostConstruct
    protected void registerMyselfToFactory(){
        FrequencyControlStrategyFactory.registerFrequencyController(getStrategyName(),this);
    }
    private <T> T executeWithFrequencyControlMap(Map<String,K> frequencyControlMap,SupplierThrowWithoutParam<T> supplier) throws Throwable{
        if(reachRateLimit(frequencyControlMap)){
            throw new FrequencyControlException(CommonErrorEnum.FREQUENCY_LIMIT);
        }
        try {
            return supplier.get();
        }finally {
            addFrequencyControlStatisticsCount(frequencyControlMap);
        }
    }
    @SuppressWarnings("unchecked")
    public <T> T executeWithFrequencyControlList(List<K> frequencyControlList,SupplierThrowWithoutParam<T> supplier) throws Throwable{
        boolean existsFrequencyControlHasNullKey = frequencyControlList.stream().anyMatch(frequencyControl-> ObjectUtils.isEmpty(frequencyControl.getKey()));
        AssertUtil.isFalse(existsFrequencyControlHasNullKey,"限流策略的Key字段不允许出现空值");
        Map<String,FrequencyControlDTO> frequencyControlDTOMap = frequencyControlList.stream().collect(Collectors.groupingBy(FrequencyControlDTO::getKey,Collectors.collectingAndThen(Collectors.toList(),list->list.get(0))));
        return executeWithFrequencyControlMap((Map<String,K>)frequencyControlDTOMap,supplier);
    }
    public <T> T executeWithFrequencyControl(K frequencyControl,SupplierThrowWithoutParam<T> supplier) throws Throwable{
        return executeWithFrequencyControlList(Collections.singletonList(frequencyControl),supplier);
    }

    protected abstract void addFrequencyControlStatisticsCount(Map<String,K> frequencyControlMap);

    protected abstract boolean reachRateLimit(Map<String,K> frequencyControlMap);
    protected abstract String getStrategyName();
    @FunctionalInterface
    public interface SupplierThrowWithoutParam<T>{
        T get() throws Throwable;
    }
    @FunctionalInterface
    public interface Executor{
        void execute() throws Throwable;
    }

}
