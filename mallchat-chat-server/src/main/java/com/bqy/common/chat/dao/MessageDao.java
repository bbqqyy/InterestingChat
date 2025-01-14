package com.bqy.common.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.enums.MessageStatusEnum;
import com.bqy.common.chat.domain.vo.req.ChatMessageReq;
import com.bqy.common.chat.mapper.MessageMapper;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.CursorUtils;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    public CursorPageBaseResp<Message> getCursorPage(Long roomId, CursorPageBaseReq request, Long lastMsgId) {
        return CursorUtils.getCursorPageByMySql(this, request, wrapper -> {
            wrapper.eq(Message::getRoomId, roomId);
            wrapper.eq(Message::getStatus, MessageStatusEnum.NORMAL.getStatus());
            wrapper.le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId);
        }, Message::getId);
    }

    public Integer getGapCount(Long msgId, Long replyMsgId, Long roomId) {
        return lambdaQuery()
                .eq(Message::getRoomId,roomId)
                .gt(Message::getId,replyMsgId)
                .lt(Message::getId,msgId)
                .count();
    }

    public Integer getUnReadMessageCount(Long roomId, Date readTime) {
        return lambdaQuery()
                .eq(Message::getRoomId,roomId)
                .gt(Objects.nonNull(readTime),Message::getCreateTime,readTime)
                .count();
    }

    public boolean removeByRoomId(Long roomId, List<Long> uidList) {
        if(CollectionUtil.isNotEmpty(uidList)){
            LambdaUpdateWrapper<Message> updateWrapper = Wrappers.lambdaUpdate(Message.class)
                    .eq(Message::getRoomId,roomId)
                    .in(Message::getFromUid,uidList)
                    .set(Message::getStatus,MessageStatusEnum.DELETE.getStatus());
            return this.update(updateWrapper);

        }
        return false;
    }
}
