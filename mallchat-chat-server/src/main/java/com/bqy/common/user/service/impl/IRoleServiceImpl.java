package com.bqy.common.user.service.impl;

import com.bqy.common.user.domain.enums.RoleEnum;
import com.bqy.common.user.service.IRoleService;
import com.bqy.common.user.service.cache.UserCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class IRoleServiceImpl implements IRoleService {
    @Resource
    private UserCache userCache;
    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleIdSet = userCache.getRoleSetById(uid);
        return isAdmin(roleIdSet)||roleIdSet.contains(roleEnum.getId());
    }
    private boolean isAdmin(Set<Long> roleIdSet){
        return roleIdSet.contains(RoleEnum.ADMIN.getId());
    }
}
