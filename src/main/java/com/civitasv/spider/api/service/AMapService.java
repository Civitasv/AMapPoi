package com.civitasv.spider.api.service;

import com.civitasv.spider.model.Geocodes;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

/**
 * 高德service
 */
public interface AMapService {
    @GET("geocode/geo")
    Call<Geocodes.Response> geocoding(@Query("key") String key, @Query("address") String address, @Query("city") String city);
}