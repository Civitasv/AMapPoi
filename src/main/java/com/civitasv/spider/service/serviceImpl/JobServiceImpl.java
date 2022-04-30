package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.helper.Enum.JobStatus;
import com.civitasv.spider.mapper.JobMapper;
import com.civitasv.spider.model.po.JobPo;
import com.civitasv.spider.service.JobService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public class JobServiceImpl implements JobService {
    @Override
    public void clearTable() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            jobMapper.truncate();
        }
    }

    @Override
    public List<JobPo> list() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            return jobMapper.selectList(new QueryWrapper<>());
        }
    }

    @Override
    public List<JobPo> listUnFinished() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            QueryWrapper<JobPo> wrapper = new QueryWrapper<>();
            wrapper.ne("status", JobStatus.SUCCESS.code());
            wrapper.ne("status", JobStatus.GIVE_UP.code());
            return jobMapper.selectList(wrapper);
        }
    }

    @Override
    public boolean saveBatch(List<JobPo> jobPos) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(ExecutorType.BATCH, false)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            for (int i = 0; i < jobPos.size(); i++) {
                JobPo jobPo = jobPos.get(i);
                jobMapper.insert(jobPo);
                if ((i + 1) % 1000 == 0 || i + 1 == jobPos.size()) {
                    session.flushStatements();
                }
            }
            session.commit();
            return true;
        }
    }

    @Override
    public boolean updateBatch(List<JobPo> jobPos) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(ExecutorType.BATCH, false)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            for (int i = 0; i < jobPos.size(); i++) {
                JobPo jobPo = jobPos.get(i);
                jobMapper.updateById(jobPo);
                if ((i + 1) % 1000 == 0 || i + 1 == jobPos.size()) {
                    session.flushStatements();
                }
            }
            session.commit();
            return true;
        }
    }

    @Override
    public int updateById(JobPo jobPo) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            return jobMapper.updateById(jobPo);
        }
    }

    @Override
    public int count() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            return jobMapper.selectCount(new QueryWrapper<>());
        }
    }

    @Override
    public int count(JobStatus jobStatus) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            QueryWrapper<JobPo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", jobStatus.code());
            return jobMapper.selectCount(queryWrapper);
        }
    }

    @Override
    public int countUnFinished() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            QueryWrapper<JobPo> wrapper = new QueryWrapper<>();
            wrapper.ne("status", JobStatus.SUCCESS.code());
            wrapper.ne("status", JobStatus.GIVE_UP.code());
            return jobMapper.selectCount(wrapper);
        }
    }
}
