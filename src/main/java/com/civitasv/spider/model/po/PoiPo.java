package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.civitasv.spider.model.bo.POI;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * POI 数据库表
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
@Getter
@Setter
@ToString
@Accessors(fluent = true)
@Builder
@TableName("poi")
public class PoiPo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("JOB_ID")
    private Long jobId;

    @TableField("POI_ID")
    private String poiId;

    @TableField("NAME")
    private String name;

    @TableField("TYPE")
    private String type;

    @TableField("TYPE_CODE")
    private String typeCode;

    @TableField("BIZ_TYPE")
    private String bizType;

    @TableField("ADDRESS")
    private String address;

    @TableField("LOCATION")
    private String location;

    @TableField("TEL")
    private String tel;

    @TableField("PROVINCE_NAME")
    private String provinceName;

    @TableField("CITY_NAME")
    private String cityName;

    @TableField("AD_NAME")
    private String adName;

    // 以下为 extensions = all 时返回
    @TableField("POST_CODE")
    private String postCode;

    @TableField("WEBSITE")
    private String website;

    @TableField("EMAIL")
    private String email;

    @TableField("PROVINCE_CODE")
    private String provinceCode;

    @TableField("CITY_CODE")
    private String cityCode;

    @TableField("AD_CODE")
    private String adCode;

    public POI.Info toPoiInfo() {
        return POI.Info.builder()
                .id(id)
                .poiId(poiId)
                .name(name)
                .type(type)
                .typeCode(typeCode)
                .bizType(bizType)
                .address(address)
                .location(location)
                .tel(tel)
                .provinceName(provinceName)
                .cityName(cityName)
                .adName(adName)
                .build();
    }

    public POI.Info toPoiInfoWithExtensions(){
        return POI.Info.builder()
                .id(id)
                .poiId(poiId)
                .name(name)
                .type(type)
                .typeCode(typeCode)
                .bizType(bizType)
                .address(address)
                .location(location)
                .tel(tel)
                .provinceName(provinceName)
                .cityName(cityName)
                .adName(adName)
                .postCode(postCode)
                .website(website)
                .email(email)
                .provinceCode(provinceCode)
                .cityCode(cityCode)
                .adCode(adCode)
                .build();
    }
}
