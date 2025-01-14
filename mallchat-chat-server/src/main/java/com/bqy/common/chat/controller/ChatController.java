package com.bqy.common.chat.controller;

import com.bqy.common.chat.domain.dto.MsgReadInfoDTO;
import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.common.domain.vo.resp.ApiResult;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.RequestHolder;
import com.bqy.common.user.domain.enums.BlackTypeEnum;
import com.bqy.common.user.service.cache.UserCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/capi/chat")
@Api(tags = "聊天信息")
@Slf4j
public class ChatController {
    @Resource
    private ChatService chatService;

    @Resource
    private UserCache userCache;

    @GetMapping("/public/message/page")
    @ApiOperation("消息列表")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq req) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(RequestHolder.get().getUid(), req);
        filterBlackMsg(msgPage);
        return ApiResult.success(msgPage);
    }

    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> msgPage) {
        Set<String> blackMemberSet = getBlackUidSet();
        msgPage.getList().removeIf(a->blackMemberSet.contains(a.getFromUser().getUid().toString()));
    }

    private Set<String> getBlackUidSet() {
        return userCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(),new HashSet<>());
    }
    @PostMapping("/message/send")
    @ApiOperation("发送消息")
    public ApiResult<ChatMessageResp> sendMessage(@Valid @RequestBody ChatMessageReq req){
        Long msgId = chatService.sendMessage(req,RequestHolder.get().getUid());
        return ApiResult.success(chatService.getMsgResp(msgId,RequestHolder.get().getUid()));
    }
    @PutMapping("/message/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMessage(@Valid @RequestBody ChatMessageBaseReq req){
        chatService.recallMessage(RequestHolder.get().getUid(),req);
        return ApiResult.success();
    }
    @PutMapping("/message/mark")
    @ApiOperation("标记消息")
    public ApiResult<Void> markMessage(@Valid @RequestBody ChatMsgMarkReq req){
        chatService.markMessage(RequestHolder.get().getUid(),req);
        return ApiResult.success();
    }

    @GetMapping("/message/read/page")
    @ApiOperation("消息已读未读列表")
    public ApiResult<CursorPageBaseResp<ChatMessageReadResp>> messageReadPage(@Valid ChatMessageReadReq req){
        return ApiResult.success(chatService.getReadMessagePage(RequestHolder.get().getUid(),req));
    }

    @GetMapping("/message/read/count")
    @ApiOperation("消息已读未读总数")
    public ApiResult<Collection<MsgReadInfoDTO>> messageReadCount(@Valid ChatMessageReadInfoReq req){
        return ApiResult.success(chatService.getMsgReadCount(RequestHolder.get().getUid(),req));
    }
    @PutMapping("/message/read")
    @ApiOperation("消息阅读")
    public ApiResult<Void> messageRead(@Valid @RequestBody ChatMessageMemberReq req){
        Long uid = RequestHolder.get().getUid();
        chatService.msgRead(uid,req);
        return ApiResult.success();
    }


}
