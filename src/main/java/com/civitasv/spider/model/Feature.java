package com.civitasv.spider.model;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Feature {
    private final String type;
    private final String geometry;
    private final Map<String, String> properties;

    public Feature(String type, String geometry, Map<String, String> properties) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

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
