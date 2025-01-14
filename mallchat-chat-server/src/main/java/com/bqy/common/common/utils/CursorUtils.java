package com.bqy.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bqy.common.common.domain.vo.req.CursorPageBaseReq;
import com.bqy.common.common.domain.vo.resp.CursorPageBaseResp;
import org.springframework.data.redis.core.ZSetOperations;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CursorUtils {
    public static <T> CursorPageBaseResp<T> getCursorPageByMySql(IService<T> mapper, CursorPageBaseReq cursorPageBaseReq, Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T,?> cursorColumn){
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        initWrapper.accept(wrapper);
        if(StringUtils.isNotBlank(cursorPageBaseReq.getCursor())){
            wrapper.lt(cursorColumn,parseCursor(cursorPageBaseReq.getCursor(),cursorType));
        }
        wrapper.orderByDesc(cursorColumn);
        Page<T> page = mapper.page(cursorPageBaseReq.plusPage(),wrapper);
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(String::valueOf)
                .orElse(null);
        boolean isLast = page.getRecords().size() != cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }
    public static <T> CursorPageBaseResp<Pair<T,Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String,T> typeConvert){
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if(StrUtil.isBlank(cursorPageBaseReq.getCursor())){
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey,cursorPageBaseReq.getPageSize());
        }else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey,Double.parseDouble(cursorPageBaseReq.getCursor()),cursorPageBaseReq.getPageSize());
        }
        List<Pair<T,Double>> result = typedTuples.stream()
                .map(t-> Pair.of(typeConvert.apply(t.getValue()),t.getScore()))
                .sorted((o1,o2)->o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);
        Boolean isLast = result.size()!=cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor,isLast,result);
    }

    private static Object parseCursor(String cursor, Class<?> cursorType) {
        if(Date.class.isAssignableFrom(cursorType)){
            return new Date(Long.parseLong(cursor));
        }else {
            return cursor;
        }
    }
    private static String toCursor(Object o){
        if(o instanceof Date){
            return String.valueOf(((Date) o).getTime());
        }else {
            return o.toString();
        }
    }
}
