package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.Enum.CustomErrorCodeEnum;
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
    public Integer requestActualTimes = 0;
    public Integer requestExceptedTimes = 1;
    public Integer poiActualSum = 0;
    public Integer poiExceptedSum = 0;
    public POI poi;
    public CustomErrorCodeEnum errorCodeEnum;

    public Job(Long id, Long taskid, Double[] bounds, String types, String keywords, Integer page, Integer size) {
        this.id = id;
        this.taskid = taskid;
        this.bounds = bounds;
        this.types = types;
        this.keywords = keywords;
        this.page = page;
        this.size = size;
    }

    public Job(Long id, Long taskid, Double[] bounds, String types, String keywords, Integer page, Integer size, JobStatus jobStatus, Integer requestActualTimes, Integer requestExceptedTimes, Integer poiActualSum, Integer poiExceptedSum, POI poi) {
        this.id = id;
        this.taskid = taskid;
        this.bounds = bounds;
        this.types = types;
        this.keywords = keywords;
        this.page = page;
        this.size = size;
        this.jobStatus = jobStatus;
        this.requestActualTimes = requestActualTimes;
        this.requestExceptedTimes = requestExceptedTimes;
        this.poiActualSum = poiActualSum;
        this.poiExceptedSum = poiExceptedSum;
        this.poi = poi;
    }

    public JobPo toJobPo(){
        try{
            return new JobPo(id, taskid, types,
                    keywords,page, size, jobStatus.getCode(),requestActualTimes, requestExceptedTimes, poiActualSum, poiExceptedSum
                    , Arrays.stream(bounds).map(Object::toString).collect(Collectors.joining(",")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int plusRequestActualTimes(){
        return ++requestActualTimes;
    }

    public int plusPoiActualSum(int plusPoiNum){
        return poiActualSum += plusPoiNum;
    }
}
