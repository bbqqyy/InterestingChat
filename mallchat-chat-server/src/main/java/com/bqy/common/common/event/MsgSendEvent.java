package com.bqy.common.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class MsgSendEvent extends ApplicationEvent {
    private Long msgId;
    public MsgSendEvent(Object source,Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
