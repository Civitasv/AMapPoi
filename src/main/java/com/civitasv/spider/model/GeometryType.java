package com.civitasv.spider.model;

public enum GeometryType {
    Point("Point"),
    MultiPoint("MultiPoint"),
    LineString("LineString"),
    MultiLineString("MultiLineString"),
    Polygon("Polygon"),
    MultiPolygon("MultiPolygon"),
    GeometryCollection("GeometryCollection");

    private final String val;

    GeometryType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
