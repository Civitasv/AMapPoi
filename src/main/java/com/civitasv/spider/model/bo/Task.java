package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.Enum.*;
import com.civitasv.spider.model.po.TaskPo;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
@Builder
public class Task {
    private Long id;
    private Queue<String> aMapKeys;
    private String types;
    private String keywords;
    private Integer threadNum;
    private Integer threshold;
    private UserType userType;
    private String outputDirectory;
    private OutputType outputType;
    private Integer requestActualTimes;
    private Integer requestExpectedTimes;
    private Integer poiActualCount;
    private Integer poiExpectedCount;
    private Integer totalExecutedTimes;
    private TaskStatus taskStatus;
    private BoundaryType boundaryType;
    private String boundaryConfig;
    private Double[] boundary;
    private Predicate<? super POI.Info> filter;
    private List<Job> jobs;

    public TaskPo toTaskPo() {
        return new TaskPo(
                id,
                String.join(",", aMapKeys),
                types,
                keywords,
                threadNum,
                threshold,
                userType.code(),
                outputDirectory,
                outputType.code(),
                requestActualTimes,
                requestExpectedTimes,
                poiActualCount,
                poiExpectedCount,
                totalExecutedTimes,
                taskStatus.code(),
                boundaryConfig,
                Arrays.stream(boundary).map(Object::toString).collect(Collectors.joining(",")));
    }

    public void plusRequestExceptedTimes(int plusRequestNum) {
        requestExpectedTimes += plusRequestNum;
    }

    public void plusRequestActualTimes() {
        ++requestActualTimes;
    }

    public void plusPoiExceptedSum(int plusPoiNum) {
        poiExpectedCount += plusPoiNum;
    }

    public void plusPoiActualSum(int plusPoiNum) {
        poiActualCount += plusPoiNum;
    }

    public void plusTotalExecutedTime(int plusExecutedTime) {
        totalExecutedTimes += plusExecutedTime;
    }
}
