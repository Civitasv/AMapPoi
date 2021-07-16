package com.civitasv.spider.model;

import java.util.Arrays;

public class POI {
    private final Integer status;
    private final String info;
    private final String infocode;
    private final Integer count;
    private final Info[] pois;

    public static class Info {
        public String id; // 唯一id
        public String name; // 名称
        public String type; // 兴趣点类型
        public String typecode; // 兴趣点类型编码
        public Object address; // 地址
        public Object location; // 经纬度
        private String tel; // 电话
        public Object pname; //  省份名称
        public Object cityname; // 城市名称
        public Object adname; // 区域名称

        @Override
        public String toString() {
            return "Info{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", typecode='" + typecode + '\'' +
                    ", address=" + address +
                    ", location=" + location +
                    ", tel='" + tel + '\'' +
                    ", pname=" + pname +
                    ", cityname=" + cityname +
                    ", adname=" + adname +
                    '}';
        }
    }

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public String getInfocode() {
        return infocode;
    }

    public int getCount() {
        return count;
    }

    public Info[] getPois() {
        return pois;
    }

    public POI(Integer status, String info, String infocode, Integer count, Info[] pois) {
        this.status = status;
        this.info = info;
        this.infocode = infocode;
        this.count = count;
        this.pois = pois;
    }

    @Override
    public String toString() {
        return "POI{" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", infocode='" + infocode + '\'' +
                ", count=" + count +
                ", pois=" + Arrays.toString(pois) +
                '}';
    }
}
