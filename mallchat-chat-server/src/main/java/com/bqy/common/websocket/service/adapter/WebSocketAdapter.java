package com.bqy.common.websocket.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.bqy.common.chat.domain.dto.ChatMsgMarkDTO;
import com.bqy.common.chat.domain.enums.ChatActiveStatusEnum;
import com.bqy.common.chat.domain.vo.resp.ChatMemberStatisticResp;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.common.domain.dto.ChatMsgRecallDTO;
import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.websocket.domain.enums.WSRespTypeEnum;
import com.bqy.common.websocket.domain.vo.resp.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.Collections;

public class WebSocketAdapter {
    @Resource
    private static ChatService chatService;

    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsBaseResp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildResp(User user, String token, boolean isPower) {
        WSBaseResp<WSLoginSuccess> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        WSLoginSuccess wsLoginSuccess = WSLoginSuccess.builder()
                .name(user.getName())
                .avatar(user.getAvatar())
                .token(token)
                .uid(user.getId())
                .power(isPower ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();
        wsBaseResp.setData(wsLoginSuccess);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildInvalidTokenResp() {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildBlack(User user) {
        WSBaseResp<WSBlack> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.BLACK.getType());
        WSBlack wsBlack = WSBlack.builder()
                .uid(user.getId())
                .build();
        wsBaseResp.setData(wsBlack);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildApplySend(WSFriendApply wsFriendApply) {
        WSBaseResp<WSFriendApply> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.APPLY.getType());
        wsBaseResp.setData(wsFriendApply);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgRecall(ChatMsgRecallDTO chatMsgRecallDTO) {
        WSBaseResp<WSMsgRecall> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.RECALL.getType());
        WSMsgRecall recall = new WSMsgRecall();
        BeanUtils.copyProperties(chatMsgRecallDTO,recall);
        wsBaseResp.setData(recall);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildMsgMarkSend(ChatMsgMarkDTO chatMsgMarkDTO, Integer markCount) {
        WSMsgMark.WSMsgMarkItem item = new WSMsgMark.WSMsgMarkItem();
        BeanUtils.copyProperties(chatMsgMarkDTO,item);
        item.setMarkCount(markCount);
        WSBaseResp<WSMsgMark> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MARK.getType());
        WSMsgMark wsMsgMark = new WSMsgMark();
        wsMsgMark.setMarkList(Collections.singletonList(item));
        wsBaseResp.setData(wsMsgMark);
        return wsBaseResp;
    }

    public static WSBaseResp<?> buildOfflineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify wsOnlineOfflineNotify = new WSOnlineOfflineNotify();
        wsOnlineOfflineNotify.setChangeList(Collections.singletonList(buildOfflineInfo(user)));
        assembleNum(wsOnlineOfflineNotify);
        wsBaseResp.setData(wsOnlineOfflineNotify);
        return wsBaseResp;
    }
    public static WSBaseResp<?> buildOnlineNotifyResp(User user) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        WSOnlineOfflineNotify wsOnlineOfflineNotify = new WSOnlineOfflineNotify();
        wsOnlineOfflineNotify.setChangeList(Collections.singletonList(buildOnlineInfo(user)));
        assembleNum(wsOnlineOfflineNotify);
        wsBaseResp.setData(wsOnlineOfflineNotify);
        return wsBaseResp;
    }

    private static void assembleNum(WSOnlineOfflineNotify wsOnlineOfflineNotify) {
        ChatMemberStatisticResp memberStatistic = chatService.getMemberStatistic();
        wsOnlineOfflineNotify.setOnlineNum(memberStatistic.getOnlineNum());
    }
    private static ChatMemberResp buildOnlineInfo(User user) {
        ChatMemberResp info = new ChatMemberResp();
        BeanUtil.copyProperties(user, info);
        info.setUid(user.getId());
        info.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        info.setLastOptTime(user.getLastOptTime());
        return info;
    }

    private static ChatMemberResp buildOfflineInfo(User user) {
        ChatMemberResp chatMemberResp = new ChatMemberResp();
        BeanUtil.copyProperties(user,chatMemberResp);
        chatMemberResp.setUid(user.getId());
        chatMemberResp.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        chatMemberResp.setLastOptTime(user.getLastOptTime());
        return chatMemberResp;
    }

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp resp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        wsBaseResp.setData(resp);
        return wsBaseResp;
    }
}
