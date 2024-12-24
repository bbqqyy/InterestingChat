package com.bqy.common.common.event.listener;

import com.bqy.common.common.event.UserRegisterEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.IdempotentEnum;
import com.bqy.common.user.domain.enums.ItemEnum;
import com.bqy.common.user.service.IUserBackpackService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserRegisterListener {
    @Resource
    private IUserBackpackService userBackpackService;

    @Resource
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent userRegisterEvent) {
        User user = userRegisterEvent.getUser();
        userBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent userRegisterEvent) {
        User user = userRegisterEvent.getUser();
        int registeredUser = userDao.count();
        if (registeredUser < 10) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (registeredUser < 100) {
            userBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }

    }
}
