package com.bqy.common.user.domain.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class BlackReq {
    @NotNull
    private Long uid;
}
