package com.bqy.common.user.service.cache;

import com.bqy.common.user.dao.BlackDao;
import com.bqy.common.user.dao.ItemConfigDao;
import com.bqy.common.user.dao.UserRoleDao;
import com.bqy.common.user.domain.entity.Black;
import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private BlackDao blackDao;

    @Cacheable(cacheNames = "user",key = "'roles'+#uid")
    public Set<Long> getRoleSetById(Long uid) {
        List<UserRole> roleList = userRoleDao.getRoleById(uid);
        return roleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
    @Cacheable(cacheNames = "user",key = "'blackList'")
    public Map<Integer,Set<String>> getBlackMap() {
        Map<Integer, List<Black>> listMap = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer,Set<String>> blackMap = new HashMap<>();
        listMap.forEach((type,blackList)->{
            blackMap.put(type,blackList.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return blackMap;
    }
    @CacheEvict(cacheNames = "user",key = "'blackList'")
    public Map<Integer,Set<String>> evictBlackMap(){
        return null;
    }
}
