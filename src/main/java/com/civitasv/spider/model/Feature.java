package com.civitasv.spider.model;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * GeoJSON 下的 Feature，格式：
 *
 * <pre>
 * "features": [
 *     {
 *       "type": "Feature",
 *       "properties": {},
 *       "geometry": {
 *         "type": "LineString",
 *         "coordinates": [
 *           [
 *             19.6875,
 *             33.7243396617476
 *           ],
 *           [
 *             15.468749999999998,
 *             -7.362466865535738
 *           ]
 *         ]
 *       }
 *     }
 *   ]
 * </pre>
 */
public class Feature {
    private final String type;
    private final String geometry;
    private final Map<String, String> properties;

    public Feature(String geometry) {
        this.type = "Feature";
        this.geometry = geometry;
        this.properties = new HashMap<>();
    }

    public Feature(String geometry, Map<String, String> properties) {
        this.type = "Feature";
        this.geometry = geometry;
        this.properties = properties;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return "{" +
                "\"type\":\"" + type + "\"" +
                ", \"geometry\":" + geometry +
                ", \"properties\":" + gson.toJson(properties) +
                "}";
    }
}
