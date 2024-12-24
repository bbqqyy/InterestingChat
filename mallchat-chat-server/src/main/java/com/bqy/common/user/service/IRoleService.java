package com.bqy.common.user.service;

import com.bqy.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bqy.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-24
 */
public interface IRoleService {
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
