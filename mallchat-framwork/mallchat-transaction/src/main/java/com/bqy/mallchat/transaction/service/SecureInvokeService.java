package com.bqy.mallchat.transaction.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.bqy.mallchat.transaction.dao.SecureInvokeRecordDao;
import com.bqy.mallchat.transaction.domain.dto.SecureInvokeDTO;
import com.bqy.mallchat.transaction.domain.entity.SecureInvokeRecord;
import com.bqy.mallchat.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class SecureInvokeService {
    public static final double RETRY_INTERVAL_MINUTES = 2D;
    private final SecureInvokeRecordDao secureInvokeRecordDao;
    private final Executor executor;
    @Scheduled(cron = "*/5 * * * * ?")
    public void retry(){
        List<SecureInvokeRecord> secureInvokeRecords = secureInvokeRecordDao.getWaitRetryRecords();
        secureInvokeRecords.forEach(secureInvokeRecord -> {
            doAsyncInvoke(secureInvokeRecord);
        });
    }
    public void save(SecureInvokeRecord record){
        secureInvokeRecordDao.save(record);
    }
    public void retryRecord(SecureInvokeRecord record,String errorMsg){
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord update = new SecureInvokeRecord();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if(retryTimes> record.getMaxRetryTimes()){
            update.setStatus(SecureInvokeRecord.STATUS_FAIL);
        }else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES,retryTimes);
        return DateUtil.offsetMinute(new Date(),(int) waitMinutes);
    }
    public void doAsyncInvoke(SecureInvokeRecord record){
        executor.execute(()->{
            System.out.println(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    public void invoke(SecureInvokeRecord record,boolean async){
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        if(!inTransaction){
            return;
        }
        save(record);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                if(async){
                    doAsyncInvoke(record);
                }else {
                    doInvoke(record);
                }
            }
        });
    }
    private void doInvoke(SecureInvokeRecord record) {
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeDTO();
        try{
            SecureInvokeHolder.setInvoking();
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JSONUtil.toList(secureInvokeDTO.getParameterTypes(),String.class);
            List<Class<?>> parameterClasses = getParameters(parameterStrings);
            Method method = ReflectUtil.getMethod(beanClass,secureInvokeDTO.getMethodName(),parameterClasses.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO,parameterClasses);
            method.invoke(bean,args);
            removeRecord(record.getId());
        }catch (Throwable e){
            log.error("SecureInvokeService invoke fail",e);
            retryRecord(record,e.getMessage());
        }finally {
            SecureInvokeHolder.removeInvoke();
        }
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(secureInvokeDTO.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for(int i = 0;i<jsonNode.size();i++){
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i),aClass);
        }
        return args;
    }

    private List<Class<?>> getParameters(List<String> parameterStrings) {
        return parameterStrings.stream().map(paramterString->{
            try {
                return Class.forName(paramterString);
            }catch (ClassNotFoundException e){
                log.error("secureInvokeService class not found",e);
            }
            return null;
        }).collect(Collectors.toList());
    }

}
