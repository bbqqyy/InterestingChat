package com.bqy.common.common.event.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.service.cache.RoomCache;
import com.bqy.common.common.constant.MQConstant;
import com.bqy.common.common.domain.dto.MsgSendDTO;
import com.bqy.common.common.event.MsgSendEvent;
import com.bqy.common.user.domain.enums.HotFlagEnum;
import com.bqy.mallchat.transaction.service.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
@Slf4j
public class MsgSendListener {
    @Resource
    private MQProducer mqProducer;
    @Resource
    private MessageDao messageDao;
    @Resource
    private RoomCache roomCache;
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT,classes = MsgSendEvent.class,fallbackExecution = true)
    public void messageRoute(MsgSendEvent msgSendEvent){
        Long msgId = msgSendEvent.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC,new MsgSendDTO(msgId),msgId);
    }
    @TransactionalEventListener(classes = MsgSendEvent.class,fallbackExecution = true)
    public void handlerMessage(@NotNull MsgSendEvent msgSendEvent){
        Long msgId = msgSendEvent.getMsgId();
        Message message = messageDao.getById(msgId);
        Room room = roomCache.get(message.getRoomId());
        if(isHotRoom(room)){
            //todo ai服务
        }
    }
    public boolean isHotRoom(Room room) {
        return Objects.equals(HotFlagEnum.YES.getType(), room.getHotFlag());
    }
    @TransactionalEventListener(classes = MsgSendEvent.class,fallbackExecution = true)
    public void publishChatToWeChat(@NotNull MsgSendEvent msgSendEvent){
        Long msgId = msgSendEvent.getMsgId();
        Message message = messageDao.getById(msgId);
        if(CollectionUtil.isNotEmpty(message.getExtra().getAtUidList())){
            //todo 微信集群推送
        }
    }
}
