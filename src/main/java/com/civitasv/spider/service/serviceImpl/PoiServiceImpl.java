package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.civitasv.spider.mapper.PoiMapper;
import com.civitasv.spider.model.po.PoiPo;
import com.civitasv.spider.service.PoiService;
import com.civitasv.spider.util.MyBatisUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public class PoiServiceImpl implements PoiService {
    @Override
    public void clearTable() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiMapper poiMapper = session.getMapper(PoiMapper.class);
            poiMapper.truncate();
        }
    }

    @Override
    public List<PoiPo> list() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiMapper poiMapper = session.getMapper(PoiMapper.class);
            return poiMapper.selectList(new QueryWrapper<>());
        }
    }

    @Override
    public boolean saveBatch(List<PoiPo> poiPos) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(ExecutorType.BATCH,true)) {
            PoiMapper poiMapper = session.getMapper(PoiMapper.class);
            for (int i = 0; i < poiPos.size(); i++) {
                PoiPo poiPo = poiPos.get(i);
                poiMapper.insert(poiPo);
                if ((i + 1) % 1000 == 0 || i + 1 == poiPos.size()) {
                    session.flushStatements();
                }
            }
            return true;
        }
    }

    @Override
    public int updateById(PoiPo poiPo) {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiMapper poiMapper = session.getMapper(PoiMapper.class);
            return poiMapper.updateById(poiPo);
        }
    }
}
