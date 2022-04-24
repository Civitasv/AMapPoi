package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.civitasv.spider.mapper.PoiCategoryMapper;
import com.civitasv.spider.model.po.PoiCategory;
import com.civitasv.spider.service.PoiCategoryService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-20 05:56:08
 */
public class PoiCategoryServiceImpl extends ServiceImpl<PoiCategoryMapper, PoiCategory> implements PoiCategoryService {

    @Override
    public List<String> getPoiCategory1() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select(" DISTINCT substr(CATE_ID, 1,2) as CATE_ID, CATE1 ");
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.getCate1() + "(" + poiCategory.getCateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getPoiCategory2(String cate1) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("DISTINCT substr(CATE_ID, 1,4) as CATE_ID, CATE2")
                    .eq("CATE1", cate1);
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.getCate2() + "(" + poiCategory.getCateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getPoiCategory3(String cate1, String cate2) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("DISTINCT CATE3, CATE_ID")
                    .eq("CATE1", cate1)
                    .eq("CATE2", cate2);
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.getCate3() + "(" + poiCategory.getCateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public String getPoiCategoryId(String cate1, String cate2, String cate3) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("CATE_ID")
                    .eq("CATE1", cate1)
                    .eq("CATE2", cate2)
                    .eq("CATE3", cate3);
            return poiCategoryMapper.selectOne(wrapper).getCateId();
        }
    }
}
