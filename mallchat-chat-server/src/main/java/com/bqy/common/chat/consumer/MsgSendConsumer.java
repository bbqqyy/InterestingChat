package com.bqy.common.chat.consumer;

import com.bqy.common.chat.dao.ContactDao;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.dao.RoomDao;
import com.bqy.common.chat.dao.RoomFriendDao;
import com.bqy.common.chat.domain.entity.Message;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.chat.domain.enums.RoomTypeEnum;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.chat.service.cache.GroupMemberCache;
import com.bqy.common.chat.service.cache.HotRoomCache;
import com.bqy.common.chat.service.cache.RoomCache;
import com.bqy.common.common.constant.MQConstant;
import com.bqy.common.common.domain.dto.MsgSendDTO;
import com.bqy.common.user.service.PushService;
import com.bqy.common.websocket.service.adapter.WebSocketAdapter;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP,topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendDTO> {
    @Resource
    private MessageDao messageDao;
    @Resource
    private RoomCache roomCache;
    @Resource
    private ChatService chatService;
    @Resource
    private RoomDao roomDao;
    @Resource
    private HotRoomCache hotRoomCache;
    @Resource
    private PushService pushService;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Resource
    private RoomFriendDao roomFriendDao;
    @Resource
    private ContactDao contactDao;
    @Override
    public void onMessage(MsgSendDTO msgSendDTO) {
        Message message = messageDao.getById(msgSendDTO.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp resp = chatService.getMsgResp(message,null);
        roomDao.refreshActiveTime(room.getId(),message.getId(),message.getCreateTime());
        roomCache.delete(room.getId());
        if(room.isHotRoom()){
            hotRoomCache.refreshActiveTime(room.getId(),message.getCreateTime());
            pushService.sendPushMsg(WebSocketAdapter.buildMsgSend(resp));
        }else {
            List<Long> memberUidList = new ArrayList<>();
            if(Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())){
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
            }else if(Objects.equals(room.getType(),RoomTypeEnum.FRIEND.getType())){
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(),roomFriend.getUid2());
            }
            contactDao.refreshOrCreateActiveTime(room.getId(),memberUidList,message.getId(),message.getCreateTime());
            pushService.sendPushMsg(WebSocketAdapter.buildMsgSend(resp),memberUidList);
        }

    }
}
