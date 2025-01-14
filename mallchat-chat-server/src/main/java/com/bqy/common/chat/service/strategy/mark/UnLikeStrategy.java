package com.bqy.common.chat.service.strategy.mark;

import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;
import org.checkerframework.checker.units.qual.A;

public class UnLikeStrategy extends AbstractMessageMarkStrategy{

    @Override
    protected MessageMarkTypeEnum getMarkTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    protected void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unmark(uid,msgId);
    }
}
