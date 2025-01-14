package com.bqy.common.chat.service;

import com.bqy.common.chat.domain.vo.req.AdminAddReq;
import com.bqy.common.chat.domain.vo.req.AdminRevokeReq;
import com.bqy.common.chat.domain.vo.req.MemberExitReq;

public interface GroupMemberService {
    void exitGroup(Long uid, MemberExitReq req);

    void addAdmin(Long uid, AdminAddReq req);

    void revokeAdmin(Long uid, AdminRevokeReq request);
}
