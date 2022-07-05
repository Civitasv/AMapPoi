package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum BoundaryType {
    ADCODE(0, "行政区"),
    RECTANGLE(1, "矩形"),
    CUSTOM(2, "自定义");

    private final Integer code;
    private final String description;

    public static BoundaryType getBoundaryType(String description) {
        for (BoundaryType value : BoundaryType.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static BoundaryType getBoundaryType(Integer code) {
        for (BoundaryType value : BoundaryType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
