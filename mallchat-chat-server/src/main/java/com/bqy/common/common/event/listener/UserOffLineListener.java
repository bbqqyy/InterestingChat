package com.bqy.common.common.event.listener;

import com.bqy.common.chat.domain.enums.ChatActiveStatusEnum;
import com.bqy.common.common.event.UserOffLineEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.websocket.service.WebSocketService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UserOffLineListener {
    @Resource
    private UserCache userCache;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private UserDao userDao;

    @Async
    @EventListener(classes = UserOffLineEvent.class)
    public void saveRedisAndPush(UserOffLineEvent userOffLineEvent){
        User user = userOffLineEvent.getUser();
        userCache.offLine(user.getId(),user.getLastOptTime());
        webSocketService.sendToAllOnline(WebSocketAdapter.buildOfflineNotifyResp(user),user.getId());
    }
    @Async
    @EventListener(classes = UserOffLineEvent.class)
    public void saveDB(UserOffLineEvent userOffLineEvent){
        User user = userOffLineEvent.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(update);
    }
}
