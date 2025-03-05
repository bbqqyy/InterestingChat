package com.bqy.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.bqy.common.common.annotation.FrequencyControl;
import com.bqy.common.common.domain.dto.FrequencyControlDTO;
import com.bqy.common.common.service.frequencycontrol.AbstractFrequencyControlService;
import com.bqy.common.common.service.frequencycontrol.FrequencyControlStrategyFactory;
import com.bqy.common.common.service.frequencycontrol.FrequencyControlUtil;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.common.utils.SpElUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
public class FrequencyControlAspect {
    @Around("@annotation(com.bqy.common.common.annotation.FrequencyControl)||@annotation(com.bqy.common.common.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint)throws Throwable{
        Method method = ((MethodSignature)proceedingJoinPoint.getSignature()).getMethod();
        FrequencyControl[] annotation = method.getAnnotationsByType(FrequencyControl.class);
        Map<String,FrequencyControl> keyMap = new HashMap<>();
        for (int i = 0; i < annotation.length; i++) {
            FrequencyControl frequencyControl = annotation[i];
            String prefix = StrUtil.isBlank(frequencyControl.prefixKey())? SpElUtil.getMethodKey(method)+"index:"+i:frequencyControl.prefixKey();
            String key = "";
            switch (frequencyControl.target()){
                case EL:
                    key = SpElUtil.parseSpEl(method,proceedingJoinPoint.getArgs(),frequencyControl.spEl());
                    break;
                case IP:
                    key = RequestHolder.get().getIp();
                    break;
                case UID:
                    key = RequestHolder.get().getUid().toString();
            }
            keyMap.put(prefix+":"+key,frequencyControl);
        }
        List<FrequencyControlDTO> frequencyControlDTOS = keyMap.entrySet().stream().map(entry->buildFrequencyControlDTO(entry.getKey(),entry.getValue())).collect(Collectors.toList());
        return FrequencyControlUtil.executeWithFrequencyControlList(FrequencyControlStrategyFactory.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER,frequencyControlDTOS,proceedingJoinPoint::proceed);
    }
    private FrequencyControlDTO buildFrequencyControlDTO(String key,FrequencyControl frequencyControl){
        FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
        frequencyControlDTO.setCount(frequencyControl.count());
        frequencyControlDTO.setKey(key);
        frequencyControlDTO.setTime(frequencyControl.time());
        frequencyControlDTO.setUnit(frequencyControl.timeUnit());
        return frequencyControlDTO;
    }
}
