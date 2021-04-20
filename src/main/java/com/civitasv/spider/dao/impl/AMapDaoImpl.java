package com.civitasv.spider.dao.impl;

import com.civitasv.spider.api.AMapKeys;
import com.civitasv.spider.api.RetrofitAMapClient;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.POI;
import retrofit2.Call;

import java.io.IOException;

public class AMapDaoImpl implements AMapDao {
    @Override
    public Geocodes.Response geocoding(Geocodes.Request request) {
        return geocoding(request.getAddress(), request.getCity());
    }

    @Override
    public Geocodes.Response geocoding(String address, String city) {
        return geocoding(AMapKeys.getAmapKeys().get(0), address, city);
    }

    @Override
    public Geocodes.Response geocoding(String key, String address, String city) {
        Call<Geocodes.Response> call = RetrofitAMapClient.getInstance().getAMapService().geocoding(key, address, city);
        try {
            System.out.println(call.request());
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public POI getPoi(String key, String polygon, String keywords, String types, int page, int size) {
        Call<POI> call = RetrofitAMapClient.getInstance().getAMapService().getPoi(key, polygon, keywords, types, page, size);
        try {
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }
}
