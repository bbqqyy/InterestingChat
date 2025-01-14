package com.bqy.common.common.event;

import com.bqy.common.chat.domain.dto.ChatMsgMarkDTO;
import com.bqy.common.chat.service.strategy.mark.AbstractMessageMarkStrategy;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class MsgMarkEvent extends ApplicationEvent {
    private final ChatMsgMarkDTO chatMsgMarkDTO;

    public MsgMarkEvent(Object source,ChatMsgMarkDTO chatMsgMarkDTO) {
        super(source);
        this.chatMsgMarkDTO = chatMsgMarkDTO;
    }
}
