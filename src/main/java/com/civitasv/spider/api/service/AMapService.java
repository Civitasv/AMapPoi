package com.civitasv.spider.api.service;

import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.bo.POI;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 高德service
 */
public interface AMapService {
    @GET("geocode/geo")
    Call<Geocodes.Response> geocoding(@Query("key") String key, @Query("address") String address, @Query("city") String city);

    @GET("place/polygon")
    Call<POI> getPoi(@Query("key") String key,
                     @Query("polygon") String polygon,
                     @Query("keywords") String keywords,
                     @Query("types") String types,
                     @Query("extensions") String extensions,
                     @Query("page") int page,
                     @Query("offset") int size);
}