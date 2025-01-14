package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.chat.domain.vo.resp.MemberResp;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-27
 */
public interface IRoomService  {

    RoomFriend createFriendRoom(List<Long> uidList);

    void disableRoom(List<Long> uidList);

}
