package com.bqy.common.chat.service.cache;

import com.bqy.common.chat.dao.RoomGroupDao;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.service.cache.AbstractRedisStringCache;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {
    @Resource
    private RoomGroupDao roomGroupDao;
    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING,roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroupList = roomGroupDao.listByRoomIds(roomIds);
        return roomGroupList.stream()
                .collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));

    }

}
