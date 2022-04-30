package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public enum CoordinateType {
    BD09(0, "bd09"),
    GCJ02(1, "gcj02"),
    WGS84(2, "wgs84");

    private final Integer code;
    private final String description;

    public static CoordinateType getCoordinateType(String description) {
        for (CoordinateType value : CoordinateType.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static CoordinateType getCoordinateType(Integer code) {
        for (CoordinateType value : CoordinateType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
