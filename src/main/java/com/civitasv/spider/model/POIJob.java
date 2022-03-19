package com.civitasv.spider.model;

public class POIJob {
    public POIJob(String polygon, String keywords, String types, int page, int size) {
        this.polygon = polygon;
        this.keywords = keywords;
        this.types = types;
        this.page = page;
        this.size = size;
    }

    public String polygon;
    public String keywords;
    public String types;
    public int page;
    public int size;
}
