package com.civitasv.spider.webdao;

import com.google.gson.JsonObject;

public interface DataVDao {
    JsonObject getBoundary(String areaCode);
}
