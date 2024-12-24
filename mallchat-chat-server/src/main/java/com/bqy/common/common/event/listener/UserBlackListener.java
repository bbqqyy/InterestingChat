package com.bqy.common.common.event.listener;

import com.bqy.common.common.event.UserBlackEvent;
import com.bqy.common.common.event.UserOnlineEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.UserStatusEnum;
import com.bqy.common.user.service.IpService;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.websocket.service.WebSocketService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserBlackListener {

    @Resource
    private UserDao userDao;


    @Resource
    private WebSocketService webSocketService;

    @Resource
    private UserCache userCache;

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void blackUser(UserBlackEvent userBlackEvent) {
        User user = userBlackEvent.getUser();
        webSocketService.sendMessage(WebSocketAdapter.buildBlack(user));
    }
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void changeUserStatus(UserBlackEvent userBlackEvent) {
        userDao.invalidUid(userBlackEvent.getUser().getId());
    }
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void evictCache(UserBlackEvent userBlackEvent) {
        userCache.evictBlackMap();
    }
}
