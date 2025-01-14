package com.bqy.common.common.event.listener;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.event.ItemReceiveEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.entity.UserBackpack;
import com.bqy.common.user.domain.enums.ItemTypeEnum;
import com.bqy.common.user.service.cache.ItemCache;
import com.bqy.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ItemReceiveListener {
    @Resource
    private ItemCache itemCache;
    @Resource
    private UserDao userDao;
    @Resource
    private UserCache userCache;
    @Async
    @EventListener(classes = ItemReceiveEvent.class)
    public void wear(ItemReceiveEvent itemReceiveEvent){
        UserBackpack userBackpack = itemReceiveEvent.getUserBackpack();
        ItemConfig itemConfig = itemCache.getById(userBackpack.getItemId());
        if(itemConfig.getType().equals(ItemTypeEnum.BADGE.getType())){
            User user = userDao.getById(userBackpack.getUid());
            if(ObjectUtil.isNull(user.getItemId())){
                userDao.wearBadge(userBackpack.getUid(),userBackpack.getItemId());
                userCache.userInfoChange(userBackpack.getUid());
            }
        }
    }
}
