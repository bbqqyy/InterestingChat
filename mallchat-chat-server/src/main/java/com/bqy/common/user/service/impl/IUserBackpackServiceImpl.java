package com.bqy.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.enums.YesOrNoEnum;
import com.bqy.common.common.service.LockService;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.dao.UserBackpackDao;
import com.bqy.common.user.domain.entity.UserBackpack;
import com.bqy.common.user.domain.enums.IdempotentEnum;
import com.bqy.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class IUserBackpackServiceImpl implements IUserBackpackService {

    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    @Lazy
    private IUserBackpackServiceImpl userBackpackService;
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId,idempotentEnum,businessId);
        userBackpackService.doAcquireItem(uid,itemId,idempotent);


    }
    @RedissonLock(key = "#idempotent",waitTime = 5000)
    @Transactional(rollbackFor = Exception.class)
    public void doAcquireItem(Long uid,Long itemId,String idempotent){
            UserBackpack userBackpack = userBackpackDao.getItemByIdempotent(idempotent);
            if(ObjectUtil.isNotNull(userBackpack)){
                return;
            }
            UserBackpack insert = UserBackpack.builder()
                    .uid(uid)
                    .itemId(itemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            userBackpackDao.save(insert);

        }


    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s",itemId,idempotentEnum.getType(),businessId);
    }


}
