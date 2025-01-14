package com.bqy.common.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.MessageMark;
import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;
import com.bqy.common.chat.domain.enums.MessageStatusEnum;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.chat.domain.vo.req.ChatMessageReq;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.strategy.msg.AbstractMessageHandler;
import com.bqy.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.bqy.common.common.domain.enums.YesOrNoEnum;

import java.util.*;
import java.util.stream.Collectors;

public class MessageAdapter {
    public static final int CAN_CALLBACK_GAP_COUNT = 100;
    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, List<MessageMark> messageMarks, Long receiveUid) {
        Map<Long,List<MessageMark>> markMsg = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream()
                .map(message->{
                    ChatMessageResp chatMessageResp = new ChatMessageResp();
                    chatMessageResp.setFromUser(buildFromUser(message.getFromUid()));
                    chatMessageResp.setMessage(buildMessage(message,markMsg.getOrDefault(message.getId(),new ArrayList<>()),receiveUid));
                    return chatMessageResp;
                })
                .sorted(Comparator.comparing(a->a.getMessage().getSendTime()))
                .collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildMessage(Message message, List<MessageMark> messageMarks, Long receiveUid) {
        ChatMessageResp.Message messageVo = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message,messageVo);
        messageVo.setSendTime(message.getCreateTime());
        AbstractMessageHandler<?> messageHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if(ObjectUtil.isNotNull(messageHandler)){
            messageVo.setBody(messageHandler.showMsg(message));
        }
        messageVo.setMessageMark(buildMsgMark(messageMarks,receiveUid));
        return messageVo;
    }

    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> messageMarks, Long receiveUid) {
        Map<Integer,List<MessageMark>> messageMarkMap = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeMsgList = messageMarkMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(),new ArrayList<>());
        List<MessageMark> unLikeMsgList = messageMarkMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(),new ArrayList<>());
        ChatMessageResp.MessageMark messageMark = new ChatMessageResp.MessageMark();
        messageMark.setLikeCount(likeMsgList.size());
        messageMark.setDislikeCount(unLikeMsgList.size());
        messageMark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid->unLikeMsgList.stream().anyMatch(unLikeUid->ObjectUtil.equals(unLikeUid,uid))).map(a-> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        messageMark.setUserLike(Optional.ofNullable(receiveUid).filter(uid->likeMsgList.stream().anyMatch(likeUid->ObjectUtil.equals(likeUid,uid))).map(a->YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        return messageMark;
    }

    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }

    public static Message buildMsgSave(ChatMessageReq req, Long uid) {
        return Message.builder()
                .roomId(req.getRoomId())
                .fromUid(uid)
                .type(req.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();
    }
}
