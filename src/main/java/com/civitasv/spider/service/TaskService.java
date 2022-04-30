package com.civitasv.spider.service;

import com.civitasv.spider.model.bo.Task;
import com.civitasv.spider.model.po.TaskPo;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface TaskService {
    Task getUnFinishedTask() throws IOException;

    List<TaskPo> list();

    int save(TaskPo taskPo);

    int updateById(TaskPo taskPo);
}
