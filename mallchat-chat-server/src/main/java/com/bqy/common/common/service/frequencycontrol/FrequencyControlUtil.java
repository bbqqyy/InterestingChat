package com.bqy.common.common.service.frequencycontrol;

import com.bqy.common.common.annotation.FrequencyControl;
import com.bqy.common.common.domain.dto.FrequencyControlDTO;
import com.bqy.common.common.utils.AssertUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.units.qual.K;

import java.util.List;

public class FrequencyControlUtil {
    public static <T,K extends FrequencyControlDTO> T executeWithFrequencyControl(String strategyName,K frequencyControl,AbstractFrequencyControlService.SupplierThrowWithoutParam<T> supplier) throws Throwable{
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        return frequencyController.executeWithFrequencyControl(frequencyControl,supplier);
    }
    public static <K extends FrequencyControlDTO> void executeWithFrequencyControl(String strategyName,K frequencyControl,AbstractFrequencyControlService.Executor executor) throws Throwable{
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        frequencyController.executeWithFrequencyControl(frequencyControl,()->{
            executor.execute();
            return null;
        });
    }
    public static <T,K extends FrequencyControlDTO> T executeWithFrequencyControlList(String strategyName, List<K> frequencyControlList,AbstractFrequencyControlService.SupplierThrowWithoutParam<T> supplier) throws Throwable{
        boolean existsFrequencyControlHasNullKey = frequencyControlList.stream().anyMatch(frequencyControl-> ObjectUtils.isEmpty(frequencyControl.getKey()));
        AssertUtil.isFalse(existsFrequencyControlHasNullKey,"限流策略的key不允许出现空值");
        AbstractFrequencyControlService<K> frequencyController = FrequencyControlStrategyFactory.getFrequencyControllerByName(strategyName);
        return frequencyController.executeWithFrequencyControlList(frequencyControlList,supplier);

    }
    private FrequencyControlUtil(){

    }
 }
