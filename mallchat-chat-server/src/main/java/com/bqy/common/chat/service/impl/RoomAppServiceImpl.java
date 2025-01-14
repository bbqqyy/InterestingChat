package com.bqy.common.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.ContactDao;
import com.bqy.common.chat.dao.GroupMemberDao;
import com.bqy.common.chat.dao.MessageDao;
import com.bqy.common.chat.domain.dto.RoomBaseInfo;
import com.bqy.common.chat.domain.entity.*;
import com.bqy.common.chat.domain.enums.GroupErrorEnum;
import com.bqy.common.chat.domain.enums.GroupRoleAppEnum;
import com.bqy.common.chat.domain.enums.GroupRoleEnum;
import com.bqy.common.chat.domain.enums.RoomTypeEnum;
import com.bqy.common.chat.domain.vo.req.*;
import com.bqy.common.chat.domain.vo.resp.ChatMemberListResp;
import com.bqy.common.chat.domain.vo.resp.ChatRoomResp;
import com.bqy.common.chat.domain.vo.resp.MemberResp;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.chat.service.RoomAppService;
import com.bqy.common.chat.service.RoomService;
import com.bqy.common.chat.service.adapter.MemberAdapter;
import com.bqy.common.chat.service.adapter.RoomAdapter;
import com.bqy.common.chat.service.cache.*;
import com.bqy.common.chat.service.strategy.msg.AbstractMessageHandler;
import com.bqy.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.bqy.common.chat.service.strategy.msg.TextMsgHandler;
import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.event.GroupMemberAddEvent;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.HotFlagEnum;
import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.PushService;
import com.bqy.common.user.service.adapter.ChatAdapter;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.user.service.cache.UserInfoCache;
import com.bqy.common.websocket.domain.vo.resp.ChatMemberResp;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.domain.vo.resp.WSMemberChange;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoomAppServiceImpl implements RoomAppService {
    @Resource
    private RoomGroupCache roomGroupCache;
    @Resource
    private RoomFriendCache roomFriendCache;
    @Resource
    private RoomCache roomCache;
    @Resource
    private UserCache userCache;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Resource
    private UserDao userDao;
    @Resource
    private RoomService roomService;
    @Resource
    private ContactDao contactDao;
    @Resource
    private HotRoomCache hotRoomCache;
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private MessageDao messageDao;
    @Resource
    private ChatService chatService;
    @Resource
    private IRoleService roleService;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Resource
    private PushService pushService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public MemberResp getGroupDetail(Long uid, long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(roomGroup,"roomId有误");
        Long onlineNum;
        if(isHotRoom(room)){
            onlineNum = userCache.getOnlineNum();
        }else {
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
            onlineNum = userDao.getOnlineCount(memberUidList).longValue();
        }
        GroupRoleAppEnum groupRole = getGroupRole(uid,roomGroup,room);
        return MemberResp.builder()
                .groupName(roomGroup.getName())
                .avatar(roomGroup.getAvatar())
                .role(groupRole.getType())
                .onlineNum(onlineNum)
                .roomId(roomId)
                .build();
    }

    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(Long uid, CursorPageBaseReq req) {
        CursorPageBaseResp<Long> page;
        if(ObjectUtil.isNotNull(uid)){
            Double hotEnd = getCursorOrNull(req.getCursor());
            Double hotStart = null;
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid,req);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if(!contactPage.getIsLast()){
                hotStart = getCursorOrNull(contactPage.getCursor());
            }
            Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart,hotEnd);
            List<Long> hotRoomIds = typedTuples.stream()
                    .map(ZSetOperations.TypedTuple::getValue)
                    .filter(Objects::nonNull)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
            page = CursorPageBaseResp.init(contactPage,baseRoomIds);
        }else {
            CursorPageBaseResp<Pair<Long,Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(req);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage,roomIds);
        }
        if(CollectionUtil.isEmpty(page.getList())){
            return CursorPageBaseResp.empty();
        }
        List<ChatRoomResp> result = buildContactResp(uid,page.getList());
        return CursorPageBaseResp.init(page,result);
    }

    @Override
    public ChatRoomResp getContactDetail(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room,"房间号有误");
        return buildContactResp(uid,Collections.singletonList(roomId)).get(0);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {
        RoomFriend roomFriend = roomService.getFriendRoom(uid,friendUid);
        AssertUtil.isNotEmpty(roomFriend,"他还不是你的好友");
        return buildContactResp(uid,Collections.singletonList(roomFriend.getRoomId())).get(0);
    }

    @Override
    public CursorPageBaseResp<ChatMemberResp> getGroupMemberPage(MemberReq memberReq) {
        Room room = roomCache.get(memberReq.getRoomId());
        AssertUtil.isNotEmpty(room,"房间号有误");
        List<Long> memberUidList;
        if(room.isHotRoom()){
            memberUidList = null;
        }else {
            RoomGroup roomGroup = roomGroupCache.get(memberReq.getRoomId());
            memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
        }
        return chatService.getMemberPage(memberUidList,memberReq);
    }

    @Override
    @Cacheable(cacheNames = "member",key = "'memberList.'+#request.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room,"房间号有误");
        if(room.isHotRoom()){
            List<User> userList  = userDao.getMemberList();
            return MemberAdapter.buildMemberList(userList);
        }else {
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            List<Long> memberIdList = groupMemberDao.getMemberUidList(roomGroup.getId());
            Map<Long,User> batch = userInfoCache.getBatch(memberIdList);
            return MemberAdapter.buildMemberList(batch);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMember(Long uid, MemberDelReq req) {
        Room room = roomCache.get(req.getRoomId());
        AssertUtil.isNotEmpty(room,"房间号有误");
        RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
        AssertUtil.isNotEmpty(roomGroup,"房间号有误");
        GroupMember groupMember = groupMemberDao.getMember(roomGroup.getId(),uid);
        AssertUtil.isNotEmpty(groupMember, GroupErrorEnum.USER_NOT_IN_GROUP);
        Long removeUid = req.getUid();
        AssertUtil.isFalse(groupMemberDao.isLord(roomGroup.getId(),removeUid),GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        if(groupMemberDao.isManager(roomGroup.getId(),removeUid)){
            Boolean isLord = groupMemberDao.isLord(roomGroup.getId(),removeUid);
            AssertUtil.isTrue(isLord,GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
        AssertUtil.isTrue(hasPower(groupMember),GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember removeMember = groupMemberDao.getMember(roomGroup.getId(),removeUid);
        AssertUtil.isNotEmpty(removeMember,"用户已被移除");
        groupMemberDao.removeById(removeMember);
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        WSBaseResp<WSMemberChange> wsBaseResp = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(),removeMember.getUid());
        pushService.sendPushMsg(wsBaseResp,memberUidList);
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

    @Override
    @Transactional
    public Long addGroup(Long uid, GroupAddReq req) {
        RoomGroup roomGroup = roomService.createGroupRoom(uid);
        List<GroupMember> groupMemberList = RoomAdapter.buildGroupMemberBatch(req.getUidList(),roomGroup.getId());
        groupMemberDao.saveBatch(groupMemberList);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this,roomGroup,groupMemberList,uid));
        return roomGroup.getId();
    }

    @Override
    @RedissonLock(key = "#req.roomId")
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long uid, MemberAddReq req) {
        Room room = roomCache.get(req.getRoomId());
        AssertUtil.isNotEmpty(room,"房间号有误");
        AssertUtil.isTrue(room.isHotRoom(),"全员房间不需要邀请好友");
        RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "房间号有误");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(),uid);
        AssertUtil.isNotEmpty(self,"您不在这个房间");
        List<Long> memberUidList = groupMemberDao.getMemberBatch(roomGroup.getId(),req.getUidList());
        Set<Long> existUidSet = new HashSet<>(memberUidList);
        List<Long> waitAddUidList = req.getUidList().stream().filter(a->!existUidSet.contains(a)).distinct().collect(Collectors.toList());
        if(CollectionUtil.isEmpty(waitAddUidList)){
            return;
        }
        List<GroupMember> groupMemberList = MemberAdapter.buildMemberAdd(roomGroup.getId(),waitAddUidList);
        groupMemberDao.saveBatch(groupMemberList);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this,roomGroup,groupMemberList,uid));

    }

    private boolean hasPower(GroupMember groupMember) {
        return Objects.equals(groupMember.getRole(), GroupRoleEnum.LEADER.getType())||
                Objects.equals(groupMember.getRole(),GroupRoleEnum.MEMBER.getType())||
                roleService.hasPower(groupMember.getUid(), RoleEnum.ADMIN);

    }

    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(uid,roomIds);
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = CollectionUtil.isEmpty(msgIds)?new ArrayList<>():messageDao.listByIds(msgIds);
        Map<Long,Message> messageMap = messages.stream().collect(Collectors.toMap(Message::getId,Function.identity()));
        Map<Long,User> userMap = userInfoCache.getBatch(messageMap.values().stream().map(Message::getFromUid).collect(Collectors.toList()));
        Map<Long,Integer> unReadCountMap = getUnReadCountMap(uid,roomIds);
        return roomBaseInfoMap.values()
                .stream()
                .map(roomBaseInfo -> {
                    ChatRoomResp chatRoomResp = new ChatRoomResp();
                    chatRoomResp.setRoomId(roomBaseInfo.getRoomId());
                    chatRoomResp.setType(roomBaseInfo.getType());
                    chatRoomResp.setHot_Flag(roomBaseInfo.getHotFlag());
                    chatRoomResp.setName(roomBaseInfo.getName());
                    chatRoomResp.setAvatar(roomBaseInfo.getAvatar());
                    chatRoomResp.setActiveTime(roomBaseInfo.getActiveTime());
                    chatRoomResp.setUnreadCount(unReadCountMap.getOrDefault(roomBaseInfo.getRoomId(),0));
                    Message message = messageMap.get(roomBaseInfo.getRoomId());
                    if(ObjectUtil.isNotNull(message)){
                        AbstractMessageHandler abstractMessageHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        chatRoomResp.setText(userMap.get(message.getFromUid()).getName()+":"+abstractMessageHandler.showContactMsg(message));
                    }
                    return chatRoomResp;

                }).collect(Collectors.toList());
    }

    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if(ObjectUtil.isNull(uid)){
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIds(roomIds,uid);
        return contacts.parallelStream()
                .map(contact->Pair.of(contact.getRoomId(),messageDao.getUnReadMessageCount(contact.getRoomId(),contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey,Pair::getValue));


    }

    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(Long uid, List<Long> roomIds) {
        Map<Long,Room> roomMap = roomCache.getBatch(roomIds);
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType, Collectors.mapping(Room::getId, Collectors.toList())));
        List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long,RoomGroup> roomGroupMap = roomGroupCache.getBatch(groupRoomId);
        List<Long> friendRoomIds = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> roomFriendMap = getFriendRoomMap(uid,friendRoomIds);
        return roomMap.values().stream()
                .map(room -> {
                    RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
                    roomBaseInfo.setRoomId(room.getId());
                    roomBaseInfo.setType(room.getType());
                    roomBaseInfo.setHotFlag(room.getHotFlag());
                    roomBaseInfo.setActiveTime(room.getActiveTime());
                    roomBaseInfo.setLastMsgId(room.getLastMsgId());
                    if(room.getType().equals(RoomTypeEnum.GROUP.getType())){
                        RoomGroup roomGroup = roomGroupMap.get(room.getId());
                        roomBaseInfo.setName(roomGroup.getName());
                        roomBaseInfo.setAvatar(roomGroup.getAvatar());
                    }else if(room.getType().equals(RoomTypeEnum.FRIEND.getType())){
                        User user = roomFriendMap.get(room.getId());
                        roomBaseInfo.setName(user.getName());
                        roomBaseInfo.setAvatar(user.getAvatar());
                    }
                    return roomBaseInfo;
                }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    private Map<Long, User> getFriendRoomMap(Long uid,List<Long> friendRoomIds) {
        if(CollectionUtil.isEmpty(friendRoomIds)){
            return new HashMap<>();
        }
        Map<Long,RoomFriend> roomFriendMap = roomFriendCache.getBatch(friendRoomIds);
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(),uid);
        Map<Long, User> userMap = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId,friendroom->{
                    Long friendUid = ChatAdapter.getFriendUid(friendroom,uid);
                    return userMap.get(friendUid);
                }));
    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }

    private GroupRoleAppEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        GroupMember groupMember = ObjectUtil.isNull(uid)?null:groupMemberDao.getMember(roomGroup.getId(),uid);
        if(ObjectUtil.isNotNull(groupMember)){
            return GroupRoleAppEnum.of(groupMember.getRole());
        }else if(isHotRoom(room)){
            return GroupRoleAppEnum.MEMBER;
        }else {
            return GroupRoleAppEnum.REMOVE;
        }
    }

    private boolean isHotRoom(Room room) {
        return HotFlagEnum.YES.getType().equals(room.getHotFlag());
    }
}
