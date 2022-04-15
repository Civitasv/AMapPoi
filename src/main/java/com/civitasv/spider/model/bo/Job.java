package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.model.po.JobPo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Job {
    public Long id;
    public Long taskid;
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

    public Job(Long id, Long taskid, Double[] bounds, String types, String keywords, Integer page, Integer size) {
        this.id = id;
        this.taskid = taskid;
        this.bounds = bounds;
        this.types = types;
        this.keywords = keywords;
        this.page = page;
        this.size = size;
    }

    public JobPo toJobPo(){
        try{
            return new JobPo(id, taskid, types,
                    keywords,page, size, jobStatus.getCode(), requestExceptedTimes, requestActualTimes,poiExceptedSum,
                    poiActualSSum, totalExecutedTime, Arrays.stream(bounds).map(Object::toString).collect(Collectors.joining(",")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
