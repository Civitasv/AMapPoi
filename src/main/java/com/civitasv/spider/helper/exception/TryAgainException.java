package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.TryAgainErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
public class TryAgainException extends Exception {
    // 默认的错误信息
    private TryAgainErrorCode tryAgainError;

    // 携带的额外信息
    private String extraMessage = null;

    public TryAgainException(TryAgainErrorCode tryAgainErrorCode) {
        // 将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode: " + tryAgainErrorCode.code() + "\n" +
                "errorMessage: " + tryAgainErrorCode.description() + "\n" +
                "helpInfo:" + tryAgainErrorCode.helpMessage());
        this.tryAgainError = tryAgainErrorCode;
    }

    public TryAgainException(TryAgainErrorCode tryAgainErrorCode, String extraMessage) {
        // 将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode: " + tryAgainErrorCode.code() + ";\n" +
                "errorMessage" + ": " + tryAgainErrorCode.description() + " --- " + extraMessage + "\n" +
                "helpInfo: " + tryAgainErrorCode.helpMessage());
        this.tryAgainError = tryAgainErrorCode;
        this.extraMessage = extraMessage;
    }

    public TryAgainException(TryAgainErrorCode iCustomErrorCodeEnum, Exception deException) {
        this(iCustomErrorCodeEnum);
        addSuppressed(deException);
    }

    public TryAgainException(TryAgainErrorCode iCustomErrorCodeEnum, String extraMessage, Exception deException) {
        this(iCustomErrorCodeEnum, extraMessage);
        addSuppressed(deException);
    }
}
