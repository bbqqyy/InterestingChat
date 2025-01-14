package com.bqy.common.user.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.user.domain.entity.UserFriend;
import com.bqy.common.user.domain.vo.req.FriendApplyReq;
import com.bqy.common.user.mapper.UserFriendMapper;
import com.bqy.common.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    public CursorPageBaseResp<UserFriend> getFriendPage(Long uid, CursorPageBaseReq request) {
        LambdaQueryChainWrapper<UserFriend> wrapper = lambdaQuery();
        wrapper.lt(UserFriend::getId, request.getCursor());
        wrapper.orderByDesc(UserFriend::getId);
        wrapper.eq(UserFriend::getUid, uid);
        Page<UserFriend> page = page(request.plusPage(), wrapper);
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(UserFriend::getId)
                .map(String::valueOf)
                .orElse(null);
        boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());

    }

    public List<UserFriend> getFriendByIds(Long uid, List<Long> uidList) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .in(UserFriend::getFriendUid, uidList)
                .list();
    }


    public UserFriend getByFriend(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserFriend::getUid,uid)
                .eq(UserFriend::getFriendUid,targetUid)
                .one();
    }

    public List<UserFriend> getFriendUser(Long uid, Long targetId) {
        return lambdaQuery()
                .select(UserFriend::getId)
                .eq(UserFriend::getUid,uid)
                .eq(UserFriend::getFriendUid,targetId)
                .or()
                .eq(UserFriend::getFriendUid,uid)
                .eq(UserFriend::getUid,targetId)
                .list();
    }
}
