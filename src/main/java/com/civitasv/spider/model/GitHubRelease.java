package com.civitasv.spider.model;

public class GitHubRelease {
    private final String html_url;
    private final String tag_name;
    private final String name;
    private final String body;

    public GitHubRelease(String html_url, String tag_name, String name, String body) {
        this.html_url = html_url;
        this.tag_name = tag_name;
        this.name = name;
        this.body = body;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getTag_name() {
        return tag_name;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }
}
