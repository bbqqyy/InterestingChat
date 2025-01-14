package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.ImgMsgDTO;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class ImgMsgHandler extends AbstractMessageHandler<ImgMsgDTO> {
    @Resource
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    protected void saveMessage(Message message, ImgMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        extra.setImgMsgDTO(body);
        update.setExtra(extra);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "图片";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[图片]";
    }
}
