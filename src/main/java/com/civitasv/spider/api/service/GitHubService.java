package com.civitasv.spider.api.service;

import com.civitasv.spider.model.GitHubRelease;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubService {
    @GET("repos/{username}/{repoName}/releases/latest")
    Call<GitHubRelease>
    getReleaseInfo(@Path("username") String username,
                   @Path("repoName") String repoName);
}
