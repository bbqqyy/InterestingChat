package com.bqy.common.chat.controller;

import com.bqy.common.chat.dao.GroupMemberDao;
import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMemberListResp;
import com.bqy.common.chat.domain.vo.resp.MemberResp;
import com.bqy.common.chat.service.GroupMemberService;
import com.bqy.common.chat.service.IRoomService;
import com.bqy.common.chat.service.RoomAppService;
import com.bqy.common.common.domain.vo.req.IdReqVO;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.domain.vo.resp.IdRespVO;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/room")
@Api(tags = "聊天室相关接口")
@Slf4j
public class RoomController {
    @Resource
    private RoomAppService roomService;
    @Resource
    private GroupMemberService groupMemberService;
    @GetMapping("/public/group")
    @ApiOperation("群组详情")
    public ApiResult<MemberResp> groupDetail(@Valid IdReqVO request){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getGroupDetail(uid,request.getId()));
    }
    @GetMapping("/public/group/member/page")
    @ApiOperation("群成员列表")
    public ApiResult<CursorPageBaseResp<ChatMemberResp>> getGroupMemberPage(@Valid MemberReq memberReq){
        return ApiResult.success(roomService.getGroupMemberPage(memberReq));
    }
    @GetMapping("/group/member/list")
    @ApiOperation("房间内所有群成员列表-@专用")
    public ApiResult<List<ChatMemberListResp>> getMemberList(@Valid ChatMessageMemberReq req){
        return ApiResult.success(roomService.getMemberList(req));
    }
    @DeleteMapping("/group/member/del")
    @ApiOperation(" 移除群成员")
    public ApiResult<Void> delMember(@RequestBody @Valid MemberDelReq req){
        Long uid = RequestHolder.get().getUid();
        roomService.delMember(uid,req);
        return ApiResult.success();
    }
    @DeleteMapping("/group/member/exit")
    @ApiOperation("退出群聊")
    public ApiResult<Boolean> exitGroup(@RequestBody @Valid MemberExitReq req){
        Long uid = RequestHolder.get().getUid();
        groupMemberService.exitGroup(uid,req);
        return ApiResult.success();
    }
    @PostMapping("/group/add")
    @ApiOperation("添加群聊")
    public ApiResult<IdRespVO> addGroup(@RequestBody @Valid GroupAddReq req){
        Long uid = RequestHolder.get().getUid();
        Long roomId = roomService.addGroup(uid,req);
        return ApiResult.success(IdRespVO.id(roomId));
    }
    @PostMapping("/group/member/add")
    @ApiOperation("邀请进入群聊")
    public ApiResult<Void> addMember(@RequestBody @Valid MemberAddReq req){
        Long uid = RequestHolder.get().getUid();
        roomService.addMember(uid,req);
        return ApiResult.success();
    }
    @PutMapping("/group/admin/add")
    @ApiOperation("添加管理员")
    public ApiResult<Boolean> addAdmin(@RequestBody @Valid AdminAddReq req){
        Long uid = RequestHolder.get().getUid();
        groupMemberService.addAdmin(uid,req);
        return ApiResult.success();
    }
    @DeleteMapping("/group/admin")
    @ApiOperation("撤销管理员")
    public ApiResult<Boolean> revokeAdmin(@Valid @RequestBody AdminRevokeReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.revokeAdmin(uid, request);
        return ApiResult.success();
    }

}
