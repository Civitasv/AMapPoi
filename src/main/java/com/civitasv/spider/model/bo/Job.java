package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.JobStatus;

import java.time.Duration;

public class Job {
    public Integer id;
    public Task task;
    public Double[] bounds;
    public String types;
    public String keywords;
    public Integer page;
    public Integer size;
    public JobStatus jobStatus = JobStatus.UnStarted;
    public Integer requestActualTimes;
    public Integer requestExceptedTimes;
    public Integer poiActualSSum;
    public Integer poiExceptedSum;
    public Duration totalExecutedTime;
    public POI poiResponse;

    public Job(Integer id, Task task, Double[] bounds, String types, String keywords, Integer page, Integer size) {
        this.id = id;
        this.task = task;
        this.bounds = bounds;
        this.types = types;
        this.keywords = keywords;
        this.page = page;
        this.size = size;
    }
}
