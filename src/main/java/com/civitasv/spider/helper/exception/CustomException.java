package com.civitasv.spider.helper.exception;

import com.civitasv.spider.helper.Enum.CustomErrorCodeEnum;

public class CustomException extends Exception {

//    默认的错误信息
    private CustomErrorCodeEnum costomErrorCodeEnum;

//    携带的额外信息
    private String extraMessage = null;

    public CustomException(CustomErrorCodeEnum iCostomErrorCodeEnum){
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode :" + iCostomErrorCodeEnum.getCode() + ";\n" +
                "errorMessage" + ":" + iCostomErrorCodeEnum.getDescription());
        costomErrorCodeEnum = iCostomErrorCodeEnum;
    }

    public CustomException(CustomErrorCodeEnum costomErrorCodeEnum, String extraMessage){
//        将getMessage()方法的返回值与自定义异常信息同步
        super("errorCode : " + costomErrorCodeEnum.getCode() + ";\n" +
                "errorMessage" + " : " + costomErrorCodeEnum.getDescription() + " --- " + extraMessage);
        this.costomErrorCodeEnum = costomErrorCodeEnum;
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
