package com.bqy.common.user.service;

import com.bqy.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bqy.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-19
 */
public interface IUserBackpackService{
    /**
     * 用户发放一个物品
     * @param uid
     * @param itemId
     * @param idempotentEnum
     * @param businessId
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum,String businessId);
}
