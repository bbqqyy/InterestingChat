package com.bqy.common.user.service;

import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.req.PageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.domain.vo.resp.PageBaseResp;
import com.bqy.common.user.domain.vo.req.FriendApplyReq;
import com.bqy.common.user.domain.vo.req.FriendApproveReq;
import com.bqy.common.user.domain.vo.req.FriendCheckReq;
import com.bqy.common.user.domain.vo.resp.FriendApplyResp;
import com.bqy.common.user.domain.vo.resp.FriendCheckResp;
import com.bqy.common.user.domain.vo.resp.FriendResp;
import com.bqy.common.user.domain.vo.resp.FriendUnReadResp;

/**
 * <p>
 * 用户联系人表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
public interface IUserFriendService {

    CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request);

    FriendCheckResp checkFriend(Long uid, FriendCheckReq friendApplyReq);

    void applyFriend(Long uid, FriendApplyReq request);

    void applyApprove(Long uid, FriendApproveReq request);

    FriendUnReadResp unRead(Long uid);

    PageBaseResp<FriendApplyResp> applyFriendPage(Long uid, PageBaseReq request);

    void deleteFriend(Long uid, Long targetId);
}
