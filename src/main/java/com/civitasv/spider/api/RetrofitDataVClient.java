package com.civitasv.spider.api;

import com.civitasv.spider.api.service.AMapService;
import com.civitasv.spider.api.service.DataVService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit DataV API 基类
 */
public class RetrofitDataVClient {
    private static RetrofitDataVClient instance;
    private static Retrofit retrofit;
    private final static String BASE_URL = "https://geo.datav.aliyun.com/areas_v2/";

    public static synchronized RetrofitDataVClient getInstance() {
        if (instance == null)
            return new RetrofitDataVClient();
        return instance;
    }

    private RetrofitDataVClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.DAYS)
                .readTimeout(1, TimeUnit.DAYS)
                .writeTimeout(1, TimeUnit.DAYS)
                .retryOnConnectionFailure(false)
                .callTimeout(1, TimeUnit.DAYS)
                .build();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build();
    }

    public DataVService getDataVService() {
        return retrofit.create(DataVService.class);
    }
}
