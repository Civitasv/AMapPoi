package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 获取高德 POI 类型信息，方便用户选取并展示
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-20 05:56:08
 */
@Getter
@Setter
@ToString
@Accessors(fluent = true)
@TableName("poi_category")
public class PoiCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("CATE_ID")
    private String cateId;

    @TableField("BIG")
    private String big;

    @TableField("MID")
    private String mid;

    @TableField("SUB")
    private String sub;
}
