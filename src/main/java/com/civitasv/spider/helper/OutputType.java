package com.civitasv.spider.helper;

public enum OutputType {
    CSV("csv"),
    SHAPEFILE("shapefile"),
    GEOJSON("geojson"),
    TXT("txt");

    private final String description;

    OutputType(String description) {
        this.description = description;
    }

    public static OutputType getOutputType(String description){
        for (OutputType value : OutputType.values()) {
            if(value.description.equals(description)){
                return value;
            }
        }
        throw new IllegalArgumentException("没有符合该描述的枚举值");
    }
}