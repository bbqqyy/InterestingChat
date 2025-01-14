package com.bqy.common.chat.service.adapter;

import com.bqy.common.chat.domain.entity.Contact;
import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.chat.domain.vo.req.ChatMessageReq;
import com.bqy.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.bqy.common.user.domain.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomAdapter {
    public static List<ChatMessageReadResp> buildReadResp(List<Contact> contactList) {
        return contactList.stream()
                .map(contact -> {
                    ChatMessageReadResp resp = new ChatMessageReadResp();
                    resp.setUid(contact.getUid());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static List<GroupMember> buildGroupMemberBatch(List<Long> uidList, Long groupId) {
        return uidList.stream()
                .map(uid->{
                    GroupMember groupMember = new GroupMember();
                    groupMember.setGroupId(groupId);
                    groupMember.setUid(uid);
                    return groupMember;
                }).collect(Collectors.toList());
    }

    public static ChatMessageReq buildGroupAddMessage(RoomGroup roomGroup, User user, Map<Long, User> batch) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomGroup.getRoomId());
        chatMessageReq.setMsgType(MessageTypeEnum.SYSTEM.getType());
        StringBuilder sb = new StringBuilder();
        sb.append("\"")
                .append(user.getName())
                .append("\"")
                .append("邀请")
                .append(batch.values().stream().map(u -> "\"" + u.getName() + "\"").collect(Collectors.joining(",")))
                .append("加入群聊");
        chatMessageReq.setBody(sb.toString());
        return chatMessageReq;

    }
}
