package com.bqy.common.websocket.service;

import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

import java.util.Optional;

public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    Boolean offLine(Channel channel, Optional<Long> uid);

    void scanLoginSuccess(Integer code, Long id);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendMessage(WSBaseResp<?> msg);

    void removed(Channel channel);

    void sendToAllOnline(WSBaseResp<?> buildOfflineNotifyResp, Long skipUid);
}
