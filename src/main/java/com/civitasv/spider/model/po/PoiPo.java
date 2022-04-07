package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
@TableName("poi")
public class PoiPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Integer id;

    @TableField("jobid")
    private String jobid;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("typecode")
    private String typecode;

    @TableField("address")
    private String address;

    @TableField("location")
    private String location;

    @TableField("tel")
    private String tel;

    @TableField("pname")
    private String pname;

    @TableField("cityname")
    private String cityname;

    @TableField("adname")
    private String adname;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    @Override
    public String toString() {
        return "Poi{" +
        "id=" + id +
        ", jobid=" + jobid +
        ", name=" + name +
        ", type=" + type +
        ", typecode=" + typecode +
        ", address=" + address +
        ", location=" + location +
        ", tel=" + tel +
        ", pname=" + pname +
        ", cityname=" + cityname +
        ", adname=" + adname +
        "}";
    }
}
