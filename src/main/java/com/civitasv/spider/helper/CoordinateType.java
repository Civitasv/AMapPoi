package com.civitasv.spider.helper;

public enum CoordinateType {
    BD09("bd09"), GCJ02("gcj02"), WGS84("wgs84");
    public String val;

    CoordinateType(String val) {
        this.val = val;
    }
}
