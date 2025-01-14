package com.bqy.common.common.event.listener;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.dao.MessageMarkDao;
import com.bqy.common.chat.domain.dto.ChatMsgMarkDTO;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.MessageMark;
import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.common.event.MsgMarkEvent;
import com.bqy.common.user.domain.enums.IdempotentEnum;
import com.bqy.common.user.service.IUserBackpackService;
import com.bqy.common.user.service.PushService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class MsgMarkListener {

    @Resource
    private MessageDao messageDao;

    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private IUserBackpackService userBackpackService;

    @Resource
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MsgMarkEvent.class,fallbackExecution = true)
    public void changeMsgType(MsgMarkEvent event){
        ChatMsgMarkDTO chatMsgMarkDTO = event.getChatMsgMarkDTO();
        Message message = messageDao.getById(chatMsgMarkDTO.getMsgId());
        if(!ObjectUtil.equals(message.getType(), MessageTypeEnum.TEXT.getType())){
            return;
        }
        Integer markCount = messageMarkDao.getMarkCount(chatMsgMarkDTO.getMsgId(),chatMsgMarkDTO.getMarkType());
        MessageMarkTypeEnum messageMarkTypeEnum = MessageMarkTypeEnum.of(message.getType());
        if(markCount<messageMarkTypeEnum.getRiseNum()){
            return;
        }
        if(messageMarkTypeEnum.getType().equals(MessageMarkTypeEnum.LIKE.getType())){
            userBackpackService.acquireItem(chatMsgMarkDTO.getUid(), chatMsgMarkDTO.getMsgId(), IdempotentEnum.MSG_ID,message.getId().toString());
        }
    }
    @Async
    @TransactionalEventListener(classes = MsgMarkEvent.class,fallbackExecution = true)
    public void notifyAll(MsgMarkEvent event){
        ChatMsgMarkDTO chatMsgMarkDTO = event.getChatMsgMarkDTO();
        Integer markCount = messageMarkDao.getMarkCount(chatMsgMarkDTO.getMsgId(),chatMsgMarkDTO.getMarkType());
        pushService.sendPushMsg(WebSocketAdapter.buildMsgMarkSend(chatMsgMarkDTO,markCount));
    }
}
