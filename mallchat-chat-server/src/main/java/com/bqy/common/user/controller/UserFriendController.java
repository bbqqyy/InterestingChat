package com.bqy.common.user.controller;


import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.req.PageBaseReq;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.domain.vo.resp.PageBaseResp;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.vo.req.FriendApplyReq;
import com.bqy.common.user.domain.vo.req.FriendApproveReq;
import com.bqy.common.user.domain.vo.req.FriendCheckReq;
import com.bqy.common.user.domain.vo.req.FriendDeleteReq;
import com.bqy.common.user.domain.vo.resp.FriendApplyResp;
import com.bqy.common.user.domain.vo.resp.FriendCheckResp;
import com.bqy.common.user.domain.vo.resp.FriendResp;
import com.bqy.common.user.domain.vo.resp.FriendUnReadResp;
import com.bqy.common.user.service.IUserFriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 用户联系人表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
@RestController
@RequestMapping("/capi/user/friend")
@Slf4j
public class UserFriendController {
    @Resource
    private IUserFriendService userFriendService;

    @GetMapping("/check")
    public ApiResult<FriendCheckResp> checkFriend(@Valid FriendCheckReq friendCHeckReq) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.checkFriend(uid, friendCHeckReq));
    }

    @PostMapping("/apply")
    public ApiResult<Void> applyFriend(@Valid @RequestBody FriendApplyReq request) {
        userFriendService.applyFriend(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @GetMapping("/apply/page")
    public ApiResult<PageBaseResp<FriendApplyResp>> applyFriendPage(@Valid PageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.applyFriendPage(uid, request));
    }

    @GetMapping("/apply/unread")
    public ApiResult<FriendUnReadResp> unRead() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.unRead(uid));
    }

    @GetMapping("/list")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(userFriendService.friendList(uid, request));
    }

    @PutMapping("/approve")
    public ApiResult<Void> approveApply(@Valid @RequestBody FriendApproveReq req){
        userFriendService.applyApprove(RequestHolder.get().getUid(),req);
        return ApiResult.success();
    }
    @PostMapping("delete")
    public ApiResult<Void> deleteFriend(@Valid @RequestBody FriendDeleteReq req){
        userFriendService.deleteFriend(RequestHolder.get().getUid(),req.getTargetId());
        return ApiResult.success();
    }


}

