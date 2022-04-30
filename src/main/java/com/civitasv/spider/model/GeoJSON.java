package com.civitasv.spider.model;

import java.util.List;

/**
 * GeoJSON 数据
 */
public class GeoJSON {
    private final String type;
    private final List<Feature> features;

    public GeoJSON(List<Feature> features) {
        this.type = "FeatureCollection";
        this.features = features;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type + "\"" +
                ", \"features\":" + features +
                '}';
    }
}
