package com.civitasv.spider.model.bo;

import com.civitasv.spider.helper.*;
import com.civitasv.spider.util.BoundaryUtil;
import com.civitasv.spider.viewmodel.POIViewModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

public class Task {
    public Integer id;
    public Queue<String> aMapKeys;
    public String types;
    public String keywords;
    public Integer threadNum;
    public Integer threshold;
    public String outputDirectory;
    public OutputType outputType;
    public UserType userType;
    public Double[] boundary;
    public Integer requestActualTimes;
    public Integer requestExceptedTimes;
    public Integer poiActualSum;
    public Integer poiExecutedSum;
    public Integer totalExecutedTime;
    public BoundaryType boundryType;
    public String boundryConfig;
    public Predicate<? super POI.Info> filter;
    public List<Job> Jobs;
    public TaskStatus taskStatus = TaskStatus.UnStarted;

    public Task(Integer id, Queue<String> aMapKeys, String types, String keywords, Integer threadNum, Integer threshold,
                String outputDirectory, OutputType outputType, UserType userType,
                Integer requestActualTimes, Integer requestExceptedTimes, Integer poiActualSum, Integer poiExecutedSum,
                Integer totalExecutedTime, String boundryConfig, TaskStatus taskStatus, Double[] bounds) {
        this.id = id;
        this.aMapKeys = aMapKeys;
        this.types = types;
        this.keywords = keywords;
        this.threadNum = threadNum;
        this.threshold = threshold;
        this.outputDirectory = outputDirectory;
        this.outputType = outputType;
        this.userType = userType;
        this.requestActualTimes = requestActualTimes;
        this.requestExceptedTimes = requestExceptedTimes;
        this.poiActualSum = poiActualSum;
        this.poiExecutedSum = poiExecutedSum;
        this.totalExecutedTime = totalExecutedTime;
        this.boundryConfig = boundryConfig;
        this.boundryType = BoundaryType.getBoundryType(boundryConfig.split(":")[0]);
        this.Jobs = new ArrayList<>();
        this.taskStatus = taskStatus;
        this.boundary = bounds;

        // 生成filter
        final GeometryFactory geometryFactory = new GeometryFactory();
        String configContent = boundryConfig.split(":")[1];
        switch (boundryType){
            case ADCODE:
                String adCode = configContent.split(",")[0];
                String adName = configContent.split(",")[1];
                filter = info -> {
                    int level = getLevel(adCode);
                    if (level == 0)
                        return "中华人民共和国".equals(adName);
                    else if (level == 1)
                        return info.pname.equals(adName);
                    else if (level == 2)
                        return info.cityname.equals(adName);
                    else return info.adname.equals(adName);
                };
                break;
            case RECTANGLE:
                filter = info -> true;
                break;
            case CUSTOM:
                String[] filepathPlusType = configContent.split(",");
                Geometry boundary = POIViewModel.getBoundaryByUserFile(filepathPlusType[0],
                        CoordinateType.getBoundryType(filepathPlusType[1]));
                filter = info -> {
                    if (info.location == null) return false;
                    String[] lonlat = info.location.split(",");
                    if (lonlat.length != 2) {
                        return false;
                    }
                    Coordinate coordinate = new Coordinate(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                    return boundary.intersects(geometryFactory.createPoint(coordinate));
                };
                break;
        }
    }

    private int getLevel(String adCode) {
        if ("100000".equals(adCode)) {
            return 0; // country
        } else if ("0000".equals(adCode.substring(2))) {
            return 1; // 省份
        } else if ("00".equals(adCode.substring(4))) {
            return 2; // 城市
        } else return 3; // 县/区
    }

    public static Double[] parseBoundryConfig(String config) throws Exception {
        String[] typePlusConfig = config.split(":");
        String configType = typePlusConfig[0];
        switch (configType) {
            case "行政区":
                String adCode = typePlusConfig[1];
                // 获取完整边界和边界名
                Map<String, Object> data = BoundaryUtil.getBoundaryAndAdNameByAdCode(adCode);
                if (data == null) {
                    throw new Exception("网络异常，请稍后重试");
                }
                Geometry geometry = (Geometry) data.get("gcj02Boundary");
                return POIViewModel.getBoundaryFromGeometry(geometry);


            case "矩形":
                // 获取坐标类型
                String[] rectanglePlusType = typePlusConfig[1].split(",");
                String rectangle = rectanglePlusType[0];
                CoordinateType type = CoordinateType.valueOf(rectanglePlusType[1]);
                Double[] rectangleBoundary = POIViewModel.getBoundaryByRectangle(rectangle, type);
                if (rectangleBoundary == null) {
                    throw new Exception("无法获取矩形边界，请检查矩形格式！");
                }
                return rectangleBoundary;
            case "自定义":
                String[] filepathPlusType = typePlusConfig[1].split(",");
                Geometry boundary = POIViewModel.getBoundaryByUserFile(filepathPlusType[0],
                        CoordinateType.getBoundryType(filepathPlusType[1]));
                rectangleBoundary = POIViewModel.getBoundaryFromGeometry(boundary);
                if (rectangleBoundary == null) {
                    throw new Exception("geojson文件解析失败");
                }
                return rectangleBoundary;
        }




        throw new RuntimeException("代码不应到达此处");
    }
}
