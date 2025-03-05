package com.bqy.common.common.event;

import com.bqy.common.user.domain.entity.User;
import com.bqy.common.websocket.service.impl.WebSocketServiceImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserOffLineEvent extends ApplicationEvent {
    private final User user;
    public UserOffLineEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
