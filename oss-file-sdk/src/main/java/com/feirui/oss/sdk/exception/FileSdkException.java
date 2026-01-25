package com.feirui.oss.sdk.exception;

import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import lombok.Getter;

@Getter
public class FileSdkException extends Exception {

    /**
     * 错误码
     */
    private final int errCode;
    /**
     * 异常信息
     */
    private final String errMsg;
    /**
     * 异常数据
     */
    private final Object errData;
    /**
     * 原始捕获异常
     */
    private final Throwable cause;

    public FileSdkException(FileSdkRespEnum responseEnum, Object object, Throwable cause) {
        super(responseEnum.getMsg());
        this.errCode = responseEnum.getCode();
        this.errMsg = responseEnum.getMsg();
        this.errData = object;
        this.cause = cause;
    }

}
