package com.civitasv.spider.model;

import java.util.Arrays;

public class POI {
    private final Integer status;
    private final String info;
    private final String infocode;
    private final Integer count;
    private final Info[] pois;

    public static class Info {
        public String id;
        public String name;
        public String type;
        public String typecode;
        public Object address;
        public Object location;
        public Object pname;
        public Object cityname;
        public Object adname;

        @Override
        public String toString() {
            return "Info{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", typecode='" + typecode + '\'' +
                    ", address='" + address + '\'' +
                    ", location='" + location + '\'' +
                    ", pname='" + pname + '\'' +
                    ", cityname='" + cityname + '\'' +
                    ", adname='" + adname + '\'' +
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
