package com.bqy.common.user.dao;

import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.domain.entity.UserBackpack;
import com.bqy.common.user.domain.enums.ItemEnum;
import com.bqy.common.user.mapper.UserBackpackMapper;
import com.bqy.common.user.service.IUserBackpackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-19
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    public Integer getCountByValidItems(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getItemId,itemId)
                .eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .count();

    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getItemId,itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .orderByAsc(UserBackpack::getItemId)
                .last("limit 1")
                .one();
    }

    public boolean useItem(UserBackpack item) {
        return lambdaUpdate()
                .eq(UserBackpack::getId,item.getId())
                .eq(UserBackpack::getStatus,YesOrNoEnum.NO.getStatus())
                .set(UserBackpack::getStatus,YesOrNoEnum.YES.getStatus())
                .update();
    }

    public List<UserBackpack> getOwnedBadge(Long uid, List<Long> badgeIds) {
        return lambdaQuery()
                .eq(UserBackpack::getUid,uid)
                .eq(UserBackpack::getStatus,YesOrNoEnum.NO.getStatus())
                .in(UserBackpack::getItemId,badgeIds)
                .list();
    }

    public UserBackpack getItemByIdempotent(String idempotent) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent,idempotent)
                .one();
    }
}
