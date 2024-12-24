package com.bqy.common.common.interceptor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import com.bqy.common.common.exception.HttpErrorCode;
import com.bqy.common.user.service.LoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.rmi.server.UID;
import java.util.Optional;
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String UID = "uid";
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    @Resource
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long uid = loginService.getValidUid(token);
        if(ObjectUtil.isNotNull(uid)){
            request.setAttribute(UID,uid);
        }else {
            boolean isPublic = isPublic(request);
            if(!isPublic){
                HttpErrorCode.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;


    }

    private boolean isPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        return split.length>3&&"public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h->h.startsWith(BEARER))
                .map(h->h.replace(BEARER,""))
                .orElse(null);
    }

}
