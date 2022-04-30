package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum JobStatus {
    UnStarted(0, "未开始"),
    SUCCESS(1, "成功"),
    Failed(2, "失败，但会尝试重新请求数据"),
    GIVE_UP(3, "放弃，不再尝试请求数据");

    private final Integer code;
    private final String description;

    public static JobStatus getJobStatus(String description) {
        for (JobStatus value : JobStatus.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static JobStatus getJobStatus(Integer code) {
        for (JobStatus value : JobStatus.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
