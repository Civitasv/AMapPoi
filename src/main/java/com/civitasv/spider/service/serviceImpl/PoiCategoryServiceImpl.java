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
    public List<String> getPoiCategoryBig() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select(" DISTINCT substr(CATE_ID, 1,2) as CATE_ID, BIG ");
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.big() + "(" + poiCategory.cateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getPoiCategoryMid(String big) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("DISTINCT substr(CATE_ID, 1,4) as CATE_ID, MID")
                    .eq("BIG", big);
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.mid() + "(" + poiCategory.cateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getPoiCategorySub(String big, String mid) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("DISTINCT SUB, CATE_ID")
                    .eq("BIG", big)
                    .eq("MID", mid);
            return poiCategoryMapper.selectList(wrapper).stream().map(poiCategory -> poiCategory.sub() + "(" + poiCategory.cateId() + ")").collect(Collectors.toList());
        }
    }

    @Override
    public String getPoiCategoryId(String big, String mid, String sub) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiCategoryMapper poiCategoryMapper = session.getMapper(PoiCategoryMapper.class);
            QueryWrapper<PoiCategory> wrapper = new QueryWrapper<>();
            wrapper.select("CATE_ID")
                    .eq("BIG", big)
                    .eq("MID", mid)
                    .eq("SUB", sub);
            return poiCategoryMapper.selectOne(wrapper).cateId();
        }
    }
}
