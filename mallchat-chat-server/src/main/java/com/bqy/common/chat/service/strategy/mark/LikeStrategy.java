package com.bqy.common.chat.service.strategy.mark;

import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;

public class LikeStrategy extends AbstractMessageMarkStrategy{

    @Override
    protected MessageMarkTypeEnum getMarkTypeEnum() {
        return MessageMarkTypeEnum.LIKE;
    }

    @Override
    protected void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType()).unmark(uid,msgId);
    }
}
