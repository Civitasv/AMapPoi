package com.civitasv.spider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.civitasv.spider.model.po.PoiPo;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface PoiMapper extends BaseMapper<PoiPo> {

    @Update("TRUNCATE TABLE tmp_truncate_table")
    void truncate();
}
