package com.civitasv.spider.service;

import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.model.po.JobPo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface JobService {
    void clearTable();
    List<JobPo> list();
    List<JobPo> listUnFinished();
    boolean saveBatch(List<JobPo> jobPos);

    boolean updateBatch(List<JobPo> jobPos);

    int updateById(JobPo jobPo);
    int count();
    int count(JobStatus taskStatus);
    int countUnFinished();
}
