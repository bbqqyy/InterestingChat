package com.bqy.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bqy.common.chat.domain.entity.MessageMark;
import com.bqy.common.chat.domain.enums.MessageStatusEnum;
import com.bqy.common.chat.mapper.MessageMapper;
import com.bqy.common.chat.mapper.MessageMarkMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public List<MessageMark> getValidMarkByMsgIdBatch(List<Long> msgIds) {
        return lambdaQuery()
                .in(MessageMark::getMsgId,msgIds)
                .eq(MessageMark::getStatus, MessageStatusEnum.NORMAL.getStatus())
                .list();
    }

    public MessageMark get(Long uid, Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId,msgId)
                .eq(MessageMark::getUid,uid)
                .eq(MessageMark::getType,markType)
                .one();
    }

    public Integer getMarkCount(Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId,msgId)
                .eq(MessageMark::getType,markType)
                .eq(MessageMark::getStatus,MessageStatusEnum.NORMAL.getStatus())
                .count();
    }
}
