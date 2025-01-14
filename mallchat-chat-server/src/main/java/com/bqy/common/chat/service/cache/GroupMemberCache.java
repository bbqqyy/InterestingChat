package com.bqy.common.chat.service.cache;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.dao.GroupMemberDao;
import com.bqy.common.chat.dao.RoomDao;
import com.bqy.common.chat.dao.RoomGroupDao;
import com.bqy.common.chat.domain.entity.GroupMember;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomGroup;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class GroupMemberCache {
    @Resource
    private RoomGroupDao roomGroupDao;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Cacheable(cacheNames = "member",key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId){
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if(ObjectUtil.isNull(roomGroup)){
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }
    @CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }
}
