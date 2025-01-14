package com.bqy.common.common.event;

import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.service.IUserFriendService;
import com.bqy.common.user.service.impl.IUserFriendServiceImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserApplyEvent extends ApplicationEvent {
    private UserApply userApply;
    public UserApplyEvent(Object resource, UserApply userApply) {
        super(resource);
        this.userApply = userApply;
    }
}
