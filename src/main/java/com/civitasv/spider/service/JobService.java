package com.civitasv.spider.service;

import com.civitasv.spider.model.entity.JobPo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.civitasv.spider.model.entity.TaskPo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface JobService extends IService<JobPo> {
    TaskPo getLatestTask();


}
