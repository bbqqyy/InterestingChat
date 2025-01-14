package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.entity.msg.VideoMsgDTO;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VideoMsgHandler extends AbstractMessageHandler<VideoMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void saveMessage(Message msg, VideoMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "视频";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[视频]";
    }
}