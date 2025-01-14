package com.bqy.mallchat.transaction.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecureInvokeDTO {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
