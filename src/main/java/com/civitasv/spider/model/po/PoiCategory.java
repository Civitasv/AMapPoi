package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-20 05:56:08
 */
@TableName("poi_category")
public class PoiCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("CATE_ID")
    private String cateId;

    @TableField("CATE1")
    private String cate1;

    @TableField("CATE2")
    private String cate2;

    @TableField("CATE3")
    private String cate3;


    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getCate1() {
        return cate1;
    }

    public void setCate1(String cate1) {
        this.cate1 = cate1;
    }

    public String getCate2() {
        return cate2;
    }

    public void setCate2(String cate2) {
        this.cate2 = cate2;
    }

    public String getCate3() {
        return cate3;
    }

    public void setCate3(String cate3) {
        this.cate3 = cate3;
    }

    @Override
    public String toString() {
        return "PoiCategory{" +
        "cateId=" + cateId +
        ", cate1=" + cate1 +
        ", cate2=" + cate2 +
        ", cate3=" + cate3 +
        "}";
    }
}
