package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.UserRole;
import com.bqy.common.user.mapper.UserRoleMapper;
import com.bqy.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-24
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    public List<UserRole> getRoleById(Long uid) {
        return lambdaQuery()
                .eq(UserRole::getUid,uid)
                .list();
    }
}
