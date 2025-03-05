package com.bqy.common.user.controller;

import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.vo.req.UploadUrlReq;
import com.bqy.common.user.service.OssService;
import com.bqy.mallchat.oss.domain.OssResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/capi/oss")
public class OssController {
    @Resource
    private OssService ossService;

    @GetMapping("/upload/url")
    @ApiOperation("临时上传文件")
    public ApiResult<OssResp> uploadUrl(@Valid UploadUrlReq req){
        return ApiResult.success(ossService.getUploadUrl(RequestHolder.get().getUid(), req));
    }
}
