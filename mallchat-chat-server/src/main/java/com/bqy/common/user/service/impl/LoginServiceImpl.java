package com.bqy.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.utils.JwtUtils;
import com.bqy.common.common.utils.RedisUtils;
import com.bqy.common.user.service.LoginService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    private static final int TOKEN_EXPIRE_DAYS = 3;
    @Resource
    private JwtUtils jwtUtils;
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expireDay = RedisUtils.getExpire(userTokenKey,TimeUnit.DAYS);
        if(expireDay==-2){
            return;
        }
        if(expireDay<1){
            RedisUtils.expire(userTokenKey,TOKEN_EXPIRE_DAYS,TimeUnit.DAYS);
        }
    }

    @Override
    public String login(Long id) {
        String token = jwtUtils.createToken(id);
        RedisUtils.set(getUserTokenKey(id),token,TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        Long uid = jwtUtils.getUidOrNull(token);
        if(ObjectUtil.isNull(uid)){
            return null;
        }
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        if(StrUtil.isBlank(oldToken)){
            return null;
        }
        return ObjectUtil.equal(token,oldToken)?uid:null;
    }

    private String getUserTokenKey(Long id){
        return RedisKey.getKey(RedisKey.REDIS_KEY,id);
    }
}
