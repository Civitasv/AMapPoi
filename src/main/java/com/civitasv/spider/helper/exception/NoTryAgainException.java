package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.NoTryAgainErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class NoTryAgainException extends Exception {
    // 默认的错误信息
    private NoTryAgainErrorCode noTryAgainError;
    // 携带的额外信息
    private String extraMessage = null;

    public NoTryAgainException(NoTryAgainErrorCode noTryAgainErrorCode) {
        // 将 {@link getMessage()} 方法的返回值与自定义异常信息同步
        super("errorCode: " + noTryAgainErrorCode.code() + "\n" +
                "errorMessage: " + noTryAgainErrorCode.description() + "\n" +
                "helpInfo:" + noTryAgainErrorCode.helpMessage());
        this.noTryAgainError = noTryAgainErrorCode;
    }

    /**
     * 将getMessage()方法的返回值与自定义异常信息同步
     */
    public NoTryAgainException(NoTryAgainErrorCode noTryAgainErrorCode, String extraMessage) {
        super("errorCode : " + noTryAgainErrorCode.code() + ";\n" +
                "errorMessage" + " : " + noTryAgainErrorCode.description() + " --- " + extraMessage + "\n" +
                "helpInfo:" + noTryAgainErrorCode.helpMessage());
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
}
