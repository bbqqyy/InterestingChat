package com.bqy.common.common.event.listener;

import com.bqy.common.common.event.UserOnlineEvent;
import com.bqy.common.common.event.UserRegisterEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.IdempotentEnum;
import com.bqy.common.user.domain.enums.ItemEnum;
import com.bqy.common.user.domain.enums.UserStatusEnum;
import com.bqy.common.user.service.IUserBackpackService;
import com.bqy.common.user.service.IpService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

@Component
public class UserOnlineListener {

    @Resource
    private UserDao userDao;

    @Resource
    private IpService ipService;

    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void saveDB(UserOnlineEvent userOnlineEvent) {
        User user = userOnlineEvent.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserStatusEnum.ONLINE.getStatus());
        userDao.updateById(update);
        ipService.refreshIpDetailAsync(user.getId());
    }
}
