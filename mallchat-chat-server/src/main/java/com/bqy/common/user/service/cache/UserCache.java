package com.bqy.common.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.utils.RedisUtils;
import com.bqy.common.user.dao.BlackDao;
import com.bqy.common.user.dao.ItemConfigDao;
import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.dao.UserRoleDao;
import com.bqy.common.user.domain.entity.Black;
import com.bqy.common.user.domain.entity.ItemConfig;
import com.bqy.common.user.domain.entity.User;
import com.bqy.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserCache {

    @Resource
    private UserRoleDao userRoleDao;

    @Resource
    private BlackDao blackDao;

    @Resource
    private UserSummeryCache userSummeryCache;

    @Resource
    private UserDao userDao;

    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid-> RedisKey.getKey(RedisKey.USER_MODIFY_STRING,uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys,Long.class);
    }

    public void refreshUserModifyTime(Long uid){
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING,uid);
        RedisUtils.set(key,new Date().getTime());
    }
    public void deleteUserInfo(Long uid){
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING,uid);
        RedisUtils.del(key);
    }
    public void userInfoChange(Long uid){
        deleteUserInfo(uid);
        userSummeryCache.delete(uid);
        refreshUserModifyTime(uid);
    }
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

    public Long getOnlineNum() {
        String key = RedisKey.getKey(RedisKey.ONLINE_UID_ZET);
        return RedisUtils.zCard(key);
    }

    public User getUserInfo(Long uid) {
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    public Map<Long,User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(uid-> RedisKey.getKey(RedisKey.USER_INFO_STRING,uid)).collect(Collectors.toList());
        //批量get
        List<User> users = RedisUtils.mget(keys,User.class);
        Map<Long,User> map = users.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        //load需要更新的uid
        List<Long> needLoadUidList = uids.stream().filter(uid->!map.containsKey(uid)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(needLoadUidList)){
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String,User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a->RedisKey.getKey(RedisKey.USER_INFO_STRING,a.getId()),Function.identity()));
            RedisUtils.mset(redisMap,5*60);
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId,Function.identity())));
        }
        return map;
    }

    public void offLine(Long id, Date lastOptTime) {
        String onlineKey = RedisKey.ONLINE_UID_ZET;
        String offlineKey = RedisKey.OFFLINE_UID_ZET;
        RedisUtils.zRemove(onlineKey,id);
        RedisUtils.zAdd(offlineKey,id,lastOptTime.getTime());
    }
}
