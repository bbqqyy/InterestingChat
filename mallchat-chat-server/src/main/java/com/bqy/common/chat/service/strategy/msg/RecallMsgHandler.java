package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.entity.msg.MsgRecall;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.common.domain.dto.ChatMsgRecallDTO;
import com.bqy.common.common.event.MessageRecallEvent;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.cache.UserCache;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

public class RecallMsgHandler extends AbstractMessageHandler<Object>{
    @Resource
    private UserCache userCache;
    @Resource
    private MessageDao messageDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    protected void saveMessage(Message message, Object body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        if(!Objects.equals(recall.getRecallUid(),msg.getFromUid())){
            return "管理员\""+userInfo.getName()+"\"撤回了一条成员消息";
        }
        return "\""+userInfo.getName()+"\"撤回了一条消息";

    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }
    public void recall(Long recallUid,Message message){
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid,new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this,new ChatMsgRecallDTO(message.getId(),message.getRoomId(),recallUid)));
    }
}
