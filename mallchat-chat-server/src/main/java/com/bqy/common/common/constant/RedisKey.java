package com.bqy.common.common.constant;

public class RedisKey {
    private static final String BASE_KEY = "mallchat:chat";
    public static final String REDIS_KEY = "userToke:uid_%d";
    public static final String USER_MODIFY_STRING = "userModify:uid_%d";
    /**
     * 群组详情
     */
    public static final String GROUP_INFO_STRING = "groupInfo:roomId_%d";
    /**
     * 群组详情
     */
    public static final String GROUP_FRIEND_STRING = "groupFriend:roomId_%d";
    /**
     * 在线用户列表
     */
    public static final String ONLINE_UID_ZET = "online";
    /**
     * 热门房间列表
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * 用户信息
     */
    public static final String USER_INFO_STRING = "userInfo:uid_%d";

    /**
     * 房间详情
     */
    public static final String ROOM_INFO_STRING = "roomInfo:roomId_%d";
    /**
     * 用户的信息汇总
     */
    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";
    public static String getKey(String key,Object... o){
        return BASE_KEY+String.format(key,o);
    }

}
