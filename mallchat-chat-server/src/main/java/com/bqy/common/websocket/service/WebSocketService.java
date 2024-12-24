package com.bqy.common.websocket.service;

import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

public interface WebSocketService {

    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void offLine(Channel channel);

    void scanLoginSuccess(Integer code, Long id);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);

    void sendMessage(WSBaseResp<?> msg);
}
