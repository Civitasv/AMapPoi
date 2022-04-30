package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.mapper.CityCodeMapper;
import com.civitasv.spider.model.po.City;
import com.civitasv.spider.service.CityCodeService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-22 09:03:30
 */
public class CityCodeServiceImpl implements CityCodeService {
    @Override
    public List<City> listByCityId(String cityId) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            CityCodeMapper jobMapper = session.getMapper(CityCodeMapper.class);
            QueryWrapper<City> wrapper = new QueryWrapper<>();
            wrapper.eq("PARENT_ID", cityId);
            return jobMapper.selectList(wrapper);
        }
    }
}
