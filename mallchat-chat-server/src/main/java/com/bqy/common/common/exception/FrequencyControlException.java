package com.bqy.common.common.exception;

import lombok.Data;

@Data
public class FrequencyControlException extends RuntimeException {
    private static final long serialVersionUId = 1L;
    protected Integer errorCode;
    protected String errorMsg;

    public FrequencyControlException() {
        super();
    }
    public FrequencyControlException(String errorMsg){
        super(errorMsg);
        this.errorMsg = errorMsg;
    }
    public FrequencyControlException(ErrorEnum errorEnum){
        super(errorEnum.getErrorMsg());
        this.errorMsg = errorEnum.getErrorMsg();
        this.errorCode = errorEnum.getErrorCode();
    }

}
