package com.civitasv.spider.api;

import com.civitasv.spider.api.service.GitHubService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitGitHubClient {
    private static RetrofitGitHubClient instance;
    private static Retrofit retrofit;
    private final static String BASE_URL = "https://api.github.com/";

    public static synchronized RetrofitGitHubClient getInstance() {
        if (instance == null)
            return new RetrofitGitHubClient();
        return instance;
    }

    private RetrofitGitHubClient() {
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

    public GitHubService getGitHubService() {
        return retrofit.create(GitHubService.class);
    }
}
