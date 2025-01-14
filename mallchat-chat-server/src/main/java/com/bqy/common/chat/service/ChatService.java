package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.dto.MsgReadInfoDTO;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;

import java.util.Collection;
import java.util.List;

public interface ChatService {

    CursorPageBaseResp<ChatMessageResp> getMsgPage(Long uid, ChatMessagePageReq req);

    Long sendMessage(ChatMessageReq req, Long uid);

    ChatMessageResp getMsgResp(Long msgId, Long uid);

    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    void recallMessage(Long uid, ChatMessageBaseReq req);

    void markMessage(Long uid, ChatMsgMarkReq req);

    CursorPageBaseResp<ChatMessageReadResp> getReadMessagePage(Long uid, ChatMessageReadReq req);

    Collection<MsgReadInfoDTO> getMsgReadCount(Long uid, ChatMessageReadInfoReq req);

    void msgRead(Long uid, ChatMessageMemberReq req);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq memberReq);
}
