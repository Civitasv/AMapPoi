package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.civitasv.spider.helper.Enum.BoundaryType;
import com.civitasv.spider.helper.Enum.OutputType;
import com.civitasv.spider.helper.Enum.TaskStatus;
import com.civitasv.spider.helper.Enum.UserType;
import com.civitasv.spider.model.bo.Task;
import com.civitasv.spider.util.TaskUtil;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * <p>
 * 用于构造 Task
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
@Getter
@Setter
@ToString
@Builder
@Accessors(fluent = true)
@TableName("task")
@AllArgsConstructor
public class TaskPo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("KEYS")
    private String keys;

    @TableField("TYPES")
    private String types;

    @TableField("KEYWORDS")
    private String keywords;

    @TableField("THREAD_NUMBERS")
    private Integer threadNum;

    @TableField("THRESHOLD")
    private Integer threshold;

    @TableField("USER_TYPE")
    private Integer userType;

    @TableField("OUTPUT_DIRECTORY")
    private String outputDirectory;

    @TableField("OUTPUT_TYPE")
    private Integer outputType;

    @TableField("REQUEST_ACTUAL_TIMES")
    private Integer requestActualTimes;

    @TableField("REQUEST_EXPECTED_TIMES")
    private Integer requestExpectedTimes;

    @TableField("POI_ACTUAL_COUNT")
    private Integer poiActualCount;

    @TableField("POI_EXPECTED_COUNT")
    private Integer poiExpectedCount;

    @TableField("TOTAL_EXECUTED_TIMES")
    private Integer totalExecutedTimes;

    @TableField("STATUS")
    private Integer status;

    @TableField("BOUNDARY_CONFIG")
    private String boundaryConfig;

    @TableField("BOUNDARY")
    private String bounds;

    public Task toTask() throws IOException {
        Queue<String> queue = Arrays.stream(keys.split(",")).collect(Collectors.toCollection(LinkedList::new));
        BoundaryType boundaryType = BoundaryType.getBoundaryType(boundaryConfig.split(":")[0]);
        return Task.builder().id(id)
                .aMapKeys(queue)
                .types(types)
                .keywords(keywords)
                .threadNum(threadNum)
                .threshold(threshold)
                .userType(UserType.getUserType(userType))
                .outputDirectory(outputDirectory)
                .outputType(OutputType.getOutputType(outputType))
                .requestActualTimes(requestActualTimes)
                .requestExpectedTimes(requestExpectedTimes)
                .poiActualCount(poiActualCount)
                .poiExpectedCount(poiExpectedCount)
                .totalExecutedTimes(totalExecutedTimes)
                .taskStatus(TaskStatus.getTaskStatus(status))
                .boundaryConfig(boundaryConfig)
                .boundaryType(boundaryType)
                .boundary(Arrays.stream(bounds.split(",")).map(Double::valueOf).toArray(Double[]::new))
                .filter(TaskUtil.generateFilter(boundaryConfig, boundaryType))
                .jobs(new ArrayList<>())
                .build();
    }
}
