package com.bqy.common.chat.dao;

import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-27
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {

    public List<Room> getRoomByRoomIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(Room::getId,roomIds)
                .list();
    }

    public void refreshActiveTime(Long roomId, Long msgId, Date createTime) {
        lambdaUpdate()
                .eq(Room::getId,roomId)
                .set(Room::getLastMsgId,msgId)
                .set(Room::getActiveTime,createTime)
                .update();
    }
}
