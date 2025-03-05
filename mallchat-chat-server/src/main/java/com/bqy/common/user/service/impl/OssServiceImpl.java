package com.bqy.common.user.service.impl;

import com.bqy.common.common.utils.AssertUtil;
import com.bqy.common.user.domain.enums.OssSceneEnum;
import com.bqy.common.user.domain.vo.req.UploadUrlReq;
import com.bqy.common.user.service.OssService;
import com.bqy.mallchat.oss.MinIOTemplate;
import com.bqy.mallchat.oss.domain.OssReq;
import com.bqy.mallchat.oss.domain.OssResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OssServiceImpl implements OssService {
    @Resource
    private MinIOTemplate minIOTemplate;
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum ossSceneEnum = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(ossSceneEnum,"场景有误");
        OssReq ossReq = OssReq.builder()
                .uid(uid)
                .fileName(req.getFileName())
                .filePath(ossSceneEnum.getPath())
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
