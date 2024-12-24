package com.bqy.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.UserService;
import com.bqy.common.user.service.WxMsgService;
import com.bqy.common.user.service.adapter.TextBuilder;
import com.bqy.common.user.service.adapter.UserAdapter;
import com.bqy.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {

    private static final ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    @Value("${wx.mp.callback}")
    private String callback;

    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    @Resource
    @Lazy
    private WxMpService wxMpService;

    @Resource
    private WebSocketService webSocketService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {

        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if(ObjectUtil.isNull(code)){
            return null;
        }
        User user = userDao.getByOpenId(openId);
        if(ObjectUtil.isNotNull(user)&& StrUtil.isNotBlank(user.getAvatar())){
            webSocketService.scanLoginSuccess(code,user.getId());
            return null;
        }
        if(ObjectUtil.isNull(user)){
            User newUser = User.builder()
                    .openId(openId)
                    .build();
            userService.register(newUser);
        }
        WAIT_AUTHORIZE_MAP.put(openId,code);
        webSocketService.waitAuthorize(code);
        String authorizeUrl = String.format(URL,wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录：<a href=\""+authorizeUrl+"\"> 登录 </a>",wxMpXmlMessage);


    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {

        String openId = userInfo.getOpenid();
        User user = userDao.getByOpenId(openId);
        if(StrUtil.isBlank(user.getAvatar())){
            fillUserInfo(user.getId(),userInfo);
        }
        Integer code = WAIT_AUTHORIZE_MAP.remove(openId);
        webSocketService.scanLoginSuccess(code,user.getId());


    }
    private void fillUserInfo(Long uid,WxOAuth2UserInfo userInfo){
        User user = UserAdapter.buildAuthorizeUser(uid,userInfo);
        userDao.updateById(user);
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {

        try{
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_","");
            return Integer.parseInt(code);
        }catch (Exception e){
            log.error("getEventKey error,EventKey:{}",wxMpXmlMessage.getEventKey(),e);
            return null;
        }

    }
}
