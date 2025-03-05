package com.bqy.common.user.dao;

import com.bqy.common.user.domain.entity.UserEmoji;
import com.bqy.common.user.domain.vo.resp.UserEmojiResp;
import com.bqy.common.user.mapper.UserEmojiMapper;
import com.bqy.common.user.service.IUserEmojiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2025-01-15
 */
@Service
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {

    public List<UserEmoji> listByUid(Long uid) {
        return lambdaQuery()
                .eq(UserEmoji::getUid, uid)
                .list();
    }

    public int countByUid(Long uid) {
        return lambdaQuery()
                .eq(UserEmoji::getUid, uid)
                .count();
    }

    public boolean isExist(Long uid, String expressionUrl) {
        int count = lambdaQuery()
                .eq(UserEmoji::getUid, uid)
                .eq(UserEmoji::getExpressionUrl, expressionUrl)
                .count();
        return count > 0;
    }
}
