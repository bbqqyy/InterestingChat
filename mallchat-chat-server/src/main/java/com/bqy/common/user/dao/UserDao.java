package com.bqy.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.UserStatusEnum;
import com.bqy.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-17
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User>{

    public User getByOpenId(String openId) {
//        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery(User.class)
//                .eq(User::getOpenId,openId);
//        return this.getOne(lambdaQueryWrapper);
          return lambdaQuery()
                  .eq(User::getOpenId,openId)
                  .one();
    }

    public User getByName(String name) {
        return lambdaQuery()
                .eq(User::getName,name)
                .one();
    }

    public boolean modifyName(Long uid, String name) {
        return lambdaUpdate()
                .eq(User::getId,uid)
                .set(User::getName,name)
                .update();
    }

    public void wearBadge(Long uid, Long itemId) {
        lambdaUpdate()
                .eq(User::getId,uid)
                .set(User::getItemId,itemId)
                .update();
    }

    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId,id)
                .set(User::getStatus, UserStatusEnum.OFFLINE.getStatus())
                .update();
    }
}
