package com.bqy.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoResp {
    @ApiModelProperty(value = "uid")
    private Long uid;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "剩余改名机会")
    private Integer modifyNameChance;
}
