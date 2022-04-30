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
    List<String> getPoiCategoryBig();

    List<String> getPoiCategoryMid(String big);

    List<String> getPoiCategorySub(String big, String mid);

    String getPoiCategoryId(String big, String mid, String sub);
}
