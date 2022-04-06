package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.JobStatus;

import java.time.Duration;

public class Job {
    public Integer id;
    public Task task;
    double[] bounds;
    public String keywords;
    public String words;
    public Integer page;
    public Integer size;
    public JobStatus jobStatus = JobStatus.UnStarted;
    public Integer requestActualTimes;
    public Integer requestExceptedTimes;
    public Integer poiActualSSum;
    public Integer poiExceptedSum;
    public Duration totalExecutedTime;

    public Job(Integer id, Task task, double[] bounds, String keywords, String words, Integer page, Integer size) {
        this.id = id;
        this.task = task;
        this.bounds = bounds;
        this.keywords = keywords;
        this.words = words;
        this.page = page;
        this.size = size;
    }
}
