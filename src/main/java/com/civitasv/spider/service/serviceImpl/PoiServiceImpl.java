package com.civitasv.spider.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.civitasv.spider.mapper.PoiMapper;
import com.civitasv.spider.model.po.PoiPo;
import com.civitasv.spider.service.PoiService;
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
public class PoiServiceImpl extends ServiceImpl<PoiMapper, PoiPo> implements PoiService {
    @Override
    public void clearTable() {
        SqlSessionFactory defaultMyBatis = MyBatisUtils.getDefaultMybatisPlus();
        try (SqlSession session = defaultMyBatis.openSession(true)) {
            PoiMapper poiMapper = session.getMapper(PoiMapper.class);
            poiMapper.truncate();
        }
    }
}
