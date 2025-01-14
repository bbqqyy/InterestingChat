package com.bqy.common.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.enums.GroupRoleEnum;
import com.bqy.common.chat.domain.vo.resp.ChatMemberListResp;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.websocket.domain.enums.WSRespTypeEnum;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.domain.vo.resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MemberAdapter {

    public static List<ChatMemberResp> buildMember(List<User> list) {
        return list.stream()
                .map(user -> {
                    ChatMemberResp chatMemberResp = new ChatMemberResp();
                    chatMemberResp.setUid(user.getId());
                    chatMemberResp.setActiveStatus(user.getActiveStatus());
                    chatMemberResp.setLastOptTime(user.getLastOptTime());
                    return chatMemberResp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(List<User> userList) {
        return userList.stream()
                .map(user -> {
                    ChatMemberListResp resp = new ChatMemberListResp();
                    BeanUtil.copyProperties(user,resp);
                    resp.setUid(user.getId());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(Map<Long, User> batch) {
        return buildMemberList(new ArrayList<>(batch.values()));
    }

    public static WSBaseResp<WSMemberChange> buildMemberRemoveWS(Long roomId, Long uid) {
        WSBaseResp<WSMemberChange> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setUid(uid);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_REMOVE);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

    public static WSBaseResp<WSMemberChange> buildMemberAddWS(Long roomId, User user) {
        WSBaseResp<WSMemberChange> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setActiveStatus(user.getActiveStatus());
        wsMemberChange.setLastOptTime(user.getLastOptTime());
        wsMemberChange.setUid(user.getId());
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_REMOVE);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

    public static List<GroupMember> buildMemberAdd(Long groupId, List<Long> waitAddUidList) {
        return waitAddUidList.stream().map(a -> {
            GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setUid(a);
            member.setRole(GroupRoleEnum.MEMBER.getType());
            return member;
        }).collect(Collectors.toList());
    }
}
