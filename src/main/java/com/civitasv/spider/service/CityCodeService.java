package com.civitasv.spider.service;

import com.civitasv.spider.model.po.City;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-22 09:03:30
 */
public interface CityCodeService {
    List<City> listByCityId(String cityId);
}
