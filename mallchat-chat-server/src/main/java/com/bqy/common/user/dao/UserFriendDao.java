package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.UserFriend;
import com.bqy.common.user.mapper.UserFriendMapper;
import com.bqy.common.user.service.IUserFriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户联系人表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-12-25
 */
@Service
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> implements IUserFriendService {

}
