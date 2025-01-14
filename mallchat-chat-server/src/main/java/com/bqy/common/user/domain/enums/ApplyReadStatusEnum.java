package com.bqy.common.user.domain.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyReadStatusEnum {
    UN_READ(1,"未读"),
    READ(2,"已读");
    private final Integer status;
    private final String desc;
}
