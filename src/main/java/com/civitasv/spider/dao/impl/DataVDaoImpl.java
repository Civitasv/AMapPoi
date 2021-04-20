package com.civitasv.spider.dao.impl;

import com.civitasv.spider.api.RetrofitAMapClient;
import com.civitasv.spider.api.RetrofitDataVClient;
import com.civitasv.spider.dao.DataVDao;
import com.civitasv.spider.model.Geocodes;
import com.google.gson.JsonObject;
import retrofit2.Call;

import java.io.IOException;

public class DataVDaoImpl implements DataVDao {
    @Override
    public JsonObject getBoundary(String areaCode) {
        Call<JsonObject> call = RetrofitDataVClient.getInstance().getDataVService().getBoundary(areaCode);
        try {
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }
}
