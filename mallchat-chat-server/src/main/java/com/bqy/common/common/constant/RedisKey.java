package com.bqy.common.common.constant;

public class RedisKey {
    private static final String BASE_KEY = "mallchat:chat";
    public static final String REDIS_KEY = "userToke:uid_%d";

    public static String getKey(String key,Object... o){
        return BASE_KEY+String.format(key,o);
    }

}
