package com.bqy.common.user.service.impl;

import com.bqy.common.common.annotation.RedissonLock;
import com.bqy.common.common.domain.vo.req.IdReqVO;
import com.bqy.common.common.domain.vo.resp.IdRespVO;
import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.dao.UserEmojiDao;
import com.bqy.common.user.domain.entity.UserEmoji;
import com.bqy.common.user.domain.vo.req.UserEmojiReq;
import com.bqy.common.user.domain.vo.resp.UserEmojiResp;
import com.bqy.common.user.service.IUserEmojiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IUserEmojiServiceImpl implements IUserEmojiService {
    @Resource
    private UserEmojiDao userEmojiDao;

    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listByUid(uid)
                .stream()
                .map(emoji -> UserEmojiResp.builder()
                        .id(emoji.getId())
                        .expressionUrl(emoji.getExpressionUrl())
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    @RedissonLock(key = "#uid")
    @Transactional(rollbackFor = Exception.class)
    public IdRespVO addEmoji(Long uid, UserEmojiReq req) {
        int count = userEmojiDao.countByUid(uid);
        AssertUtil.isFalse(count>30,"表情包最多存有30个");
        boolean isExist = userEmojiDao.isExist(uid,req.getExpressionUrl());
        AssertUtil.isFalse(isExist,"该表情已经存在");
        UserEmoji insert = UserEmoji.builder()
                .uid(uid)
                .expressionUrl(req.getExpressionUrl())
                .build();
        userEmojiDao.save(insert);
        return IdRespVO.id(insert.getId());
    }

    @Override
    @RedissonLock(key = "#uid")
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmoji(Long uid, IdReqVO reqVO) {
        UserEmoji userEmoji = userEmojiDao.getById(reqVO.getId());
        AssertUtil.isNotEmpty(userEmoji,"表情包id有误");
        AssertUtil.equal(uid,userEmoji.getUid(),"别人的表情包不可以删除");
        userEmojiDao.removeById(userEmoji);
    }
}
