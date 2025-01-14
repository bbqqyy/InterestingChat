package com.bqy.common.chat.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberListResp {
    @ApiModelProperty("uid")
    private Long uid;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("头像")
    private String avatar;
}
