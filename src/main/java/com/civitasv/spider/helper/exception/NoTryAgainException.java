package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.NoTryAgainErrorCode;

public class NoTryAgainException extends Exception {

    //    默认的错误信息
    private NoTryAgainErrorCode noTryAgainError;

    //    携带的额外信息
    private String extraMessage = null;

    public NoTryAgainException(NoTryAgainErrorCode noTryAgainErrorCode) {
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode: " + noTryAgainErrorCode.getCode() + "\n" +
                "errorMessage: " + noTryAgainErrorCode.getDescription() + "\n" +
                "helpInfo:" + noTryAgainErrorCode.getHelpinfo());
        noTryAgainError = noTryAgainErrorCode;
    }

    public NoTryAgainException(NoTryAgainErrorCode noTryAgainErrorCode, String extraMessage) {
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode : " + noTryAgainErrorCode.getCode() + ";\n" +
                "errorMessage" + " : " + noTryAgainErrorCode.getDescription() + " --- " + extraMessage + "\n" +
                "helpInfo:" + noTryAgainErrorCode.getHelpinfo());
        this.noTryAgainError = noTryAgainErrorCode;
        this.extraMessage = extraMessage;
    }

    public NoTryAgainException(NoTryAgainErrorCode noTryAgainErrorCode, Exception deException) {
        this(noTryAgainErrorCode);
        addSuppressed(deException);
    }

    public NoTryAgainException(NoTryAgainErrorCode noTryErrorCode, String extraMessage, Exception deException) {
        this(noTryErrorCode, extraMessage);
        addSuppressed(deException);
    }

    public NoTryAgainErrorCode getNoTryAgainError() {
        return noTryAgainError;
    }

    public int getExceptionCode() {
        return noTryAgainError.getCode();
    }

    public String getExceptionMessage() {
        return noTryAgainError.getDescription();
    }

    public String getExtraMessage() {
        return extraMessage;
    }
}
