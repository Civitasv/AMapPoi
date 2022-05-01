package com.civitasv.spider.util;

import com.civitasv.spider.model.bo.Job;
import com.civitasv.spider.model.bo.POI;
import com.civitasv.spider.model.po.JobPo;
import com.civitasv.spider.model.po.PoiPo;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeanUtils {
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

    public static List<PoiPo> jobs2PoiPos(List<Job> jobs, boolean includeExtensions) {
        return jobs.stream().filter(job -> job.poi() != null)
                .flatMap(job -> job.poi().details()
                        .stream()
                        .map(info -> includeExtensions
                                ? info.toPoiPoWithExtensions(job.jobId())
                                : info.toPoiPo(job.jobId())))
                .collect(Collectors.toList());
    }

    public static List<JobPo> jobs2JobPos(List<Job> jobs) {
        return jobs.stream()
                .map(Job::toJobPo)
                .collect(Collectors.toList());
    }

    public static List<Job> jobPos2Jobs(List<JobPo> jobPos) {
        return jobPos.stream()
                .map(JobPo::toJob)
                .collect(Collectors.toList());
    }

    public static List<POI.Info> poiPo2PoiInfo(List<PoiPo> poiPos, boolean includeExtensions) {
        return poiPos.stream()
                .map(includeExtensions ? PoiPo::toPoiInfoWithExtensions : PoiPo::toPoiInfo)
                .collect(Collectors.toList());
    }

    public static String obj2String(Object obj) {
        return Objects.toString(obj, "");
    }
}
