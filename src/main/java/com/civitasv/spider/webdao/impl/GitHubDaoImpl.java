package com.civitasv.spider.webdao.impl;

import com.civitasv.spider.api.RetrofitGitHubClient;
import com.civitasv.spider.model.GitHubRelease;
import com.civitasv.spider.webdao.GitHubDao;
import retrofit2.Call;

import java.io.IOException;

public class GitHubDaoImpl implements GitHubDao {
    @Override
    public GitHubRelease getReleaseInfo(String username, String repoName) {
        Call<GitHubRelease> call = RetrofitGitHubClient.getInstance().getGitHubService().getReleaseInfo(username, repoName);
        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
