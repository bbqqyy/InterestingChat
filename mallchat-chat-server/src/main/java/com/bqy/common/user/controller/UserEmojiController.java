package com.bqy.common.user.controller;


import com.bqy.common.common.domain.vo.req.IdReqVO;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.domain.vo.resp.IdRespVO;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.vo.req.UserEmojiReq;
import com.bqy.common.user.domain.vo.resp.UserEmojiResp;
import com.bqy.common.user.service.IUserEmojiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表情包 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2025-01-15
 */
@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包管理相关接口")
public class UserEmojiController {

    @Resource
    private IUserEmojiService userEmojiService;

    @GetMapping("/list")
    @ApiOperation("表情包列表")
    public ApiResult<List<UserEmojiResp>> getEmojiList(){
        return ApiResult.success(userEmojiService.list(RequestHolder.get().getUid()));
    }
    @PostMapping("/add")
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> addEmoji(@Valid @RequestBody UserEmojiReq req){
        return ApiResult.success(userEmojiService.addEmoji(RequestHolder.get().getUid(),req));
    }
    @DeleteMapping("/delete")
    @ApiOperation("删除表情包")
    public ApiResult<Void> deleteEmoji(@Valid @RequestBody IdReqVO reqVO){
        userEmojiService.deleteEmoji(RequestHolder.get().getUid(),reqVO);
        return ApiResult.success();
    }
}

