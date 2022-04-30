package com.civitasv.spider.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public class GitHubRelease {
    @SerializedName("html_url")
    private final String htmlUrl;
    @SerializedName("tag_name")
    private final String tagName;
    private final String name;
    private final String body;
}
