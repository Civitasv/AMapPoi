package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.model.po.JobPo;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    public Integer totalExecutedTime;
    public POI poi;

    public Job(Integer id, Task task, Double[] bounds, String types, String keywords, Integer page, Integer size) {
        this.id = id;
        this.task = task;
        this.bounds = bounds;
        this.types = types;
        this.keywords = keywords;
        this.page = page;
        this.size = size;
    }

    public JobPo toJobPo(){
        return new JobPo(id, task.id, String.join(",", task.aMapKeys), types,
                keywords,page, size, jobStatus.getCode(), requestExceptedTimes, requestActualTimes,poiExceptedSum,
                poiActualSSum, totalExecutedTime, Arrays.stream(bounds).map(Object::toString).collect(Collectors.joining(",")));
    }
}
