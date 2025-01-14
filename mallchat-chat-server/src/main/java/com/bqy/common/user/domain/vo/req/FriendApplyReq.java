package com.bqy.common.user.domain.vo.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyReq {
    @NotBlank
    private String msg;

    @NotNull
    private Long targetUid;
}
