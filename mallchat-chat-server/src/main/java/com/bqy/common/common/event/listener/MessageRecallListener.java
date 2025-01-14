package com.bqy.common.common.event.listener;

import com.bqy.common.chat.service.cache.MsgCache;
import com.bqy.common.common.domain.dto.ChatMsgRecallDTO;
import com.bqy.common.common.event.MessageRecallEvent;
import com.bqy.common.user.service.PushService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Slf4j
@Component
public class MessageRecallListener {
    @Resource
    private MsgCache msgCache;
    @Resource
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class,fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event){
        ChatMsgRecallDTO recallDTO = event.getChatMsgRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }
    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class,fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event){
        pushService.sendPushMsg(WebSocketAdapter.buildMsgRecall(event.getChatMsgRecallDTO()));
    }
}
