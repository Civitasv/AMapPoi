package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@TableName("city_code")
@Accessors(fluent = true)
@RequiredArgsConstructor
public class City {
    @TableId("CITY_ID")
    private final String cityId;
    @TableField("NAME")
    private final String cityName;

    @Override
    public String toString() {
        return cityName;
    }
}
