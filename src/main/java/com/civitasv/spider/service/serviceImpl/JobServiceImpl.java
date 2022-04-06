package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.civitasv.spider.helper.TaskStatus;
import com.civitasv.spider.mapper.JobMapper;
import com.civitasv.spider.mapper.TaskMapper;
import com.civitasv.spider.model.entity.JobPo;
import com.civitasv.spider.model.entity.TaskPo;
import com.civitasv.spider.service.JobService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public class JobServiceImpl extends ServiceImpl<JobMapper, JobPo> implements JobService {

    @Override
    public TaskPo getLatestTask() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMyBatis();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            QueryWrapper<TaskPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", TaskStatus.Pause).or()
                            .eq("status", TaskStatus.Preprocessing).or()
                            .eq("status", TaskStatus.Processing);
            return taskMapper.selectOne(queryWrapper);
        }
    }
}
