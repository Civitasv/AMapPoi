package com.civitasv.spider.helper.Enum;

public enum TryAgainErrorCode {

    // 自定义Error
    RETURN_NULL_DATA(80001, "网络请求返回为null", "请求参数错误"),
    TIME_OUT(80002, "请求超时", "请重新尝试"),
    TRY_OTHER_KEY(80003, "其中一个key无效", "重新尝试其他key"),

    CQPS_HAS_EXCEEDED_THE_LIMIT(10019, "使用的某个服务总QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    CKQPS_HAS_EXCEEDED_THE_LIMIT(10020, "某个Key使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    CUQPS_HAS_EXCEEDED_THE_LIMIT(10021, "账号使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    CGQPS_HAS_EXCEEDED_THE_LIMIT(10022, "QPS超限", "请减少并发数"),
    QPS_HAS_EXCEEDED_THE_LIMIT(10014, "云图服务QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),
    GATEWAY_TIMEOUT(10015, "受单机QPS限流限制", "受单机QPS限流限制时出现该问题，建议降低请求的QPS或在控制台提工单联系我们");


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

    public static TryAgainErrorCode getError(String description) {
        for (TryAgainErrorCode value : TryAgainErrorCode.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        return null;
    }

    public static TryAgainErrorCode getError(Integer code) {
        for (TryAgainErrorCode value : TryAgainErrorCode.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
