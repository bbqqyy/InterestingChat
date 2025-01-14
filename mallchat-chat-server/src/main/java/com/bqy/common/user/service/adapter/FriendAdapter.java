package com.bqy.common.user.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.domain.entity.UserFriend;
import com.bqy.common.user.domain.enums.ApplyReadStatusEnum;
import com.bqy.common.user.domain.enums.ApplyStatusEnum;
import com.bqy.common.user.domain.enums.ApplyTypeEnum;
import com.bqy.common.user.domain.vo.req.FriendApplyReq;
import com.bqy.common.user.domain.vo.resp.FriendApplyResp;
import com.bqy.common.user.domain.vo.resp.FriendCheckResp;
import com.bqy.common.user.domain.vo.resp.FriendResp;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendAdapter {

    public static List<FriendResp> buildFriend(List<UserFriend> userFriendList, List<User> friendList) {
        Map<Long,User> userMap = friendList.stream().collect(Collectors.toMap(User::getId,user->user));
        return userFriendList.stream()
                .map(userFriend -> {
                    FriendResp friendResp = new FriendResp();
                    friendResp.setUid(userFriend.getFriendUid());
                    User user = userMap.get(userFriend.getFriendUid());
                    if(ObjectUtil.isNotNull(user)){
                        friendResp.setActiveStatus(user.getActiveStatus());
                        friendResp.setAvatar(user.getAvatar());
                        friendResp.setName(user.getName());
                    }
                    return friendResp;
                }).collect(Collectors.toList());
    }

    public static UserApply buildFriendApply(Long uid, FriendApplyReq request) {
        UserApply userApply = new UserApply();
        userApply.setUid(uid);
        userApply.setType(ApplyTypeEnum.ADD_FRIEND.getCode());
        userApply.setTargetId(request.getTargetUid());
        userApply.setMsg(request.getMsg());
        userApply.setStatus(ApplyStatusEnum.WAIT_APPROVE.getStatus());
        userApply.setReadStatus(ApplyReadStatusEnum.UN_READ.getStatus());
        return userApply;

    }

    public static List<FriendApplyResp> buildFriendApplyList(List<UserApply> records) {
        return records.stream()
                .map(record->{
                    FriendApplyResp friendApplyResp = new FriendApplyResp();
                    friendApplyResp.setAvatar("");
                    friendApplyResp.setName("");
                    friendApplyResp.setUid(record.getUid());
                    friendApplyResp.setType(record.getType());
                    friendApplyResp.setMsg(record.getMsg());
                    friendApplyResp.setStatus(record.getStatus());
                    friendApplyResp.setApplyId(record.getId());
                    return friendApplyResp;
                }).collect(Collectors.toList());
    }
}
