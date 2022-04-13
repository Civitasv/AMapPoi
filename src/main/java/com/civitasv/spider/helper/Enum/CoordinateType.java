package com.civitasv.spider.helper.Enum;

public enum CoordinateType {
    BD09(0,"bd09"),
    GCJ02(1,"gcj02"),
    WGS84(2,"wgs84");
    private final String description;
    private final Integer code;

    CoordinateType(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static CoordinateType getBoundryType(String description){
        for (CoordinateType value : CoordinateType.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }

    public static CoordinateType getBoundryType(Integer code){
        for (CoordinateType value : CoordinateType.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}
