package com.bqy.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum GroupRoleAppEnum {
    LEADER(1, "群主"),
    MANAGER(2, "管理"),
    MEMBER(3, "普通成员"),
    REMOVE(4, "被移除的成员"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, GroupRoleAppEnum> cache;

    static {
        cache = Arrays.stream(GroupRoleAppEnum.values()).collect(Collectors.toMap(GroupRoleAppEnum::getType, Function.identity()));
    }

    public static GroupRoleAppEnum of(Integer type) {
        return cache.get(type);
    }
}