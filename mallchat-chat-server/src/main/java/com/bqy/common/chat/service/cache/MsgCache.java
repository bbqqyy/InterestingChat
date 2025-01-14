package com.bqy.common.chat.service.cache;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MsgCache {
    @Resource
    private MessageDao messageDao;

    @Cacheable(cacheNames = "msg",key = "'msg'+#msgId")
    public Message getMsg(Long msgId){
        return messageDao.getById(msgId);
    }
    @CacheEvict(cacheNames = "msg",key = "'msg'+#msgId")
    public Message evictMsg(Long msgId){
        return null;
    }
}
