package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.civitasv.spider.mapper.JobMapper;
import com.civitasv.spider.model.po.JobPo;
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
    public void clearTable() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            JobMapper jobMapper = session.getMapper(JobMapper.class);
            jobMapper.truncate();
        }
    }
}
