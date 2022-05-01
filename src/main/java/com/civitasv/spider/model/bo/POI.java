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
    @Accessors(fluent = true)
    @AllArgsConstructor
    public enum OutputFields {
        ID("id", "id", "唯一ID", false, true),
        NAME("name", "name", "名称", false, true),
        TYPE("type", "type", "类型", false, true),
        TYPE_CODE("typeCode", "typeCode", "类型编码", false, true),
        BIZ_TYPE("bizType", "bizType", "行业类型", false, true),
        ADDRESS("address", "address", "地址", false, true),
        GCJ02_LNG("gcj02_lng", "gcj02_lng", "gcj02 格式经度", false, true),
        GCJ02_LAT("gcj02_lat", "gcj02_lat", "gcj02 格式纬度", false, true),
        WGS84_LNG("wgs84_lng", "wgs84_lng", "wgs84 格式经度", false, true),
        WGS84_LAT("wgs84_lat", "wgs84_lat", "wgs84 格式纬度", false, true),
        TEL("tel", "tel", "电话", false, true),
        PROVINCE_NAME("provinceName", "province", "省份名称", false, true),
        CITY_NAME("cityName", "city", "城市名称", false, true),
        AD_NAME("adName", "ad", "区域名称", false, true),

        POST_CODE("postCode", "postCode", "邮编", true, false),
        WEBSITE("website", "website", "网址", true, false),
        EMAIL("email", "email", "邮箱", true, false),
        PROVINCE_CODE("provinceCode", "provCode", "省份编码", true, false),
        CITY_CODE("cityCode", "cityCode", "城市编码", true, false),
        AD_CODE("adCode", "adCode", "区域编码", true, false);
        private final String fieldName;
        private final String shapeFieldName; // shp 字段长度有限制
        private final String description;
        private final boolean inExtension;
        private boolean checked;

        public void checked(boolean checked) {
            this.checked = checked;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @Builder
    @ToString
    public static class Info {
        private transient Long id;  // 对应PoiPo id
        @SerializedName("id")
        private Object poiId; // 唯一id 对应pid
        @SerializedName("name")
        private Object name; // 名称
        @SerializedName("type")
        private Object type; // 兴趣点类型
        @SerializedName("typecode")
        private Object typeCode; // 兴趣点类型编码
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
                    .poiId(BeanUtils.obj2String(poiId))
                    .jobId(jobId)
                    .name(BeanUtils.obj2String(name))
                    .type(BeanUtils.obj2String(type))
                    .typeCode(BeanUtils.obj2String(typeCode))
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
                    .poiId(BeanUtils.obj2String(poiId))
                    .jobId(jobId)
                    .name(BeanUtils.obj2String(name))
                    .type(BeanUtils.obj2String(type))
                    .typeCode(BeanUtils.obj2String(typeCode))
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
