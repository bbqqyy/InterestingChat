package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.chat.domain.entity.RoomGroup;

public interface RoomService {
    RoomFriend getFriendRoom(Long uid, Long friendUid);

    RoomGroup createGroupRoom(Long uid);
}
