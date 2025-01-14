package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMemberListResp;
import com.bqy.common.chat.domain.vo.resp.ChatRoomResp;
import com.bqy.common.chat.domain.vo.resp.MemberResp;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;

import java.util.List;

public interface RoomAppService {
    MemberResp getGroupDetail(Long uid, long id);

    CursorPageBaseResp<ChatRoomResp> getContactPage(Long uid, CursorPageBaseReq req);

    ChatRoomResp getContactDetail(Long uid, Long roomId);

    ChatRoomResp getContactDetailByFriend(Long uid, Long uid1);

    CursorPageBaseResp<ChatMemberResp> getGroupMemberPage(MemberReq memberReq);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq req);

    void delMember(Long uid, MemberDelReq req);

    Long addGroup(Long uid, GroupAddReq req);

    void addMember(Long uid, MemberAddReq req);
}
