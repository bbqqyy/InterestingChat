package com.bqy.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.bqy.common.common.constant.RedisKey;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import com.bqy.common.common.utils.CursorUtils;
import com.bqy.common.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class HotRoomCache {
    public CursorPageBaseResp<Pair<Long,Double>> getRoomCursorPage(CursorPageBaseReq cursorPageBaseReq){
        return CursorUtils.getCursorPageByRedis(cursorPageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_ZET),Long::parseLong);
    }
    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart,Double hotEnd){
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET),hotStart,hotEnd);
    }
    public void refreshActiveTime(Long roomId, Date refreshTime){
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET),roomId,(double) refreshTime.getTime());
    }


}
