package com.civitasv.spider.webdao;

import com.civitasv.spider.model.GitHubRelease;

public interface GitHubDao {
    GitHubRelease getReleaseInfo(String username, String repoName);
}
