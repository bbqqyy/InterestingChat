package com.bqy.common.user.service;

import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.vo.req.BlackReq;
import com.bqy.common.user.domain.vo.req.WearBadgeReq;
import com.bqy.common.user.domain.vo.resp.BadgeResp;
import com.bqy.common.user.domain.vo.resp.UserInfoResp;

import java.util.List;

public interface UserService {
    Long register(User user);

    UserInfoResp getUserInfo(Long uid);

    void modifyName(Long uid, String name);

    List<BadgeResp> badges(Long uid);

    void wearBadge(Long uid, Long itemId);

    void blackUser(BlackReq blackReq);
}
