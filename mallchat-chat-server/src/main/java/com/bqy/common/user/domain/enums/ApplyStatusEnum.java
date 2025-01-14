package com.bqy.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyStatusEnum {
    WAIT_APPROVE(1,"等待同意"),
    AGREE(2,"同意");
    private final Integer status;
    private final String desc;
}
