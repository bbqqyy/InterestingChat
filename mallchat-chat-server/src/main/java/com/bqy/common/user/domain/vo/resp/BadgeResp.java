package com.bqy.common.user.domain.vo.resp;

import lombok.Data;

@Data
public class BadgeResp {
    private Long id;
    private String img;
    private String description;
    private Integer obtain;
    private Integer wearing;
}
