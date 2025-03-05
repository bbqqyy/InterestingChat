package com.bqy.common.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.bqy.common.common.event.UserOffLineEvent;
import com.bqy.common.common.event.UserOnlineEvent;
import com.bqy.common.common.utils.RedisUtils;
import com.bqy.common.user.dao.UserDao;
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
import lombok.SneakyThrows;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

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

    /**
     * redis保存loginCode的key
     */
    private static final String LOGIN_CODE = "loginCode";

    private static final Integer MAX_SIZE = 10000;
    private static final Duration DURATION_TIME = Duration.ofHours(1);
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAX_SIZE)
            .expireAfterWrite(DURATION_TIME)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());

    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateCode(channel);
        try {
            //生成带参数二维码
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION_TIME.getSeconds());
            //发送给前端
            sendMessage(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
        } catch (WxErrorException e) {
            log.error(e.getMessage());
        }


    }

    private void onLine(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }

    @Override
    public Boolean offLine(Channel channel, Optional<Long> uid) {
        ONLINE_WS_MAP.remove(channel);
        if (uid.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> ObjectUtil.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uid.get()));
        }
        return true;
    }

    @Override
    public void scanLoginSuccess(Integer code, Long id) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (ObjectUtil.isNull(channel)) {
            return;
        }
        User user = userDao.getById(id);
        WAIT_LOGIN_MAP.invalidate(code);
        String token = loginService.login(id);
        loginSuccess(channel, user, token);

    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (ObjectUtil.isNull(channel)) {
            return;
        }
        sendMessage(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (ObjectUtil.isNotNull(uid)) {
            User user = userDao.getById(uid);
            loginSuccess(channel, user, token);

        } else {
            sendMessage(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMessage(WSBaseResp<?> msg) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            threadPoolExecutor.execute(() -> {
                sendMessage(channel, msg);
            });

        });
    }

    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        Optional<Long> uidOptional = Optional.ofNullable(wsChannelExtraDTO)
                .map(WSChannelExtraDTO::getUid);
        boolean offLineAll = offLine(channel, uidOptional);
        if (uidOptional.isPresent() && offLineAll) {
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOffLineEvent(this,user));
        }
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> buildOfflineNotifyResp, Long skipUid) {
        ONLINE_WS_MAP.forEach(((channel, wsChannelExtraDTO) -> {
            if(ObjectUtil.isNotNull(skipUid)&&ObjectUtil.equals(wsChannelExtraDTO.getUid(),skipUid)){
                return;
            }
            threadPoolExecutor.execute(()->sendMessage(channel,buildOfflineNotifyResp));
        }));
    }

    private void loginSuccess(Channel channel, User user, String token) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        sendMessage(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        //用户成功上线的消息
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));

    }

    private void sendMessage(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    private Integer generateCode(Channel channel) {

        Integer code;
        do {
            code = RedisUtils.inc(LOGIN_CODE, (int) DURATION_TIME.toMinutes(), TimeUnit.MINUTES);
        } while (WAIT_LOGIN_MAP.asMap().containsKey(code));
        //保存一份到本地
        WAIT_LOGIN_MAP.put(code, channel);
        return code;
    }
}
