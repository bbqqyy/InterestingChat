package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.UserApply;
import com.bqy.common.user.mapper.UserApplyMapper;
import com.bqy.common.user.service.IUserApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户申请表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> implements IUserApplyService {

}
