package com.bqy.common.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.entity.UserBackpack;
import com.bqy.common.user.domain.vo.resp.BadgeResp;
import com.bqy.common.user.domain.vo.resp.UserInfoResp;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdapter {

    public static User buildUser(String openId) {
        return User.builder().openId(openId).build();
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {

        User user = new User();
        user.setId(id);
        user.setName(userInfo.getNickname());
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setSex(userInfo.getSex());
        return user;


    }

    public static UserInfoResp buildUseInfoResp(User user, Integer modifyNameCount) {
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtil.copyProperties(user,userInfoResp);
        userInfoResp.setModifyNameChance(modifyNameCount);
        return userInfoResp;


    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> badges, List<UserBackpack> userBackpacks, User user) {

        Set<Long> obtainedItems = userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());
        return badges.stream().map(badge -> {
            BadgeResp badgeResp = new BadgeResp();
            BeanUtil.copyProperties(badge, badgeResp);
            badgeResp.setObtain(obtainedItems.contains(badge.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            badgeResp.setWearing(ObjectUtil.equals(badge.getId(), user.getItemId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return badgeResp;
        }).sorted(Comparator.comparing(BadgeResp::getWearing, Comparator.reverseOrder())
                .thenComparing(BadgeResp::getObtain, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
