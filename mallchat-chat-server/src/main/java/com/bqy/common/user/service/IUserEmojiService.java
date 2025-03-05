package com.bqy.common.user.service;

import com.bqy.common.common.domain.vo.req.IdReqVO;
import com.bqy.common.common.domain.vo.resp.IdRespVO;
import com.bqy.common.user.domain.entity.UserEmoji;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bqy.common.user.domain.vo.req.UserEmojiReq;
import com.bqy.common.user.domain.vo.resp.UserEmojiResp;

import java.util.List;

/**
 * <p>
 * 用户表情包 服务类
 * </p>
 *
 * @author ${author}
 * @since 2025-01-15
 */
public interface IUserEmojiService{

    List<UserEmojiResp> list(Long uid);

    IdRespVO addEmoji(Long uid, UserEmojiReq req);

    void deleteEmoji(Long uid, IdReqVO reqVO);
}
