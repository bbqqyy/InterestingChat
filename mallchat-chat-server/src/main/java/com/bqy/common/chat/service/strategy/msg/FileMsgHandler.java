package com.bqy.common.chat.service.strategy.msg;

import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.msg.FileMsgDTO;
import com.bqy.common.chat.domain.entity.msg.MessageExtra;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
@Component
public class FileMsgHandler extends AbstractMessageHandler<FileMsgDTO>{
    @Resource
    private MessageDao messageDao;
    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    @Override
    protected void saveMessage(Message message, FileMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        extra.setFileMsg(body);
        update.setExtra(message.getExtra());
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getFileMsg();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "文件:"+msg.getExtra().getFileMsg().getFileName();
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[文件]"+msg.getExtra().getFileMsg().getFileName();
    }
}
