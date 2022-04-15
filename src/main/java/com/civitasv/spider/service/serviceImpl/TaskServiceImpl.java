package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.helper.Enum.TaskStatus;
import com.civitasv.spider.mapper.TaskMapper;
import com.civitasv.spider.model.bo.Job;
import com.civitasv.spider.model.bo.Task;
import com.civitasv.spider.model.po.TaskPo;
import com.civitasv.spider.service.JobService;
import com.civitasv.spider.service.PoiService;
import com.civitasv.spider.service.TaskService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public class TaskServiceImpl implements TaskService {

    private final JobService jobService = new JobServiceImpl();
    private final PoiService poiService = new PoiServiceImpl();

    @Override
    public Task getUnFinishedTask() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);

            QueryWrapper<TaskPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", TaskStatus.Pause).or()
                    .eq("status", TaskStatus.Preprocessing).or()
                    .eq("status", TaskStatus.Processing);
            TaskPo taskPo = taskMapper.selectOne(queryWrapper);
            if(taskPo == null){
                return null;
            }
            Task task = taskPo.toTask();

            task.jobs = jobService.list().stream().map(jobPo -> {
                Job job = jobPo.toJob();
                job.taskid = task.id;
                return job;
            }).collect(Collectors.toList());
            return task;
        }
    }

    @Override
    public List<TaskPo> list() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            return taskMapper.selectList(new QueryWrapper<>());
        }
    }

    @Override
    public int save(TaskPo taskPo) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            return taskMapper.insert(taskPo);
        }
    }

    @Override
    public int updateById(TaskPo taskPo) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            TaskMapper taskMapper = session.getMapper(TaskMapper.class);
            return taskMapper.updateById(taskPo);
        }
    }
}
