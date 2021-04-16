package com.civitasv.spider.util;

import net.sf.cglib.beans.BeanMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bean2Map {
    /**
     * 将对象装换为map
     *
     * @param bean PoJo对象
     * @return Map
     */
    public static <T> Map<String, String> beanToMap(T bean) {
        Map<String, String> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (beanMap.get(key) != null)
                    map.put(key + "", beanMap.get(key).toString());
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map  Map
     * @param bean JavaBean
     * @return Map
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
