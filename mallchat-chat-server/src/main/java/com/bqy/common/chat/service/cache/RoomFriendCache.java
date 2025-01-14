package com.bqy.common.chat.service.cache;

import com.bqy.common.chat.dao.RoomFriendDao;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.service.cache.AbstractRedisStringCache;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Resource
    private RoomFriendDao roomFriendDao;
    @Override
    protected String getKey(Long groupId) {
        return RedisKey.getKey(RedisKey.GROUP_FRIEND_STRING,groupId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomFriends = roomFriendDao.getListByIds(roomIds);
        return roomFriends.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }
}
