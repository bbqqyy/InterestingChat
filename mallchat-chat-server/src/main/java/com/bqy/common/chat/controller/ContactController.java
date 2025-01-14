package com.bqy.common.chat.controller;

import com.bqy.common.chat.domain.vo.req.ContactFriendReq;
import com.bqy.common.chat.domain.vo.resp.ChatRoomResp;
import com.bqy.common.chat.service.RoomAppService;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.req.IdReqVO;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/capi/chat")
@Slf4j
public class ContactController {
    @Resource
    private RoomAppService roomAppService;
    @GetMapping("/public/contact/page")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq req){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactPage(uid,req));
    }
    @GetMapping("/public/contact/detail")
    public ApiResult<ChatRoomResp> getContactDetail(@Valid IdReqVO reqVO){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactDetail(uid,reqVO.getId()));
    }
    @GetMapping("public/contact/detail/friend")
    public ApiResult<ChatRoomResp> getContactDetailFriend(@Valid ContactFriendReq req){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomAppService.getContactDetailByFriend(uid,req.getUid()));
    }

}
