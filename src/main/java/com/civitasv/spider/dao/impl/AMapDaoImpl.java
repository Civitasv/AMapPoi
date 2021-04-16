package com.civitasv.spider.dao.impl;

import com.civitasv.spider.api.AMapKeys;
import com.civitasv.spider.api.RetrofitClient;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.model.Geocodes;
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
        Call<Geocodes.Response> call = RetrofitClient.getInstance().getAMapService().geocoding(key, address, city);
        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
