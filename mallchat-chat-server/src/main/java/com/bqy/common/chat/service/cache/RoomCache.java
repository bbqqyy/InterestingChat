package com.bqy.common.chat.service.cache;

import com.bqy.common.chat.dao.RoomDao;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.service.cache.AbstractRedisStringCache;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    @Resource
    private RoomDao roomDao;
    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING,roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.getRoomByRoomIds(roomIds);
        return rooms.stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
