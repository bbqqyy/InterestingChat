package com.bqy.common.user.service;

import com.bqy.common.user.domain.dto.ItemInfoDTO;
import com.bqy.common.user.domain.dto.SummeryInfoDTO;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.vo.req.BlackReq;
import com.bqy.common.user.domain.vo.req.ItemInfoReq;
import com.bqy.common.user.domain.vo.req.SummeryInfoReq;
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

    List<SummeryInfoDTO> getSummeryInfo(SummeryInfoReq req);

    List<ItemInfoDTO> getItemInfo(ItemInfoReq req);
}
