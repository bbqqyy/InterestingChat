package com.bqy.common.user.domain.vo.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyNameReq {
    @NotBlank
    @Length(max = 6,message = "名字太长，最多不能超过六位")
    private String name;

    @NotNull
    private Long uid;
}
