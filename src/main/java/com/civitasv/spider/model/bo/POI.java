package com.civitasv.spider.model.bo;

import com.civitasv.spider.model.po.PoiPo;
import com.civitasv.spider.util.BeanUtils;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
@Builder
@ToString
public class POI {
    @SerializedName("status")
    private final Integer status;
    @SerializedName("info")
    private String info;
    @SerializedName("infocode")
    private Integer infoCode;
    @SerializedName("count")
    private Integer count;
    @SerializedName("pois")
    private List<Info> details;

    @Getter
    @Setter
    @Accessors(fluent = true)
    @Builder
    @ToString
    public static class Info {
        private transient Long id;  // 对应PoiPo id
        @SerializedName("id")
        private String poiId; // 唯一id 对应pid
        @SerializedName("name")
        private String name; // 名称
        @SerializedName("type")
        private String type; // 兴趣点类型
        @SerializedName("typecode")
        private String typeCode; // 兴趣点类型编码
        @SerializedName("biz_type")
        private Object bizType; // 行业类型
        @SerializedName("address")
        private Object address; // 地址
        @SerializedName("location")
        private Object location; // 经纬度
        @SerializedName("tel")
        private Object tel; // 电话
        @SerializedName("pname")
        private Object provinceName; //  省份名称
        @SerializedName("cityname")
        private Object cityName; // 城市名称
        @SerializedName("adname")
        private Object adName; // 区域名称

        @SerializedName("postcode")
        private Object postCode; // 邮编，extensions=all时返回
        @SerializedName("website")
        private Object website; // 网址，extensions=all时返回
        @SerializedName("email")
        private Object email; // 邮箱，extensions=all时返回
        @SerializedName("pcode")
        private Object provinceCode; // 省份编码，extensions=all时返回
        @SerializedName("citycode")
        private Object cityCode; // 城市编码，extensions=all时返回
        @SerializedName("adcode")
        private Object adCode; // 区域编码，extensions=all时返回

        public PoiPo toPoiPo(Long jobId) {
            return PoiPo.builder()
                    .id(id)
                    .poiId(poiId)
                    .jobId(jobId)
                    .name(name)
                    .type(type)
                    .typeCode(typeCode)
                    .bizType(BeanUtils.obj2String(bizType))
                    .address(BeanUtils.obj2String(address))
                    .location(BeanUtils.obj2String(location))
                    .tel(BeanUtils.obj2String(tel))
                    .provinceName(BeanUtils.obj2String(provinceName))
                    .cityName(BeanUtils.obj2String(cityName))
                    .adName(BeanUtils.obj2String(adName))
                    .build();
        }

        public PoiPo toPoiPoWithExtensions(Long jobId) {
            return PoiPo.builder()
                    .id(id)
                    .poiId(poiId)
                    .jobId(jobId)
                    .name(name)
                    .type(type)
                    .typeCode(typeCode)
                    .bizType(BeanUtils.obj2String(bizType))
                    .address(BeanUtils.obj2String(address))
                    .location(BeanUtils.obj2String(location))
                    .tel(BeanUtils.obj2String(tel))
                    .provinceName(BeanUtils.obj2String(provinceName))
                    .cityName(BeanUtils.obj2String(cityName))
                    .adName(BeanUtils.obj2String(adName))
                    .postCode(BeanUtils.obj2String(postCode))
                    .website(BeanUtils.obj2String(website))
                    .email(BeanUtils.obj2String(email))
                    .provinceCode(BeanUtils.obj2String(provinceCode))
                    .cityCode(BeanUtils.obj2String(cityCode))
                    .adCode(BeanUtils.obj2String(adCode))
                    .build();
        }
    }
}
