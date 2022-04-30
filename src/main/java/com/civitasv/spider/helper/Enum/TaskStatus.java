package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public enum TaskStatus {
    UnStarted(0, "未开始"),
    Preprocessing(1, "预处理"),
    Processing(2, "处理中"),
    Pause(3, "暂停"),
    Success(4, "成功"),
    Some_Failed(5, "部分任务失败，可以重试"),
    Give_Up(6, "用户确认放弃任务，导出错误文件");

    private final Integer code;
    private final String description;

    public static TaskStatus getTaskStatus(String description) {
        for (TaskStatus value : TaskStatus.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static TaskStatus getTaskStatus(Integer code) {
        for (TaskStatus value : TaskStatus.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
