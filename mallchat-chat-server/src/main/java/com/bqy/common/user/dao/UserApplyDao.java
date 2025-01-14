package com.bqy.common.user.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.domain.enums.ApplyReadStatusEnum;
import com.bqy.common.user.domain.enums.ApplyStatusEnum;
import com.bqy.common.user.domain.enums.ApplyTypeEnum;
import com.bqy.common.user.mapper.UserApplyMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery()
                .eq(UserApply::getUid,uid)
                .eq(UserApply::getTargetId,targetUid)
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVE.getStatus())
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
                .one();
    }

    public void agree(Long applyId) {
        lambdaUpdate()
                .set(UserApply::getId,applyId)
                .set(UserApply::getStatus,ApplyStatusEnum.AGREE.getStatus())
                .update();
    }

    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery()
                .eq(UserApply::getTargetId,targetId)
                .eq(UserApply::getReadStatus, ApplyReadStatusEnum.UN_READ.getStatus())
                .count();

    }

    public IPage<UserApply> friendApplyPage(Long uid, Page plusPage) {
        return lambdaQuery()
                .eq(UserApply::getTargetId,uid)
                .eq(UserApply::getType,ApplyTypeEnum.ADD_FRIEND.getCode())
                .orderByDesc(UserApply::getCreateTime)
                .page(plusPage);

    }

    public void readApply(Long uid, List<Long> userApplyIdList) {
        lambdaUpdate()
                .eq(UserApply::getReadStatus,ApplyReadStatusEnum.UN_READ)
                .eq(UserApply::getTargetId,uid)
                .in(UserApply::getId,userApplyIdList)
                .set(UserApply::getReadStatus,ApplyReadStatusEnum.READ)
                .update();
    }
}
