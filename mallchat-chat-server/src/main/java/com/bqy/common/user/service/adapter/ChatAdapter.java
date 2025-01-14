package com.bqy.common.user.service.adapter;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.enums.HotFlagEnum;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import com.bqy.common.chat.domain.enums.RoomTypeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatAdapter {
    public static final String SEPARATOR = ",";

    public static String generateRoomKey(List<Long> uidList) {
        return uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    public static Room buildRoom(RoomTypeEnum type) {
        Room room = new Room();
        room.setType(type.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;

    }

    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;

    }

    public static Set<Long> getFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream()
                .map(friend->getFriendUid(friend,uid))
                .collect(Collectors.toSet());
    }

    public static Long getFriendUid(RoomFriend roomFriend,Long uid) {
        return ObjectUtil.equals(roomFriend.getUid1(),uid)?roomFriend.getUid1():roomFriend.getUid2();
    }

    public static RoomGroup buildGroupRoom(User user, Room room) {
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setRoomId(room.getId());
        roomGroup.setName(user.getName()+"的群聊");
        roomGroup.setAvatar(user.getAvatar());
        return roomGroup;

    }
}
