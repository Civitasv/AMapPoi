package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.model.bo.Job;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>
 * 读取数据库表 Job 信息，主要用于构造 {@link Job}
 * </p>
 *
 * @author zhanghang
 * @see Job
 * @since 2022-04-06 09:08:52
 */
@Getter
@Setter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
@TableName("job")
public class JobPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一 ID，自增主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private final Long jobId;

    /**
     * 对应 Task ID
     */
    @TableField("TASK_ID")
    private final Long taskId;

    /**
     * POI 类型
     */
    @TableField("TYPES")
    private final String types;

    /**
     * POI 关键字
     */
    @TableField("KEYWORDS")
    private final String keywords;

    @TableField("PAGE")
    private final Integer page;

    @TableField("SIZE")
    private final Integer size;

    /**
     * @see JobStatus
     */
    @TableField("STATUS")
    private final Integer status;

    /**
     * Job 实际请求次数
     */
    @TableField("REQUEST_ACTUAL_TIMES")
    private final Integer requestActualTimes;

    /**
     * Job 期望请求次数
     * <p>
     * 1 normally
     */
    @TableField("REQUEST_EXPECTED_TIMES")
    private final Integer requestExpectedTimes;

    /**
     * POI 爬取时，该 Job 实际获得的数量
     */
    @TableField("POI_ACTUAL_COUNT")
    private final Integer poiActualCount;

    /**
     * 该 Job 期望获得的数量
     * <p>
     * 用于检查是否与 {@link #poiActualCount} 匹配
     * <p>
     * Debug Use
     */
    @TableField("POI_EXPECTED_COUNT")
    private final Integer poiExpectedCount;

    /**
     * 左上（经纬度），右下（经纬度）
     * <p>
     * 例如：110.360476,31.383448,116.65032,36.365931
     */
    @TableField("BOUNDARY")
    private final String boundary;

    public Job toJob() {
        Double[] bounds = Arrays.stream(boundary.split(","))
                .map(Double::valueOf)
                .toArray(Double[]::new);
        return new Job(
                jobId,
                null,
                bounds,
                types,
                keywords,
                page,
                size)
                .jobStatus(JobStatus.getJobStatus(status))
                .requestActualTimes(requestActualTimes)
                .requestExpectedTimes(requestExpectedTimes)
                .poiActualCount(poiActualCount)
                .poiExpectedCount(poiExpectedCount)
                .poi(null);
    }
}
