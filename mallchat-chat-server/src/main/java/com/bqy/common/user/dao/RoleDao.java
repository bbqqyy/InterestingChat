package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.Role;
import com.bqy.common.user.mapper.RoleMapper;
import com.bqy.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-24
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
