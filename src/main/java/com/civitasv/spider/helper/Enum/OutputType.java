package com.civitasv.spider.helper.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum OutputType {
    CSV(0, "csv"),
    SHAPEFILE(1, "shp"),
    GEOJSON(2, "geojson"),
    TXT(3, "txt");

    private final Integer code;
    private final String description;

    public static OutputType getOutputType(String description) {
        for (OutputType value : OutputType.values()) {
            if (value.description.equals(description)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static OutputType getOutputType(Integer code) {
        for (OutputType value : OutputType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    @Override
    public String toString() {
        return description;
    }
}
