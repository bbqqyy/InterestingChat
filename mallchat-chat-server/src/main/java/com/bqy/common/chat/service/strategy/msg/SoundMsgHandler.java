package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.entity.msg.SoundMsgDTO;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SoundMsgHandler extends AbstractMessageHandler<SoundMsgDTO> {
    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.SOUND;
    }

    @Override
    public void saveMessage(Message msg, SoundMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setSoundMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getSoundMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "语音";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[语音]";
    }
}
