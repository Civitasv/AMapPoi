package com.civitasv.spider.helper.Enum;

public enum TryAgainErrorCode {

    // 自定义Error
    RETURN_NULL_DATA(90002,"网络请求返回为null","请求参数错误"),
    TIME_OUT(90004,"请求超时","请重新尝试"),

    CGQPS_HAS_EXCEEDED_THE_LIMIT(10022, "QPS超限", "请减少并发数");


    private final Integer code;
    private final String description;
    private final String helpinfo;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getHelpinfo() {
        return helpinfo;
    }

    TryAgainErrorCode(Integer code, String description, String helpinfo) {
        this.code = code;
        this.description = description;
        this.helpinfo = helpinfo;
    }

    public static TryAgainErrorCode getError(String description){
        for (TryAgainErrorCode value : TryAgainErrorCode.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        return null;
    }

    public static TryAgainErrorCode getError(Integer code){
        for (TryAgainErrorCode value : TryAgainErrorCode.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
