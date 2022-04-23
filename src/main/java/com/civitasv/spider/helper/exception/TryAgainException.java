package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.TryAgainErrorCode;

public class TryAgainException extends Exception {

    //    默认的错误信息
    private TryAgainErrorCode tryAgainError;

    //    携带的额外信息
    private String extraMessage = null;

    public TryAgainException(TryAgainErrorCode tryAgainErrorCode) {
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode: " + tryAgainErrorCode.getCode() + "\n" +
                "errorMessage: " + tryAgainErrorCode.getDescription() + "\n" +
                "helpInfo:" + tryAgainErrorCode.getHelpinfo());
        tryAgainError = tryAgainErrorCode;
    }

    public TryAgainException(TryAgainErrorCode tryAgainErrorCode, String extraMessage) {
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode : " + tryAgainErrorCode.getCode() + ";\n" +
                "errorMessage" + " : " + tryAgainErrorCode.getDescription() + " --- " + extraMessage + "\n" +
                "helpInfo:" + tryAgainErrorCode.getHelpinfo());
        this.tryAgainError = tryAgainErrorCode;
        this.extraMessage = extraMessage;
    }

    public TryAgainException(TryAgainErrorCode iCostomErrorCodeEnum, Exception deException) {
        this(iCostomErrorCodeEnum);
        addSuppressed(deException);
    }

    public TryAgainException(TryAgainErrorCode iCostomErrorCodeEnum, String extraMessage, Exception deException) {
        this(iCostomErrorCodeEnum, extraMessage);
        addSuppressed(deException);
    }

    public TryAgainErrorCode getError() {
        return tryAgainError;
    }

    public int getExceptionCode() {
        return tryAgainError.getCode();
    }

    public String getExceptionMessage() {
        return tryAgainError.getDescription();
    }

    public String getExtraMessage() {
        return extraMessage;
    }
}
