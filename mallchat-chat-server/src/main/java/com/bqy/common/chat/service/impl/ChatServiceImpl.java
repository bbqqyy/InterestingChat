package com.bqy.common.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.*;
import com.bqy.common.chat.domain.dto.MsgReadInfoDTO;
import com.bqy.common.chat.domain.entity.*;
import com.bqy.common.chat.domain.enums.ChatActiveStatusEnum;
import com.bqy.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.bqy.common.chat.domain.enums.MessageMarkTypeEnum;
import com.bqy.common.chat.domain.enums.MessageTypeEnum;
import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMemberStatisticResp;
import com.bqy.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.bqy.common.chat.domain.vo.resp.ChatMessageResp;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.chat.service.ContactService;
import com.bqy.common.chat.service.adapter.MemberAdapter;
import com.bqy.common.chat.service.adapter.MessageAdapter;
import com.bqy.common.chat.service.adapter.RoomAdapter;
import com.bqy.common.chat.service.cache.RoomCache;
import com.bqy.common.chat.service.cache.RoomGroupCache;
import com.bqy.common.chat.service.helper.ChatMemberHelper;
import com.bqy.common.chat.service.strategy.mark.AbstractMessageMarkStrategy;
import com.bqy.common.chat.service.strategy.mark.MsgMarkFactory;
import com.bqy.common.chat.service.strategy.msg.AbstractMessageHandler;
import com.bqy.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.bqy.common.chat.service.strategy.msg.RecallMsgHandler;
import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.event.MsgSendEvent;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private UserDao userDao;
    @Resource
    private RoomCache roomCache;
    @Resource
    private ContactDao contactDao;
    @Resource
    private MessageDao messageDao;
    @Resource
    private MessageMarkDao messageMarkDao;
    @Resource
    private RoomFriendDao roomFriendDao;
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Resource
    private IRoleService iRoleService;
    @Resource
    private RecallMsgHandler recallMsgHandler;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private ContactService contactService;
    @Resource
    private RoomGroupDao roomGroupDao;
    @Resource
    private UserCache userCache;
    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(Long receiveUid, ChatMessagePageReq req) {
        Long lastMsgId = getLastMsgId(req.getRoomId(),receiveUid);
        CursorPageBaseResp<Message> cursorPageBaseResp = messageDao.getCursorPage(req.getRoomId(),req,lastMsgId);
        if(ObjectUtil.isNull(cursorPageBaseResp)){
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPageBaseResp,getMsgRespBatch(cursorPageBaseResp.getList(),receiveUid));
    }

    @Override
    @Transactional
    public Long sendMessage(ChatMessageReq req, Long uid) {
        checkMessage(req,uid);
        AbstractMessageHandler<?> messageHandler = MsgHandlerFactory.getStrategyNoNull(req.getMsgType());
        Long msgId = messageHandler.checkAndSaveMsg(req,uid);
        applicationEventPublisher.publishEvent(new MsgSendEvent(this,msgId));
        return msgId;
    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message message = messageDao.getById(msgId);
        return getMsgResp(message,receiveUid);
    }
    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message),receiveUid));
    }

    @Override
    public void recallMessage(Long uid, ChatMessageBaseReq req) {
        Message message = messageDao.getById(req.getMsgId());
        checkRecallMessage(uid,message);
        recallMsgHandler.recall(uid,message);

    }

    @Override
    @RedissonLock(key = "#uid")
    public void markMessage(Long uid, ChatMsgMarkReq req) {
        AbstractMessageMarkStrategy strategy = MsgMarkFactory.getStrategyNoNull(req.getMarkType());
        switch (MessageMarkActTypeEnum.of(req.getActType())){
            case MARK:
                strategy.mark(uid,req.getMsgId());
                break;
            case UN_MARK:
                strategy.unmark(uid,req.getMsgId());
                break;
        }
    }

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadMessagePage(@Nullable Long uid, ChatMessageReadReq req) {
        Message message = messageDao.getById(req.getMsgId());
        AssertUtil.isNotEmpty(message,"消息id有误");
        AssertUtil.equal(message.getFromUid(),uid,"只能看到自己的消息");
        CursorPageBaseResp<Contact> page;
        if(req.getSearchType()==1){
            page = contactDao.getReadPage(message,req);
        }else {
            page = contactDao.getUnReadPage(message,req);
        }
        if(CollectionUtil.isEmpty(page.getList())){
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(page, RoomAdapter.buildReadResp(page.getList()));
    }

    @Override
    public Collection<MsgReadInfoDTO> getMsgReadCount(Long uid, ChatMessageReadInfoReq req) {
        List<Message> messages = messageDao.listByIds(req.getMsgIds());
        messages.forEach(message->{
            AssertUtil.equal(message.getFromUid(),uid,"只能查看自己的消息");
        });
        return contactService.getMsgReadCount(messages).values();
    }

    @Override
    @RedissonLock(key = "#uid")
    public void msgRead(Long uid, ChatMessageMemberReq req) {
        Contact contact = contactDao.get(uid,req.getRoomId());
        if(ObjectUtil.isNotNull(contact)){
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        }else {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(req.getRoomId());
            insert.setReadTime(new Date());
            contactDao.save(insert);
        }
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq memberReq) {
        Pair<ChatActiveStatusEnum,String> pair = ChatMemberHelper.getCursorPair(memberReq.getCursor());
        ChatActiveStatusEnum chatActiveStatusEnum = pair.getKey();
        String timeCursor = pair.getValue();
        List<ChatMemberResp> resultList = new ArrayList<>();
        Boolean isLast = Boolean.FALSE;
        if(chatActiveStatusEnum == ChatActiveStatusEnum.ONLINE){
            CursorPageBaseResp<User> cursorPageBaseResp = userDao.getCursorPage(memberUidList,new CursorPageBaseReq(memberReq.getPageSize(),timeCursor),ChatActiveStatusEnum.ONLINE);
            resultList.addAll(MemberAdapter.buildMember(cursorPageBaseResp.getList()));
            if(cursorPageBaseResp.getIsLast()){
                chatActiveStatusEnum = ChatActiveStatusEnum.OFFLINE;
                Integer leftSize = memberReq.getPageSize() - cursorPageBaseResp.getList().size();
                cursorPageBaseResp = userDao.getCursorPage(memberUidList,new CursorPageBaseReq(leftSize,null),ChatActiveStatusEnum.OFFLINE);
                resultList.addAll(MemberAdapter.buildMember((cursorPageBaseResp.getList())));
            }
            timeCursor = cursorPageBaseResp.getCursor();
            isLast = cursorPageBaseResp.getIsLast();
        }else if(chatActiveStatusEnum == ChatActiveStatusEnum.OFFLINE){
            CursorPageBaseResp<User> cursorPageBaseResp = userDao.getCursorPage(memberUidList,new CursorPageBaseReq(memberReq.getPageSize(),timeCursor),ChatActiveStatusEnum.OFFLINE);
            resultList.addAll(MemberAdapter.buildMember(cursorPageBaseResp.getList()));
            timeCursor = cursorPageBaseResp.getCursor();
            isLast = cursorPageBaseResp.getIsLast();
        }
        List<Long> uidList = resultList.stream().map(ChatMemberResp::getUid).collect(Collectors.toList());
        RoomGroup roomGroup = roomGroupDao.getByRoomId(memberReq.getRoomId());
        Map<Long,Integer> uidMapRole = groupMemberDao.getMemberMapRole(roomGroup.getId(),uidList);
        resultList.forEach(member->member.setRoleId(uidMapRole.get(member.getUid())));
        return new CursorPageBaseResp<>(ChatMemberHelper.generateCursor(chatActiveStatusEnum,timeCursor),isLast,resultList);
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        System.out.println(Thread.currentThread().getName());
        Long onlineNum = userCache.getOnlineNum();
        ChatMemberStatisticResp chatMemberStatisticResp = new ChatMemberStatisticResp();
        chatMemberStatisticResp.setOnlineNum(onlineNum);
        return chatMemberStatisticResp;
    }

    private void checkRecallMessage(Long uid, Message message) {
        AssertUtil.isNotEmpty(message,"消息有误");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(),"撤回的消息不能继续撤回了");
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if(hasPower){
            return;
        }
        boolean isSelf = ObjectUtil.equal(uid,message.getFromUid());
        AssertUtil.isTrue(isSelf,"抱歉，你没有权限");
        long between = DateUtil.between(message.getCreateTime(),new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between<2,"时间太久了，不能撤回了");
    }

    private void checkMessage(ChatMessageReq req, Long uid) {
        Room room  = roomCache.get(req.getRoomId());
        if(room.isHotRoom()){
            return;
        }
        if(room.isRoomFriend()){
            RoomFriend roomFriend = roomFriendDao.getByRoomId(req.getRoomId());
            AssertUtil.equal(roomFriend.getStatus(), NormalOrNoEnum.NORMAL.getStatus(),"你已经被对方拉黑");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1())||uid.equals(roomFriend.getUid2()),"你已经被拉黑");
        }
        if(room.isRoomGroup()){
            RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
            GroupMember groupMember = groupMemberDao.getMember(roomGroup.getId(),uid);
            AssertUtil.isNotEmpty(groupMember,"你已经被移出群聊");
        }
    }

    private List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if(CollectionUtil.isEmpty(messages)){
            return new ArrayList<>();
        }
        List<MessageMark> messageMarks = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages,messageMarks,receiveUid);
    }

    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room,"房间id不对");
        if(room.isHotRoom()){
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid,"请先登录");
        Contact contact = contactDao.get(roomId,receiveUid);
        return contact.getLastMsgId();
    }

}
