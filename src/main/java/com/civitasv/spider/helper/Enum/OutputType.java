package com.civitasv.spider.helper.Enum;

public enum OutputType {
    CSV(0, "csv"),
    SHAPEFILE(1, "shp"),
    GEOJSON(2, "geojson"),
    TXT(3, "txt");

    private final String description;
    private final Integer code;

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    OutputType(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

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
}
