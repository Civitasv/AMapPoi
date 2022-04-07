package com.civitasv.spider.helper;

public enum BoundaryType {
    ADCODE(0,"行政区"),
    RECTANGLE(1,"矩形"),
    CUSTOM(2,"自定义");

    private final String description;
    private final Integer code;

    BoundaryType(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static BoundaryType getBoundryType(String description){
        for (BoundaryType value : BoundaryType.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static BoundaryType getBoundryType(Integer code){
        for (BoundaryType value : BoundaryType.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
