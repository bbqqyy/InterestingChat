package com.bqy.common.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.chat.domain.vo.resp.MemberResp;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.chat.dao.RoomDao;
import com.bqy.common.chat.dao.RoomFriendDao;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.user.domain.enums.NormalOrNoEnum;
import com.bqy.common.chat.domain.enums.RoomTypeEnum;
import com.bqy.common.chat.service.IRoomService;
import com.bqy.common.user.service.adapter.ChatAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class IRoomServiceImpl implements IRoomService {
    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private RoomDao roomDao;

    @Override
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList,"房间人数不能为空");
        AssertUtil.equal(uidList.size(),2,"房间人数不对，只能为两个人");
        String key = ChatAdapter.generateRoomKey(uidList);
        RoomFriend roomFriend = roomFriendDao.getByKey(key);
        if(ObjectUtil.isNotNull(roomFriend)){
            restoreRoomIfNeed(roomFriend);
        }else {
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = createFriendRoom(room.getId(),uidList);
        }
        return roomFriend;

    }

    @Override
    public void disableRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);
        roomFriendDao.disableRoom(key);
    }


    private Room createRoom(RoomTypeEnum type) {
        Room insert = ChatAdapter.buildRoom(type);
        roomDao.save(insert);
        return insert;
    }
    private RoomFriend createFriendRoom(Long roomId,List<Long> uidList){
        RoomFriend roomFriend = ChatAdapter.buildFriendRoom(roomId,uidList);
        roomFriendDao.save(roomFriend);
        return roomFriend;
    }


    private void restoreRoomIfNeed(RoomFriend roomFriend) {
        if(ObjectUtil.equals(roomFriend.getStatus(), NormalOrNoEnum.NOT_NORMAL.getStatus())){
            roomFriendDao.restoreRoom(roomFriend.getRoomId());
        }
    }
}
