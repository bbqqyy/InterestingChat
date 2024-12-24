package com.bqy.common.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.bqy.common.common.domain.dto.RequestInfo;
import com.bqy.common.common.exception.HttpErrorCode;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.enums.BlackTypeEnum;
import com.bqy.common.user.service.cache.UserCache;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

@Component
public class BlackInterceptor implements HandlerInterceptor {
    @Resource
    private UserCache userCache;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        if(inBlackList(requestInfo.getUid(),blackMap.get(BlackTypeEnum.UID.getType()))){
            HttpErrorCode.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        if(inBlackList(requestInfo.getUid(),blackMap.get(BlackTypeEnum.IP.getType()))){
            HttpErrorCode.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> set) {
        if(ObjectUtil.isNull(target)|| CollectionUtil.isEmpty(set)){
            return false;
        }
        return set.contains(target.toString());
    }
}
