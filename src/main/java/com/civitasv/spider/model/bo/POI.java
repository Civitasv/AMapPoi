package com.civitasv.spider.model.bo;

import com.civitasv.spider.model.po.PoiPo;

import java.util.List;

public class POI {
    private final Integer status;
    private String info;
    private String infocode;
    private Integer count;
    private List<Info> pois;

    public static class Info {
        public Long oid;  // 对应id
        public String id; // 唯一id 对应pid
        public String name; // 名称
        public String type; // 兴趣点类型
        public String typecode; // 兴趣点类型编码
        public Object address; // 地址
        public Object location; // 经纬度
        public Object tel; // 电话
        public Object pname; //  省份名称
        public Object cityname; // 城市名称
        public Object adname; // 区域名称

        public Info(Long oid, String id, String name, String type, String typecode, Object address, Object location, Object tel, Object pname, Object cityname, Object adname) {
            this.oid = oid;
            this.id = id;
            this.name = name;
            this.type = type;
            this.typecode = typecode;
            this.address = address;
            this.location = location;
            this.tel = tel;
            this.pname = pname;
            this.cityname = cityname;
            this.adname = adname;
        }

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

        public PoiPo toPoiPo(Long jobid){
            return new PoiPo(oid, id, jobid, name, type, typecode, address.toString(), location.toString(), tel.toString(), pname.toString(), cityname.toString(), adname.toString());
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

    public List<Info> getPois() {
        return pois;
    }

    public POI(Integer status) {
        this.status = status;
    }

    public POI(Integer status, String info, String infocode, Integer count, List<Info> pois) {
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
                ", pois=" + pois +
                '}';
    }
}
