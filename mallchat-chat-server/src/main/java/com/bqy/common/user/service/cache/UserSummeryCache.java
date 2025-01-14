package com.bqy.common.user.service.cache;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.user.domain.dto.SummeryInfoDTO;
import com.bqy.common.common.service.cache.AbstractRedisStringCache;
import com.bqy.common.user.dao.UserBackpackDao;
import com.bqy.common.user.domain.entity.*;
import com.bqy.common.user.domain.enums.ItemTypeEnum;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserSummeryCache extends AbstractRedisStringCache<Long, SummeryInfoDTO> {

    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private ItemCache itemCache;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_STRING,uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 10*60L;
    }

    @Override
    protected Map<Long, SummeryInfoDTO> load(List<Long> uidList) {
        Map<Long,User> userMap = userInfoCache.load(uidList);
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> userBackpacks = userBackpackDao.getItemByIds(uidList,itemIds);
        Map<Long,List<UserBackpack>> badgeMap = userBackpacks.stream().collect(Collectors.groupingBy(UserBackpack::getUid));
        return uidList.stream().map(uid->{
            SummeryInfoDTO summeryInfoDTO = new SummeryInfoDTO();
            User user = userMap.get(uid);
            if(ObjectUtil.isNull(user)){
                return null;
            }
            List<UserBackpack> userBadgeList = badgeMap.getOrDefault(user.getId(),new ArrayList<>());
            summeryInfoDTO.setUid(user.getId());
            summeryInfoDTO.setName(user.getName());
            summeryInfoDTO.setAvatar(user.getAvatar());
            summeryInfoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            summeryInfoDTO.setWearingItemId(user.getItemId());
            summeryInfoDTO.setItemIds(userBadgeList.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            return summeryInfoDTO;
        }).filter(ObjectUtil::isNotNull)
                .collect(Collectors.toMap(SummeryInfoDTO::getUid, Function.identity()));
    }
}
