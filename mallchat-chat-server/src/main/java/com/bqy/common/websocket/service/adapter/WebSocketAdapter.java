package com.bqy.common.websocket.service.adapter;

import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.websocket.domain.enums.WSRespTypeEnum;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.domain.vo.resp.WSBlack;
import com.bqy.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.bqy.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {
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
}
