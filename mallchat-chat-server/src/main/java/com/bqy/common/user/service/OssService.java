package com.bqy.common.user.service;

import com.bqy.common.user.domain.vo.req.UploadUrlReq;
import com.bqy.mallchat.oss.domain.OssResp;

public interface OssService {

    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
