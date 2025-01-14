package com.bqy.common.user.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bqy.common.chat.domain.enums.ChatActiveStatusEnum;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.CursorUtils;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import com.bqy.common.user.domain.enums.UserStatusEnum;
import com.bqy.common.user.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    public List<User> getFriendList(List<Long> friendIdList) {
        return lambdaQuery()
                .in(User::getId, friendIdList)
                .orderByDesc(User::getId)
                .select(User::getId, User::getActiveStatus,User::getName, User::getAvatar)
                .list();
    }

    public Integer getOnlineCount(List<Long> memberUidList) {
        return lambdaQuery()
                .in(User::getId,memberUidList)
                .eq(User::getActiveStatus,UserStatusEnum.ONLINE.getStatus())
                .count();
    }

    public CursorPageBaseResp<User> getCursorPage(List<Long> memberUidList, CursorPageBaseReq cursorPageBaseReq, ChatActiveStatusEnum online) {
        return CursorUtils.getCursorPageByMySql(this,cursorPageBaseReq,wrapper->{
            wrapper.eq(User::getActiveStatus,online);
            wrapper.in(CollectionUtil.isNotEmpty(memberUidList),User::getId,memberUidList);
        },User::getLastOptTime);
    }

    public List<User> getMemberList() {
        return lambdaQuery()
                .eq(User::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .orderByDesc(User::getLastOptTime)
                .last("limit 1000")
                .select(User::getId,User::getName,User::getAvatar)
                .list();
    }
}
