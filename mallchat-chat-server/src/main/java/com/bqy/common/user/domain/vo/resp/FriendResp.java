package com.bqy.common.user.domain.vo.resp;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class FriendResp {
    private Long uid;
    private String name;
    private String avatar;
    private Integer activeStatus;
}
