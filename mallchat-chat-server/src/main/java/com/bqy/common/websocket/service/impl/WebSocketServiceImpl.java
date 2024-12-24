package com.bqy.common.websocket.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.bqy.common.common.config.ThreadPoolConfig;
import com.bqy.common.common.event.UserOnlineEvent;
import com.bqy.common.common.event.UserRegisterEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.IpInfo;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.LoginService;
import com.bqy.common.websocket.NettyUtil;
import com.bqy.common.websocket.domain.dto.WSChannelExtraDTO;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.service.WebSocketService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Resource
    @Lazy
    private WxMpService wxMpService;
    @Resource
    private UserDao userDao;
    @Resource
    private LoginService loginService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private IRoleService roleService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolExecutor;

    private static final Integer MAX_SIZE = 10000;
    private static final Duration DURATION_TIME = Duration.ofHours(1);
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    private static final Cache<Integer,Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterWrite(DURATION_TIME)
            .build();
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel,new WSChannelExtraDTO());

    }
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateCode(channel);
        try {
            //生成带参数二维码
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION_TIME.getSeconds());
            //发送给前端
            sendMessage(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
        }catch (WxErrorException e){
            log.error(e.getMessage());
        }


    }

    @Override
    public void offLine(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        //todo 用户下线
    }

    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(ObjectUtil.isNull(channel)){
            return;
        }
        User user = userDao.getById(id);
        WAIT_LOGIN_MAP.invalidate(code);
        String token = loginService.login(id);
        loginSuccess(channel,user,token);

    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if(ObjectUtil.isNull(channel)){
            return;
        }
        sendMessage(channel,WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if(ObjectUtil.isNotNull(uid)){
            User user = userDao.getById(uid);
            loginSuccess(channel,user,token);

        }else {
            sendMessage(channel,WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMessage(WSBaseResp<?> msg) {
        ONLINE_WS_MAP.forEach((channel,ext)->{
            threadPoolExecutor.execute(()->{
                sendMessage(channel,msg);
            });

        });
    }

    private void loginSuccess(Channel channel, User user, String token) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        sendMessage(channel,WebSocketAdapter.buildResp(user,token,roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        //用户成功上线的消息
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel,NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this,user));

    }

    private void sendMessage(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    private Integer generateCode(Channel channel) {

        Integer code;
        do{
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        }while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code,channel)));
        return code;
    }
}
