package com.bqy.common.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.req.PageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.domain.vo.resp.PageBaseResp;
import com.bqy.common.common.event.UserApplyEvent;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.dao.UserApplyDao;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.dao.UserFriendDao;
import com.bqy.common.chat.domain.entity.RoomFriend;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.domain.entity.UserFriend;
import com.bqy.common.user.domain.enums.ApplyStatusEnum;
import com.bqy.common.user.domain.vo.req.FriendApplyReq;
import com.bqy.common.user.domain.vo.req.FriendApproveReq;
import com.bqy.common.user.domain.vo.req.FriendCheckReq;
import com.bqy.common.user.domain.vo.resp.FriendApplyResp;
import com.bqy.common.user.domain.vo.resp.FriendCheckResp;
import com.bqy.common.user.domain.vo.resp.FriendResp;
import com.bqy.common.user.domain.vo.resp.FriendUnReadResp;
import com.bqy.common.chat.service.IRoomService;
import com.bqy.common.user.service.IUserFriendService;
import com.bqy.common.user.service.adapter.FriendAdapter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IUserFriendServiceImpl implements IUserFriendService {
    @Resource
    private UserDao userDao;
    @Resource
    private UserFriendDao userFriendDao;
    @Resource
    private UserApplyDao userApplyDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private IRoomService roomService;
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid,request);
        if(CollectionUtil.isEmpty(friendPage.getList())){
            return CursorPageBaseResp.empty();
        }
        List<Long> friendIdList = friendPage.getList().stream()
                .map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> friendList = userDao.getFriendList(friendIdList);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(),friendList));

    }

    @Override
    public FriendCheckResp checkFriend(Long uid, FriendCheckReq friendCheckReq) {
        List<UserFriend> friendList = userFriendDao.getFriendByIds(uid,friendCheckReq.getUidList());
        Set<Long> friendIdSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendChecks = friendCheckReq.getUidList().stream().map(friendUid->{
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendIdSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return FriendCheckResp.builder()
                .checkedList(friendChecks).
                        build();
    }

    @Override
    public void applyFriend(Long uid, FriendApplyReq request) {
        UserFriend friend = userFriendDao.getByFriend(uid,request.getTargetUid());
        AssertUtil.isEmpty(friend,"你们已经是好友了");
        UserApply selfApproving = userApplyDao.getFriendApproving(uid,request.getTargetUid());
        if(ObjectUtil.isNotNull(selfApproving)){
            log.info("已有好友申请 uid:{},targetUid:{}",uid,request.getTargetUid());
            return;
        }
        UserApply otherApproving = userApplyDao.getFriendApproving(request.getTargetUid(),uid);
        if(ObjectUtil.isNotNull(otherApproving)){
            ((IUserFriendService) AopContext.currentProxy()).applyApprove(uid,new FriendApproveReq(otherApproving.getId()));
            return;
        }
        UserApply insert = FriendAdapter.buildFriendApply(uid,request);
        userApplyDao.save(insert);
        applicationEventPublisher.publishEvent(new UserApplyEvent(this,insert));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply,"不存在申请记录");
        AssertUtil.equal(uid,userApply.getTargetId(),"不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), ApplyStatusEnum.WAIT_APPROVE,"已同意好友申请");

        userApplyDao.agree(request.getApplyId());
        createFriend(uid,userApply.getUid());
        RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid,userApply.getUid()));


    }

    @Override
    public FriendUnReadResp unRead(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnReadResp(unReadCount);
    }

    @Override
    public PageBaseResp<FriendApplyResp> applyFriendPage(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid,request.plusPage());
        if(CollectionUtil.isEmpty(userApplyIPage.getRecords())){
            return PageBaseResp.empty();
        }
        List<Long> userApplyIdList = userApplyIPage.getRecords().stream()
                .map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApply(uid,userApplyIdList);
        return PageBaseResp.init(userApplyIPage,FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long targetId) {
        List<UserFriend> userFriendList = userFriendDao.getFriendUser(uid,targetId);
        if(CollectionUtil.isEmpty(userFriendList)){
            log.info("没有好友关系：{},{}",uid,targetId);
            return;
        }
        List<Long> friendRecordIds = userFriendList.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        roomService.disableRoom(Arrays.asList(uid,targetId));
    }

    private void createFriend(Long uid, Long uid1) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(uid1);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(uid1);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1,userFriend2));
    }
}
