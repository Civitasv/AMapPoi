package com.civitasv.spider.dao;

import com.google.gson.JsonObject;

public interface DataVDao {
    JsonObject getBoundary(String areaCode);
}
