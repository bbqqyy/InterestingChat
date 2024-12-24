package com.bqy.common.user.controller;


import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.interceptor.TokenInterceptor;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.domain.vo.req.BlackReq;
import com.bqy.common.user.domain.vo.req.ModifyNameReq;
import com.bqy.common.user.domain.vo.req.WearBadgeReq;
import com.bqy.common.user.domain.vo.resp.BadgeResp;
import com.bqy.common.user.domain.vo.resp.UserInfoResp;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private IRoleService roleService;

    @GetMapping("/userInfo")
    @ApiOperation(value = "获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/modifyName")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(), req.getName());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    public ApiResult<List<BadgeResp>> badgeList() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/wearBadge")
    public ApiResult<Void> wearBadge(@Valid @RequestBody WearBadgeReq req) {
        userService.wearBadge(RequestHolder.get().getUid(), req.getItemId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    public ApiResult<Void> blackUser(@Valid @RequestBody BlackReq blackReq) {
        Long uid = RequestHolder.get().getUid();
        boolean hasPower = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(hasPower,"没有拉黑用户的权限");
        userService.blackUser(blackReq);
        return ApiResult.success();
    }


}

