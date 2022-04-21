package com.civitasv.spider.util;

import com.civitasv.spider.model.GitHubRelease;
import com.civitasv.spider.webdao.GitHubDao;
import com.civitasv.spider.webdao.impl.GitHubDaoImpl;

public class GitHubUtils {
    private final static GitHubDao gitHubDao = new GitHubDaoImpl();
    private final static String username = "Civitasv";
    private final static String repoName = "AMapPoi";

    public static GitHubRelease getGitHubReleaseLatest(){
        return gitHubDao.getReleaseInfo(username, repoName);
    }

}
