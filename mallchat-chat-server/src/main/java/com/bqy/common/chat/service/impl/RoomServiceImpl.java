package com.bqy.common.chat.service.impl;

import com.bqy.common.chat.dao.GroupMemberDao;
import com.bqy.common.chat.dao.RoomDao;
import com.bqy.common.chat.dao.RoomFriendDao;
import com.bqy.common.chat.dao.RoomGroupDao;
import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.chat.domain.enums.GroupRoleEnum;
import com.bqy.common.chat.domain.enums.RoomTypeEnum;
import com.bqy.common.chat.service.RoomService;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.service.adapter.ChatAdapter;
import com.bqy.common.user.service.cache.UserCache;
import com.bqy.common.user.service.cache.UserInfoCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Resource
    private RoomFriendDao roomFriendDao;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private RoomDao roomDao;
    @Resource
    private RoomGroupDao roomGroupDao;

    @Override
    public RoomFriend getFriendRoom(Long uid, Long friendUid) {
        String key = ChatAdapter.generateRoomKey(Arrays.asList(uid,friendUid));
        return roomFriendDao.getByKey(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomGroup createGroupRoom(Long uid) {
        List<GroupMember> groupMemberList = groupMemberDao.getSelfGroup(uid);
        AssertUtil.isEmpty(groupMemberList,"每个人只能建一个群");
        User user = userInfoCache.get(uid);
        Room room = CreateRoom(RoomTypeEnum.GROUP);
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user,room);
        roomGroupDao.save(roomGroup);
        GroupMember groupMember = GroupMember.builder()
                .groupId(roomGroup.getId())
                .uid(uid)
                .role(GroupRoleEnum.LEADER.getType())
                .build();
        groupMemberDao.save(groupMember);
        return roomGroup;
    }

    private Room CreateRoom(RoomTypeEnum type) {
        Room room = ChatAdapter.buildRoom(type);
        roomDao.save(room);
        return room;
    }
}
