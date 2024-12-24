package com.bqy.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public enum HttpErrorCode {
    ACCESS_DENIED(401,"登录失效，请重新登录");

    private Integer code;
    private String desc;

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(this.code);
        response.setContentType(ContentType.JSON.toString(StandardCharsets.UTF_8));
        response.getWriter().write(JSONUtil.toJsonStr(ApiResult.fail(this.code,this.desc)));
    }
}
