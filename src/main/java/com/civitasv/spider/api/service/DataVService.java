package com.civitasv.spider.api.service;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Deprecated
public interface DataVService {
    @GET("bound/geojson")
    Call<JsonObject> getBoundary(@Query("code") String areaCode);
}
