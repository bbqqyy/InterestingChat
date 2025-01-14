package com.bqy.common.chat.service.strategy.mark;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.MessageMarkDao;
import com.bqy.common.chat.domain.dto.ChatMsgMarkDTO;
import com.bqy.common.chat.domain.entity.MessageMark;
import com.bqy.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.common.event.MsgMarkEvent;
import com.bqy.common.common.exception.BusinessException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

public abstract class AbstractMessageMarkStrategy {
    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    protected abstract MessageMarkTypeEnum getMarkTypeEnum();

    @Transactional
    public void mark(Long uid,Long msgId){
        doMark(uid,msgId);
    }

    @Transactional
    public void unmark(Long uid,Long msgId){
        doUnMark(uid,msgId);
    }

    protected void doUnMark(Long uid,Long msgId){
        execute(uid,msgId,MessageMarkActTypeEnum.UN_MARK);
    }
    protected void doMark(Long uid, Long msgId) {
        execute(uid,msgId, MessageMarkActTypeEnum.MARK);
    }

    protected void execute(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        Integer markType = getMarkTypeEnum().getType();
        Integer actType = actTypeEnum.getType();
        MessageMark oldMark = messageMarkDao.get(uid,msgId,markType);
        if(ObjectUtil.isNull(oldMark)&&actTypeEnum==MessageMarkActTypeEnum.UN_MARK){
            return;
        }
        MessageMark updateMark = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .uid(uid)
                .msgId(msgId)
                .type(markType)
                .status(transformAct(actType))
                .build();
        boolean result = messageMarkDao.saveOrUpdate(updateMark);
        if(result){
            ChatMsgMarkDTO chatMsgMarkDTO = new ChatMsgMarkDTO(uid,msgId,markType,actType);
            applicationEventPublisher.publishEvent(new MsgMarkEvent(this,chatMsgMarkDTO));
        }

    }

    private Integer transformAct(Integer actType) {
        if(actType==1){
            return YesOrNoEnum.NO.getStatus();
        }else if(actType==2){
            return YesOrNoEnum.YES.getStatus();
        }
        throw new BusinessException("动作类型 1确认 2取消");

    }


}
