package com.bqy.common.common.event.listener;

import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.chat.domain.vo.req.ChatMessageReq;
import com.bqy.common.chat.service.ChatService;
import com.bqy.common.chat.service.adapter.MemberAdapter;
import com.bqy.common.chat.service.adapter.RoomAdapter;
import com.bqy.common.chat.service.cache.GroupMemberCache;
import com.bqy.common.common.event.GroupMemberAddEvent;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.PushService;
import com.bqy.common.user.service.adapter.ChatAdapter;
import com.bqy.common.user.service.cache.UserInfoCache;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.domain.vo.resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GroupMemberAddListener {
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private ChatService chatService;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Resource
    private UserDao userDao;
    @Resource
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        List<GroupMember> groupMemberList = event.getGroupMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = userInfoCache.get(inviteUid);
        List<Long> uidList = groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMessage(chatMessageReq, User.UID_SYSTEM);
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> groupMemberList = event.getGroupMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = groupMemberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> userList = userDao.listByIds(uidList);
        userList.forEach(user->{
            WSBaseResp<WSMemberChange> wsBaseResp = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(),user);
            pushService.sendPushMsg(wsBaseResp,memberUidList);
        });
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }
}
