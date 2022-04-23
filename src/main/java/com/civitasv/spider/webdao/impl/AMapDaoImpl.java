package com.civitasv.spider.webdao.impl;

import com.civitasv.spider.api.RetrofitAMapClient;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.bo.POI;
import com.civitasv.spider.webdao.AMapDao;
import retrofit2.Call;

import java.io.IOException;

public class AMapDaoImpl implements AMapDao {
    @Override
    public Geocodes.Response geocoding(String key, String address, String city) {
        Call<Geocodes.Response> call = RetrofitAMapClient.getInstance().getAMapService().geocoding(key, address, city);
        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public POI getPoi(String key, String polygon, String keywords, String types, String extensions, int page, int size) {
        Call<POI> call = RetrofitAMapClient.getInstance().getAMapService().getPoi(key, polygon, keywords, types, extensions, page, size);
        try {
            return call.execute().body();
        } catch (IOException e) {
//            e.printStackTrace();
            return null;
        }
    }
}
