package com.civitasv.spider.helper.Enum;

public enum JobStatus {
    UnStarted(0, "未开始"),
    Success(1, "成功"),
    Failed(2, "失败");

    private final String description;
    private final Integer code;

    JobStatus(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static JobStatus getUserType(String description){
        for (JobStatus value : JobStatus.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static JobStatus getUserType(Integer code){
        for (JobStatus value : JobStatus.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
