package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.helper.Enum.NoTryAgainErrorCode;
import com.civitasv.spider.helper.Enum.TryAgainErrorCode;
import com.civitasv.spider.model.po.JobPo;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 获取 {@link Task} 中分配的具体 Job 信息
 * <p>
 * 可以后续转换为 {@link JobPo}，以存储至数据库
 */
@Getter
@Setter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public class Job {
    private final Long jobId;
    private final Long taskId;
    private final Double[] bounds;
    private final String types;
    private final String keywords;
    private final Integer page;
    private final Integer size;
    private JobStatus jobStatus = JobStatus.UnStarted;
    private Integer requestActualTimes = 0;
    private Integer requestExpectedTimes = 1;
    private Integer poiActualCount = 0;
    private Integer poiExpectedCount = 0;
    private POI poi;
    private TryAgainErrorCode tryAgainErrorCode;
    private NoTryAgainErrorCode noTryAgainErrorCode;

    public JobPo toJobPo() {
        return new JobPo(
                jobId,
                taskId,
                types,
                keywords,
                page,
                size,
                jobStatus.code(),
                requestActualTimes,
                requestExpectedTimes,
                poiActualCount,
                poiExpectedCount,
                Arrays.stream(bounds).map(Object::toString).collect(Collectors.joining(",")));
    }

    public void plusRequestActualTimes() {
        ++requestActualTimes;
    }

    public void plusToPoiActualCount(int poiCount) {
        poiActualCount += poiCount;
    }
}
