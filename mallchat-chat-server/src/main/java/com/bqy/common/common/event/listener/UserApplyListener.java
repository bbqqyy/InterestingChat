package com.bqy.common.common.event.listener;

import com.bqy.common.common.event.UserApplyEvent;
import com.bqy.common.user.dao.UserApplyDao;
import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.service.PushService;
import com.bqy.common.websocket.domain.vo.resp.WSFriendApply;
import com.bqy.common.websocket.service.WebSocketService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserApplyListener{

    @Resource
    private UserApplyDao userApplyDao;
    @Resource
    private WebSocketService webSocketService;

    @Resource
    private PushService pushService;
    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class,fallbackExecution = true)
    public void notifyFriend(UserApplyEvent userApplyEvent){
        UserApply userApply = userApplyEvent.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        pushService.sendPushMsg(WebSocketAdapter.buildApplySend(new WSFriendApply(userApply.getUid(),unReadCount)),userApply.getTargetId());

    }
}
