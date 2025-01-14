package com.bqy.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyTypeEnum {
    ADD_FRIEND(1,"添加好友");

    private final Integer code;
    private final String desc;
}
