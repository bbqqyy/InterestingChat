package com.bqy.common.user.service.cache;

import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.service.cache.AbstractRedisStringCache;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserInfoCache extends AbstractRedisStringCache<Long, User> {
    @Resource
    private UserDao userDao;
    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_INFO_STRING,uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> userList = userDao.listByIds(uidList);
        return userList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
