package com.civitasv.spider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.civitasv.spider.model.po.JobPo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface JobService extends IService<JobPo> {
    void clearTable();
}
