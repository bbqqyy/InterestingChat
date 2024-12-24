package com.bqy.common.websocket;


import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.getUri());
            Optional<String> tokenOption = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k->k.get("token"))
                    .map(CharSequence::toString);
            tokenOption.ifPresent(k->NettyUtil.setAttr(ctx.channel(),NettyUtil.TOKEN,k));
            request.setUri(urlBuilder.getPath().toString());
            //取用户ip
            String ip = request.headers().get("X-Real-IP");
            if(StringUtils.isBlank(ip)){
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = inetSocketAddress.getAddress().getHostAddress();
            }
            //保存ip
            NettyUtil.setAttr(ctx.channel(),NettyUtil.IP,ip);
            //处理器只需要用一次
            ctx.pipeline().remove(this);
        }
            ctx.fireChannelRead(msg);
    }
    //    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        final HttpObject httpObject = (HttpObject) msg;
//
//        if (httpObject instanceof HttpRequest) {
//            final HttpRequest req = (HttpRequest) httpObject;
//            String token = req.headers().get("Sec-WebSocket-Protocol");
//            Attribute<Object> token1 = ctx.channel().attr(AttributeKey.valueOf("token"));
//            token1.set(token);
//            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
//                    req.getUri(),
//                    token, false);
//            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
//            if (handshaker == null) {
//                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//            } else {
//                // Ensure we set the handshaker and replace this handler before we
//                // trigger the actual handshake. Otherwise we may receive websocket bytes in this handler
//                // before we had a chance to replace it.
//                //
//                // See https://github.com/netty/netty/issues/9471.
//                ctx.pipeline().remove(this);
//
//                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
//                handshakeFuture.addListener(new ChannelFutureListener() {
//                    @Override
//                    public void operationComplete(ChannelFuture future) {
//                        if (!future.isSuccess()) {
//                            ctx.fireExceptionCaught(future.cause());
//                        } else {
//                            // Kept for compatibility
//                            ctx.fireUserEventTriggered(
//                                    WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
//                        }
//                    }
//                });
//            }
//        } else
//            ctx.fireChannelRead(msg);
//    }
}
