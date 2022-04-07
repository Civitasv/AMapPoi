package com.civitasv.spider.service;

import com.civitasv.spider.model.bo.Task;
import com.civitasv.spider.model.po.TaskPo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface TaskService extends IService<TaskPo> {

    public Task getUnFinishedTask();
}
