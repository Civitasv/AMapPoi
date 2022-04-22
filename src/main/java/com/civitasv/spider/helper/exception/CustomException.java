package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.CustomErrorCodeEnum;

public class CustomException extends Exception {

//    默认的错误信息
    private CustomErrorCodeEnum costomErrorCodeEnum;

//    携带的额外信息
    private String extraMessage = null;

    public CustomException(CustomErrorCodeEnum customErrorCodeEnum){
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode: " + customErrorCodeEnum.getCode() + "\n" +
                "errorMessage: " + customErrorCodeEnum.getDescription() + "\n" +
                "helpInfo:" + customErrorCodeEnum.getHelpinfo());
        costomErrorCodeEnum = customErrorCodeEnum;
    }

    public CustomException(CustomErrorCodeEnum customErrorCodeEnum, String extraMessage){
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode : " + customErrorCodeEnum.getCode() + ";\n" +
                "errorMessage" + " : " + customErrorCodeEnum.getDescription() + " --- " + extraMessage + "\n" +
                "helpInfo:" + customErrorCodeEnum.getHelpinfo());
        this.costomErrorCodeEnum = customErrorCodeEnum;
        this.extraMessage = extraMessage;
    }

    public CustomException(CustomErrorCodeEnum iCostomErrorCodeEnum, Exception deException){
        this(iCostomErrorCodeEnum);
        addSuppressed(deException);
    }

    public CustomException(CustomErrorCodeEnum iCostomErrorCodeEnum, String extraMessage, Exception deException){
        this(iCostomErrorCodeEnum, extraMessage);
        addSuppressed(deException);
    }

    public CustomErrorCodeEnum getCostomErrorCodeEnum() {
        return costomErrorCodeEnum;
    }

    public int getExceptionCode(){
        return costomErrorCodeEnum.getCode();
    }

    public String getExceptionMessage(){
        return costomErrorCodeEnum.getDescription();
    }

    public String getExtraMessage() {
        return extraMessage;
    }
}
