package com.civitasv.spider.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.civitasv.spider.model.bo.Job;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhanghang
 * @since 2022-04-06 09:08:52
 */
@TableName("job")
public class JobPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;

    @TableField("taskid")
    private Integer taskid;

    @TableField("keys")
    private String keys;

    @TableField("types")
    private String types;

    @TableField("keywords")
    private String keywords;

    @TableField("page")
    private Integer page;

    @TableField("size")
    private Integer size;

    @TableField("status")
    private Integer status;

    @TableField("request_excepted_times")
    private Integer requestExceptedTimes;

    @TableField("poi_actial_sum")
    private Integer poiActialSum;

    @TableField("poi_excetued_sum")
    private Integer poiExcetuedSum;

    @TableField("total_executed_time")
    private Integer totalExecutedTime;

    @TableField("bounds")
    private String bounds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRequestExceptedTimes() {
        return requestExceptedTimes;
    }

    public void setRequestExceptedTimes(Integer requestExceptedTimes) {
        this.requestExceptedTimes = requestExceptedTimes;
    }

    public Integer getPoiActialSum() {
        return poiActialSum;
    }

    public void setPoiActialSum(Integer poiActialSum) {
        this.poiActialSum = poiActialSum;
    }

    public Integer getPoiExcetuedSum() {
        return poiExcetuedSum;
    }

    public void setPoiExcetuedSum(Integer poiExcetuedSum) {
        this.poiExcetuedSum = poiExcetuedSum;
    }

    public Integer getTotalExecutedTime() {
        return totalExecutedTime;
    }

    public void setTotalExecutedTime(Integer totalExecutedTime) {
        this.totalExecutedTime = totalExecutedTime;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        return "Job{" +
        "id=" + id +
        ", taskid=" + taskid +
        ", keys=" + keys +
        ", types=" + types +
        ", keywords=" + keywords +
        ", page=" + page +
        ", size=" + size +
        ", status=" + status +
        ", requestExceptedTimes=" + requestExceptedTimes +
        ", poiActialSum=" + poiActialSum +
        ", poiExcetuedSum=" + poiExcetuedSum +
        ", totalExecutedTime=" + totalExecutedTime +
        "}";
    }

    public Job toJob(){
        return new Job(id, null,
                Arrays.stream(bounds.split("\\|")).map(Double::valueOf).toArray(Double[]::new),
                types, keywords, page, size);
    }
}
