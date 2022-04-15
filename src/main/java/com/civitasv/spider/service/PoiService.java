package com.civitasv.spider.service;

import com.civitasv.spider.model.po.PoiPo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
public interface PoiService {
    void clearTable();
    List<PoiPo> list();
    boolean saveBatch(List<PoiPo> poiPos);
    int updateById(PoiPo poiPo);
}
