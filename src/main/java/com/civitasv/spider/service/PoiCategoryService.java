package com.civitasv.spider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.civitasv.spider.model.po.PoiCategory;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-20 05:56:08
 */
public interface PoiCategoryService extends IService<PoiCategory> {
    List<String> getPoiCategory1();

    List<String> getPoiCategory2(String cate1);

    List<String> getPoiCategory3(String cate1, String cate2);

    String getPoiCategoryId(String cate1, String cate2, String cate3);
}
