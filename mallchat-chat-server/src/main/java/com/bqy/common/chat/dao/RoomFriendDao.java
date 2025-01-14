package com.bqy.common.chat.dao;

import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import com.bqy.common.chat.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-27
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend getByKey(String key) {

        return lambdaQuery()
                .eq(RoomFriend::getRoomKey,key)
                .one();
    }

    public void restoreRoom(Long roomId) {
        lambdaUpdate()
                .eq(RoomFriend::getRoomId,roomId)
                .set(RoomFriend::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .update();
    }

    public void disableRoom(String key) {
        lambdaUpdate()
                .eq(RoomFriend::getRoomKey,key)
                .set(RoomFriend::getStatus,NormalOrNoEnum.NOT_NORMAL.getStatus())
                .update();
    }

    public RoomFriend getByRoomId(Long roomId) {
        return lambdaQuery()
                .eq(RoomFriend::getRoomId,roomId)
                .one();
    }

    public List<RoomFriend> getListByIds(List<Long> roomIds) {
        return lambdaQuery()
                .in(RoomFriend::getRoomId,roomIds)
                .list();
    }
}
