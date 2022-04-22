package com.civitasv.spider.helper.Enum;

public enum TaskStatus {
    UnStarted(0, "未开始"),
    Preprocessing(1, "预处理"),
    Processing(2, "处理中"),
    Pause(3, "暂停"),
    Success(4, "成功"),
    Some_Failed(5, "部分任务失败，可以重试"),
    Give_Up(6, "用户确认放弃任务，导出错误文件");

    private final String description;
    private final Integer code;

    TaskStatus(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static TaskStatus getBoundryType(String description){
        for (TaskStatus value : TaskStatus.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static TaskStatus getBoundryType(Integer code){
        for (TaskStatus value : TaskStatus.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
