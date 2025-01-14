package com.bqy.common.chat.service.impl;

import com.bqy.common.chat.constant.GroupConst;
import com.bqy.common.chat.dao.*;
import com.bqy.common.chat.domain.entity.Contact;
import com.bqy.common.chat.domain.entity.Room;
import com.bqy.common.chat.domain.entity.RoomGroup;
import com.bqy.common.chat.domain.enums.GroupErrorEnum;
import com.bqy.common.chat.domain.vo.req.AdminAddReq;
import com.bqy.common.chat.domain.vo.req.AdminRevokeReq;
import com.bqy.common.chat.domain.vo.req.MemberExitReq;
import com.bqy.common.chat.service.GroupMemberService;
import com.bqy.common.chat.service.adapter.MemberAdapter;
import com.bqy.common.chat.service.cache.GroupMemberCache;
import com.bqy.common.common.exception.CommonErrorEnum;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.service.PushService;
import com.bqy.common.websocket.domain.vo.resp.WSBaseResp;
import com.bqy.common.websocket.domain.vo.resp.WSMemberChange;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    @Resource
    private RoomGroupDao roomGroupDao;
    @Resource
    private RoomDao roomDao;
    @Resource
    private GroupMemberDao groupMemberDao;
    @Resource
    private ContactDao contactDao;
    @Resource
    private MessageDao messageDao;
    @Resource
    private GroupMemberCache groupMemberCache;
    @Resource
    private PushService pushService;

    @Override
    public void exitGroup(Long uid, MemberExitReq req) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(req.getRoomId());
        AssertUtil.isNotEmpty(roomGroup,"房间号有误");
        Room room = roomDao.getById(req.getRoomId());
        AssertUtil.isFalse(room.isHotRoom(), GroupErrorEnum.NOT_ALLOWED_FOR_EXIT_GROUP);
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip,GroupErrorEnum.USER_NOT_IN_GROUP);
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(),uid);
        if(isLord){
            boolean delGroup = roomDao.removeById(req.getRoomId());
            AssertUtil.isTrue(delGroup, CommonErrorEnum.SYSTEM_ERROR);
            boolean delContact = contactDao.removeByRoomId(req.getRoomId(),Collections.EMPTY_LIST);
            AssertUtil.isTrue(delContact,CommonErrorEnum.SYSTEM_ERROR);
            boolean delGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(),Collections.EMPTY_LIST);
            AssertUtil.isTrue(delGroupMember,CommonErrorEnum.SYSTEM_ERROR);
            boolean delMessage = messageDao.removeByRoomId(req.getRoomId(),Collections.EMPTY_LIST);
            AssertUtil.isTrue(delMessage,CommonErrorEnum.SYSTEM_ERROR);
        }else {
            // 4.5 删除会话
            Boolean isDelContact = contactDao.removeByRoomId(req.getRoomId(), Collections.singletonList(uid));
            AssertUtil.isTrue(isDelContact, CommonErrorEnum.SYSTEM_ERROR);
            // 4.6 删除群成员
            Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
            AssertUtil.isTrue(isDelGroupMember, CommonErrorEnum.SYSTEM_ERROR);
            List<Long> memberUidList = groupMemberCache.getMemberUidList(req.getRoomId());
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(),uid);
            pushService.sendPushMsg(ws,memberUidList);
            groupMemberCache.evictMemberUidList(room.getId());

        }
    }

    @Override
    public void addAdmin(Long uid, AdminAddReq req) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(req.getRoomId());
        AssertUtil.isNotEmpty(roomGroup,GroupErrorEnum.GROUP_NOT_EXIST);
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(),uid);
        AssertUtil.isTrue(isLord,GroupErrorEnum.NOT_ALLOWED_OPERATION);
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), req.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);
        List<Long> manageUidList = groupMemberDao.getManageUidList(roomGroup.getId());
        Set<Long> manageUidSet = new HashSet<>(manageUidList);
        manageUidSet.addAll(req.getUidList());
        AssertUtil.isTrue(manageUidSet.size()< GroupConst.MAX_MANAGE_COUNT,GroupErrorEnum.MANAGE_COUNT_EXCEED);
        groupMemberDao.addAdmin(roomGroup.getId(),req.getUidList());
    }

    @Override
    public void revokeAdmin(Long uid, AdminRevokeReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupDao.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 撤销管理员
        groupMemberDao.revokeAdmin(roomGroup.getId(), request.getUidList());
    }
}
