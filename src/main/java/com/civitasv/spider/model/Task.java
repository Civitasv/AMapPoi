package com.civitasv.spider.model;

import com.civitasv.spider.helper.OutputType;

import java.time.Duration;
import java.util.Queue;

public class Task {
    public Integer id;
    public Queue<String> aMapKeys;
    public String types;
    public String keywords;
    public Integer threadNum;
    public Integer threshold;
    public String outputDirectory;
    public OutputType outputType;
    public Double[] boundary;
    public Integer requestActualTimes;
    public Integer requestExceptedTimes;
    public Integer poiActualSum;
    public Integer poiExecutedSum;
    public Duration totalExecutedTime;

    public Task(Integer id, Queue<String> aMapKeys, String types, String keywords, Integer threadNum, Integer threshold, String outputDirectory, OutputType outputType, Double[] boundary, Integer requestActualTimes, Integer requestExceptedTimes, Integer poiActualSum, Integer poiExecutedSum, Duration totalExecutedTime) {
        this.id = id;
        this.aMapKeys = aMapKeys;
        this.types = types;
        this.keywords = keywords;
        this.threadNum = threadNum;
        this.threshold = threshold;
        this.outputDirectory = outputDirectory;
        this.outputType = outputType;
        this.boundary = boundary;
        this.requestActualTimes = requestActualTimes;
        this.requestExceptedTimes = requestExceptedTimes;
        this.poiActualSum = poiActualSum;
        this.poiExecutedSum = poiExecutedSum;
        this.totalExecutedTime = totalExecutedTime;
    }
}
