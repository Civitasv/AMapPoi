package com.civitasv.spider.viewmodel;

import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.helper.CoordinateType;
import com.civitasv.spider.model.POIJob;
import com.civitasv.spider.model.Feature;
import com.civitasv.spider.model.GeoJSON;
import com.civitasv.spider.model.POI;
import com.civitasv.spider.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class POIViewModel {
    private final AMapDao mapDao;
    private ExecutorService worker, executorService;
    private final GeometryFactory geometryFactory;
    private final ViewHolder viewHolder;
    private final DataHolder dataHolder;

    private boolean hasStart;

    public POIViewModel(TextField threadNum, TextField keywords, TextArea keys, TextField types,
                        TextField adCode, TextField rectangle, TextField threshold, ChoiceBox<String> format,
                        TextField outputDirectory, TextArea messageDetail, TextField userFile, TextField failJobsFile, TabPane tabs,
                        Button directoryBtn, Button execute, Button poiType, ChoiceBox<String> userType,
                        ChoiceBox<CoordinateType> rectangleCoordinateType, ChoiceBox<CoordinateType> userFileCoordinateType,
                        MenuItem wechat, MenuItem joinQQ) {
        this.viewHolder = new ViewHolder(threadNum, keywords, keys, types, adCode,
                rectangle, threshold, format, outputDirectory, messageDetail, userFile, failJobsFile, tabs, directoryBtn,
                execute, poiType, userType, rectangleCoordinateType, userFileCoordinateType, wechat, joinQQ);
        this.dataHolder = new DataHolder();
        this.geometryFactory = new GeometryFactory();
        this.mapDao = new AMapDaoImpl();
        this.hasStart = false;
    }

    public static class ViewHolder {
        public TextField threadNum; // 线程数目
        public TextField keywords; // 关键字
        public TextArea keys; // 高德 API Key
        public TextField types; // 类型
        public TextField adCode; // 行政区六位代码
        public TextField rectangle; // 矩形左上角#矩形右下角
        public TextField threshold; // 阈值
        public ChoiceBox<String> format; // 输出格式
        public TextField outputDirectory; // 输出文件夹
        public TextArea messageDetail; // 输出信息
        public TextField userFile; // 用户自定义文件
        public TextField failJobsFile; // 任务失败文件
        public TabPane tabs; // tab 栏
        public Button directoryBtn; // 点击选择文件夹
        public Button execute; // 执行
        public Button poiType; // 点击查看 poi 类型
        public ChoiceBox<String> userType; // 用户类型
        public ChoiceBox<CoordinateType> rectangleCoordinateType; // 矩形坐标格式
        public ChoiceBox<CoordinateType> userFileCoordinateType; // 用户自定义文件坐标格式
        public MenuItem wechat; // 微信
        public MenuItem joinQQ; // QQ群

        public ViewHolder(TextField threadNum, TextField keywords, TextArea keys, TextField types,
                          TextField adCode, TextField rectangle, TextField threshold, ChoiceBox<String> format,
                          TextField outputDirectory, TextArea messageDetail, TextField userFile, TextField failJobsFile, TabPane tabs,
                          Button directoryBtn, Button execute, Button poiType, ChoiceBox<String> userType,
                          ChoiceBox<CoordinateType> rectangleCoordinateType, ChoiceBox<CoordinateType> userFileCoordinateType,
                          MenuItem wechat, MenuItem joinQQ) {
            this.threadNum = threadNum;
            this.keywords = keywords;
            this.keys = keys;
            this.types = types;
            this.adCode = adCode;
            this.rectangle = rectangle;
            this.threshold = threshold;
            this.format = format;
            this.outputDirectory = outputDirectory;
            this.messageDetail = messageDetail;
            this.userFile = userFile;
            this.failJobsFile = failJobsFile;
            this.tabs = tabs;
            this.directoryBtn = directoryBtn;
            this.execute = execute;
            this.poiType = poiType;
            this.userType = userType;
            this.rectangleCoordinateType = rectangleCoordinateType;
            this.userFileCoordinateType = userFileCoordinateType;
            this.wechat = wechat;
            this.joinQQ = joinQQ;
        }
    }

    private static class DataHolder {
        public Queue<String> aMapKeys;
        public Integer threadNum;
        public Integer threshold;
        private Integer qps;
        public Integer perExecuteTime;
        public String keywords;
        public String types;
        public String tab;
        public double[] boundary;
    }

    private boolean check() {
        if (viewHolder.keys.getText().isEmpty()) {
            // keys为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "高德key", null, "高德key池不能为空！"));
            return false;
        }
        if (viewHolder.keywords.getText().isEmpty() && viewHolder.types.getText().isEmpty()) {
            if (!"失败文件".equals(viewHolder.tabs.getSelectionModel().getSelectedItem().getText())) {
                // 关键字和类型均为空
                Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "参数设置", null, "POI关键字和POI类型两者至少必填其一！"));
                return false;
            }
        }
        if (viewHolder.threshold.getText().isEmpty()) {
            // 阈值为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "阈值", null, "阈值不能为空！"));
            return false;
        }
        if (viewHolder.threadNum.getText().isEmpty()) {
            // 线程数目为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "线程数目", null, "线程数目不能为空！"));
            return false;
        }
        if (viewHolder.outputDirectory.getText().isEmpty()) {
            // 输出文件夹为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输出文件夹", null, "输出文件夹不能为空！"));
            return false;
        }
        return true;
    }

    private boolean aMapKeys() {
        appendMessage("读取高德key中");
        Queue<String> aMapKeys = parseKeyText(viewHolder.keys.getText());
        if (aMapKeys == null) {
            // key解析异常
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "高德key", null, "请检查key的格式！"));
            return false;
        }
        dataHolder.aMapKeys = aMapKeys;
        appendMessage("高德key读取成功");
        return true;
    }

    private boolean threadNum() {
        appendMessage("读取线程数目中");
        Integer threadNum = ParseUtil.tryParse(viewHolder.threadNum.getText());
        if (threadNum == null) {
            appendMessage("解析线程数目失败，请检查！");
            analysis(false);
            return false;
        }
        appendMessage("线程数目读取成功");
        dataHolder.threadNum = threadNum;
        return true;
    }

    private boolean threshold() {
        appendMessage("读取阈值中");
        Integer threshold = ParseUtil.tryParse(viewHolder.threshold.getText());
        if (threshold == null) {
            appendMessage("解析阈值失败，请检查！");
            analysis(false);
            return false;
        }
        appendMessage("阈值读取成功");
        dataHolder.threshold = threshold;
        return true;
    }

    private boolean keywords() {
        appendMessage("读取POI关键字中");
        StringBuilder keywords = new StringBuilder();
        String[] keywordArr = viewHolder.keywords.getText().split(",");
        for (int i = 0; i < keywordArr.length; i++) {
            keywords.append(keywordArr[i]);
            if (i != keywordArr.length - 1)
                keywords.append("|");
        }
        appendMessage("POI关键字读取成功");
        dataHolder.keywords = keywords.toString();
        return true;
    }

    private boolean types() {
        appendMessage("读取POI类型中");
        StringBuilder types = new StringBuilder();
        String[] typeArr = viewHolder.types.getText().split(",");
        for (int i = 0; i < typeArr.length; i++) {
            types.append(typeArr[i]);
            if (i != typeArr.length - 1)
                types.append("|");
        }
        appendMessage("POI类型读取成功");
        dataHolder.types = types.toString();
        return true;
    }

    private boolean qps() {
        // 读取开发者类型
        int qps = 0;
        appendMessage("您是" + viewHolder.userType.getValue());
        switch (viewHolder.userType.getValue()) {
            case "个人开发者":
                qps = 20;
                break;
            case "个人认证开发者":
                qps = 50;
                break;
            case "企业开发者":
                qps = 300;
                break;
        }
        dataHolder.qps = qps;
        return true;
    }

    private boolean tab() {
        String tab = viewHolder.tabs.getSelectionModel().getSelectedItem().getText();
        switch (tab) {
            case "行政区":
                if (viewHolder.adCode.getText().isEmpty()) {
                    // 行政区为空
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "行政区代码", null, "请设置行政区代码！"));
                    return false;
                }
                break;
            case "矩形":
                if (viewHolder.rectangle.getText().isEmpty()) {
                    // 行政区为空
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "矩形", null, "请设置矩形范围！"));
                    return false;
                }
                if (!parseRect(viewHolder.rectangle.getText())) {
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "矩形", null, "请检查矩形输入格式！\n 正确格式：114.12,30.53#115.28,29.59"));
                    return false;
                }
                break;
            case "自定义":
                if (viewHolder.userFile.getText().isEmpty()) {
                    // 行政区为空
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "自定义", null, "请设置geojson文件路径！"));
                    return false;
                }
                break;
            case "失败文件":
                if (viewHolder.failJobsFile.getText().isEmpty()) {
                    // 行政区为空
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "失败文件", null, "请设置失败任务文件路径！"));
                    return false;
                }
                break;
        }
        dataHolder.tab = tab;
        return true;
    }

    private void alterThreadNum() {
        if (dataHolder.threadNum > dataHolder.qps * dataHolder.aMapKeys.size()) {
            int val = dataHolder.qps * dataHolder.aMapKeys.size();
            appendMessage(viewHolder.userType.getValue() + "线程数不能超过" + val);
            dataHolder.threadNum = val;
            appendMessage("设置线程数目为" + val);
        }
    }

    /**
     * 根据线程数、key数目和QPS设置每次运行时间
     * <p>
     * threadNum: 线程数目
     * qps:       用户qps
     * keysNum:   key数量
     * 每次运行时间 ms
     */
    private void perExecuteTime() {
        int threadNum = dataHolder.threadNum, qps = dataHolder.qps, keysNum = dataHolder.aMapKeys.size();
        dataHolder.perExecuteTime = (int) (1000 * (threadNum * 1.0 / (qps * keysNum)));
    }

    private double[] getBoundaryFromGeometry(Geometry geometry) {
        Envelope envelopeInternal = geometry.getEnvelopeInternal();

        double left = envelopeInternal.getMinX(), bottom = envelopeInternal.getMinY(),
                right = envelopeInternal.getMaxX(), top = envelopeInternal.getMaxY();
        return new double[]{left, bottom, right, top};
    }

    public void execute() {
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            clearMessage();
            if (!check()) return;
            if (!aMapKeys()) return;
            if (!threadNum()) return;
            if (!threshold()) return;
            if (!keywords()) return;
            if (!types()) return;
            if (!qps()) return;
            if (!tab()) return;
            alterThreadNum();
            perExecuteTime();
            analysis(true);

            switch (dataHolder.tab) {
                case "行政区":
                    if (!hasStart) return;
                    String adCode = viewHolder.adCode.getText();
                    appendMessage("获取行政区 " + adCode + " 区域边界中");
                    // 获取完整边界和边界名
                    Map<String, Object> data = BoundaryUtil.getBoundaryAndAdNameByAdCode(adCode);
                    if (data == null) {
                        if (!hasStart) return;
                        Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "行政区边界", null, "无法获取行政区边界，请检查行政区代码或稍后重试！"));
                        analysis(false);
                        return;
                    }
                    String adName = (String) data.get("adName");
                    Geometry geometry = (Geometry) data.get("gcj02Boundary");
                    dataHolder.boundary = getBoundaryFromGeometry(geometry);
                    if (!hasStart) return;
                    appendMessage("成功获取行政区 " + adCode + ":" + adName + " 区域边界");
                    getPoiData(dataHolder.boundary, dataHolder.threadNum,
                            dataHolder.threshold, dataHolder.keywords, dataHolder.types,
                            dataHolder.aMapKeys, info -> {
                                int level = getLevel(adCode);
                                if (level == 0)
                                    return "中华人民共和国".equals(adName);
                                else if (level == 1)
                                    return info.pname.equals(adName);
                                else if (level == 2)
                                    return info.cityname.equals(adName);
                                else return info.adname.equals(adName);
                            });
                    break;
                case "矩形":
                    // 获取坐标类型
                    if (!hasStart) return;
                    String rectangle = viewHolder.rectangle.getText();
                    CoordinateType type = viewHolder.rectangleCoordinateType.getValue();
                    appendMessage("解析矩形区域中");
                    double[] rectangleBoundary = getBoundaryByRectangle(rectangle, type);
                    if (rectangleBoundary == null) {
                        if (!hasStart) return;
                        Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "矩形", null, "无法获取矩形边界，请检查矩形格式！"));
                        analysis(false);
                        return;
                    }
                    dataHolder.boundary = rectangleBoundary;
                    if (!hasStart) return;
                    appendMessage("解析矩形区域成功");
                    getPoiData(dataHolder.boundary, dataHolder.threadNum,
                            dataHolder.threshold, dataHolder.keywords, dataHolder.types,
                            dataHolder.aMapKeys, info -> true);
                    break;
                case "自定义":
                    if (!hasStart) return;
                    appendMessage("解析用户geojson文件中");
                    Geometry boundary = getBoundaryByUserFile(viewHolder.userFile.getText(), viewHolder.userFileCoordinateType.getValue());
                    if (boundary == null) {
                        if (!hasStart) return;
                        Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "自定义", null, "geojson文件解析失败"));
                        analysis(false);
                        return;
                    }
                    dataHolder.boundary = getBoundaryFromGeometry(boundary);
                    if (!hasStart) return;
                    appendMessage("成功解析用户文件");

                    getPoiData(dataHolder.boundary, dataHolder.threadNum,
                            dataHolder.threshold, dataHolder.keywords, dataHolder.types,
                            dataHolder.aMapKeys, info -> {
                                if (info.location == null) return false;
                                String[] lonlat = info.location.toString().split(",");
                                if (lonlat.length != 2) {
                                    return false;
                                }
                                Coordinate coordinate = new Coordinate(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                                return boundary.intersects(geometryFactory.createPoint(coordinate));
                            });
                    break;
                case "失败文件":
                    if (!hasStart) return;
                    appendMessage("解析失败任务文件中");
                    List<POIJob> poiJobs = parseFailJobsFile(viewHolder.failJobsFile.getText());
                    getPOIDataFromFailJobs(dataHolder.aMapKeys, dataHolder.threadNum, poiJobs);
                    break;
            }
            analysis(false);
        });
    }

    private void getPOIDataFromFailJobs(Queue<String> keys, int threadNum, List<POIJob> poiJobs) {
        List<POI.Info> res = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(threadNum);
        // 添加任务
        List<Future<POI>> futures = new ArrayList<>();
        for (POIJob job : poiJobs) {
            Future<POI> future = executorService.submit(() ->
                    getPoi(keys, job.polygon, job.keywords, job.types, job.page, job.size));
            futures.add(future);
        }

        // 阻塞获取
        List<POIJob> failJobs = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                POI item = futures.get(i).get(20, TimeUnit.SECONDS);
                if (item != null) {
                    if (item.getStatus() == -1) {
                        // 说明剩余的任务都将失败
                        for (int j = i; j < poiJobs.size(); j++) {
                            failJobs.add(poiJobs.get(j));
                        }
                        break;
                    } else {
                        res.addAll(Arrays.asList(item.getPois()));
                    }
                } else {
                    failJobs.add(poiJobs.get(i));
                }
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                failJobs.add(poiJobs.get(i));
            }
        }
        executorService.shutdown();
        appendMessage("共获得POI：" + res.size() + "条");

        appendMessage(failJobs.size() == 0 ? "POI爬取完毕" : "未完全爬取");
        // 导出res
        switch (viewHolder.format.getValue()) {
            case "csv":
            case "txt":
                writeToCsvOrTxt(res, viewHolder.format.getValue());
                break;
            case "geojson":
                writeToGeoJson(res);
                break;
            case "shp":
                writeToShp(res);
        }
        // 导出失败任务
        if (failJobs.size() > 0)
            writeToFailJobsTxt(failJobs);
    }

    private List<POIJob> parseFailJobsFile(String failJobsFilePath) {
        List<POIJob> jobs = new ArrayList<>();
        List<Map<String, String>> parseRes = ParseUtil.parseTxtOrCsv(failJobsFilePath);
        for (Map<String, String> item : parseRes) {
            jobs.add(new POIJob(item.get("polygon"), item.get("keywords"), item.get("types"), Integer.parseInt(item.get("page")), Integer.parseInt(item.get("size"))));
        }
        return jobs;
    }

    public void cancel() {
        analysis(false);
    }

    private void analysis(boolean start) {
        if (!hasStart && !start) {
            return;
        }
        if (hasStart && start) {
            return;
        }
        hasStart = start;
        setDisable(start);
        appendMessage(start ? "开始POI爬取，请勿操作" : "停止POI爬取");
        if (!start && executorService != null)
            executorService.shutdownNow();
        if (!start && worker != null)
            worker.shutdownNow();
    }

    private void setDisable(boolean isAnalysis) {
        Platform.runLater(() -> {
            viewHolder.execute.setDisable(isAnalysis);
            viewHolder.threadNum.setDisable(isAnalysis);
            viewHolder.keys.setDisable(isAnalysis);
            viewHolder.keywords.setDisable(isAnalysis);
            viewHolder.types.setDisable(isAnalysis);
            viewHolder.tabs.setDisable(isAnalysis);
            viewHolder.threshold.setDisable(isAnalysis);
            viewHolder.format.setDisable(isAnalysis);
            viewHolder.outputDirectory.setDisable(isAnalysis);
            viewHolder.directoryBtn.setDisable(isAnalysis);
            viewHolder.poiType.setDisable(isAnalysis);
            viewHolder.userType.setDisable(isAnalysis);
        });
    }

    private Queue<String> parseKeyText(String keys) {
        List<String> keyList = Arrays.asList(keys.split(","));
        String pattern = "^[A-Za-z0-9]+$";
        for (String key : keyList) {
            boolean isMatch = Pattern.matches(pattern, key);
            if (!isMatch) {
                return null;
            }
        }
        return new ArrayDeque<>(keyList);
    }

    private Geometry getBoundaryByUserFile(String path, CoordinateType type) {
        String filePath = FileUtil.readFile(path);
        if (filePath == null) {
            return null;
        }
        return BoundaryUtil.getBoundaryByDataVGeoJSON(filePath, type);
    }

    private double[] getBoundaryByRectangle(String text, CoordinateType type) {
        String[] str = text.split("#");
        if (str.length == 2) {
            String[] leftTop = str[0].split(",");
            String[] rightBottom = str[1].split(",");
            try {
                double[] leftTopLonlat = new double[]{Double.parseDouble(leftTop[0]), Double.parseDouble(leftTop[1])};
                double[] rightBottomLonlat = new double[]{Double.parseDouble(rightBottom[0]), Double.parseDouble(rightBottom[1])};
                if (type == CoordinateType.WGS84) {
                    leftTopLonlat = CoordinateTransformUtil.transformWGS84ToGCJ02(leftTopLonlat[0], leftTopLonlat[1]);
                    rightBottomLonlat = CoordinateTransformUtil.transformWGS84ToGCJ02(rightBottomLonlat[0], rightBottomLonlat[1]);
                } else if (type == CoordinateType.BD09) {
                    leftTopLonlat = CoordinateTransformUtil.transformBD09ToGCJ02(leftTopLonlat[0], leftTopLonlat[1]);
                    rightBottomLonlat = CoordinateTransformUtil.transformBD09ToGCJ02(rightBottomLonlat[0], rightBottomLonlat[1]);
                }
                if (leftTop.length == 2 && rightBottom.length == 2) {
                    return new double[]{leftTopLonlat[0], rightBottomLonlat[1], rightBottomLonlat[0], leftTopLonlat[1]};

                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private boolean getAnalysisGrids(Deque<double[]> analysisGrid, Deque<POI> analysisPoi, double[] rect, int threshold,
                                     Queue<String> keys, String keywords, String types) {
        if (!hasStart) return false;
        boolean result = true;
        int page = 1, size = 20; // 页码、每页个数
        double left = rect[0], bottom = rect[1], right = rect[2], top = rect[3];
        String polygon = left + "," + top + "|" + right + "," + bottom;

        POI poi = getPoi(keys, polygon, keywords, types, page, size); // 访问第一页
        if (poi == null || poi.getStatus() == -1) {
            appendMessage("四分失败，请重试!");
            return false;
        }

        if (poi.getCount() > threshold) {
            appendMessage("超出阈值，继续四分");
            // 继续四分
            double itemWidth = (right - left) / 2;
            double itemHeight = (top - bottom) / 2;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    result = result && getAnalysisGrids(analysisGrid, analysisPoi, new double[]{left + i * itemWidth, bottom + j * itemHeight,
                            left + (i + 1) * itemWidth, bottom + (j + 1) * itemHeight}, threshold, keys, keywords, types);
                }
            }
        } else {
            analysisGrid.add(new double[]{left, bottom, right, top});
            analysisPoi.add(poi);
        }
        return result;
    }

    private void getPoiData(double[] boundary, int threadNum, int threshold, String keywords,
                            String types, Queue<String> keys, Predicate<? super POI.Info> filter) {
        List<POI.Info> res = new ArrayList<>();
        // 1. 获取边界
        double left = boundary[0], bottom = boundary[1], right = boundary[2], top = boundary[3];
        // 2. 获取所有任务网格
        Deque<double[]> analysisGrid = new ArrayDeque<>(); // 网格剖分
        Deque<POI> analysisPoi = new ArrayDeque<>(); // 网格剖分

        appendMessage("划分所有任务网格中");
        if (getAnalysisGrids(analysisGrid, analysisPoi, new double[]{left, bottom, right, top}, threshold, keys, keywords, types)) {
            appendMessage("任务网格切分成功，共有" + analysisGrid.size() + "个任务网格");
        } else {
            return;
        }

        // 3. 开始爬取
        if (!hasStart) return;
        appendMessage("开始POI爬取，" + (!keywords.isEmpty() ? "POI关键字：" + keywords : "") + (!types.isEmpty() ? (" POI类型：" + types) : ""));

        // 失败的任务
        List<POIJob> allFailJobs = new ArrayList<>();

        executorService = Executors.newFixedThreadPool(threadNum);
        while (hasStart && !analysisGrid.isEmpty()) {
            double[] rec = analysisGrid.pop();
            POI initPoi = analysisPoi.pop();
            appendMessage("正在爬取网格" + Arrays.toString(rec) + "，任务队列剩余" + analysisGrid.size() + "个");
            Map<String, Object> item = getPoi(keys, rec, initPoi, keywords, types);
            List<POI.Info> data = (List<POI.Info>) item.get("data");
            List<POIJob> failJobs = (List<POIJob>) item.get("failJobs");
            appendMessage("该网格含POI：" + data.size() + "条");
            res.addAll(data);
            allFailJobs.addAll(failJobs);
        }
        executorService.shutdown();
        appendMessage("该区域边界共含POI：" + res.size() + "条");
        appendMessage("执行过滤算法中");
        res = res.stream().filter(filter).collect(Collectors.toList());
        appendMessage("过滤成功，共获得POI：" + res.size() + "条");

        appendMessage(allFailJobs.size() == 0 ? "POI爬取完毕" : "未完全爬取");
        // 导出res
        switch (viewHolder.format.getValue()) {
            case "csv":
            case "txt":
                writeToCsvOrTxt(res, viewHolder.format.getValue());
                break;
            case "geojson":
                writeToGeoJson(res);
                break;
            case "shp":
                writeToShp(res);
        }
        // 导出失败任务
        if (allFailJobs.size() > 0)
            writeToFailJobsTxt(allFailJobs);
    }

    private POI retry(Runnable job, int count) {
        if (count == 0) {
            appendMessage("重试失败！");
            return null;
        }
        appendMessage("重试中...");
        Future<POI> future = (Future<POI>) executorService.submit(job);
        try {
            POI item = future.get(20, TimeUnit.SECONDS);
            return item;
        } catch (TimeoutException | InterruptedException | ExecutionException ignore) {
            appendMessage("TIMEOUT!!!");
            return retry(job, --count);
        }
    }

    private Map<String, Object> getPoi(Queue<String> keys, double[] boundary, POI initPoi, String keywords, String types) {
        List<POI.Info> res = new ArrayList<>(Arrays.asList(initPoi.getPois()));
        int total = initPoi.getCount();

        int size = 20; // 每页个数
        double left = boundary[0], bottom = boundary[1], right = boundary[2], top = boundary[3];
        String polygon = left + "," + top + "|" + right + "," + bottom;
        int taskNum = total / size + 1;

        // 添加任务
        List<Future<POI>> futures = new ArrayList<>();
        List<POIJob> jobs = new ArrayList<>();
        for (int page = 2; page <= taskNum; page++) {
            int finalPage = page;
            POIJob job = new POIJob(polygon, keywords, types, finalPage, size);
            Future<POI> future = executorService.submit(() ->
                    getPoi(keys, polygon, keywords, types, finalPage, size));
            futures.add(future);
            jobs.add(job);
        }

        // 阻塞获取
        List<POIJob> failJobs = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                POI item = futures.get(i).get(20, TimeUnit.SECONDS);
                if (item != null) {
                    if (item.getStatus() == -1) {
                        // 说明剩余的任务都将失败
                        for (int j = i; j < jobs.size(); j++) {
                            failJobs.add(jobs.get(j));
                        }
                        break;
                    } else {
                        res.addAll(Arrays.asList(item.getPois()));
                    }
                } else {
                    failJobs.add(jobs.get(i));
                }
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                failJobs.add(jobs.get(i));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", res);
        result.put("failJobs", failJobs);
        return result;
    }

    private synchronized String getAMapKey(Queue<String> keys) {
        if (keys.isEmpty()) {
            return null;
        }
        String key = keys.poll();
        keys.offer(key);
        return key;
    }

    private POI getPoi(Queue<String> keys, String polygon, String keywords, String types, int page, int size) {
        if (!hasStart) return new POI(-1);
        long startTime = System.currentTimeMillis();   //获取开始时间
        String key = getAMapKey(keys);
        if (key == null) {
            appendMessage("key池已耗尽，无法继续获取POI...");
            return new POI(-1); // -1 表示由于 key 池耗尽，无法继续获取POI
        }
        POI poi = mapDao.getPoi(key, polygon, keywords, types, "base", page, size);
        if (poi == null || !"10000".equals(poi.getInfocode())) {
            synchronized (this) {
                if (keys.contains(key)) {
                    if (poi == null) {
                        appendMessage("数据获取失败");
                        appendMessage("错误数据---" + keywords + "--" + types + "--" + page + "--" + size);
                    } else {
                        if ("10001".equals(poi.getInfocode())) {
                            appendMessage("key----" + key + "已经过期");
                        } else if ("10003".equals(poi.getInfocode())) {
                            appendMessage("key----" + key + "已达调用量上限");
                        } else {
                            appendMessage("错误代码：" + poi.getInfocode() + "详细信息：" + poi.getInfo());
                        }
                        // 去除无效的，使用其它key重新访问
                        keys.remove(key);
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis(); //获取结束时间
        if (endTime - startTime < dataHolder.perExecuteTime) { // 严格控制每次执行perExecuteTime
            try {
                TimeUnit.MILLISECONDS.sleep(dataHolder.perExecuteTime - (endTime - startTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (poi != null && "10000".equals(poi.getInfocode())) ? poi : null;
    }

    private String filename(String format) {
        String filename = viewHolder.outputDirectory.getText();
        switch (dataHolder.tab) {
            case "行政区":
                filename = filename + "/解析结果_" + viewHolder.adCode.getText() + (!viewHolder.types.getText().isEmpty() ? "types_" + viewHolder.types.getText() : "") + (!viewHolder.keywords.getText().isEmpty() ? "keywords_" + viewHolder.keywords.getText() : "");
                break;
            case "矩形":
                filename = filename + "/解析结果_" + viewHolder.rectangle.getText() + (!viewHolder.types.getText().isEmpty() ? "types_" + viewHolder.types.getText() : "") + (!viewHolder.keywords.getText().isEmpty() ? "keywords_" + viewHolder.keywords.getText() : "");
                break;
            case "自定义":
                filename = filename + "/解析结果_" + FileUtil.getFileName(viewHolder.userFile.getText()) + (!viewHolder.types.getText().isEmpty() ? "types_" + viewHolder.types.getText() : "") + (!viewHolder.keywords.getText().isEmpty() ? "keywords_" + viewHolder.keywords.getText() : "");
                break;
        }
        return filename.length() > 200 ? filename.substring(0, 200) + "等." + format : filename + "." + format;
    }

    private void writeToFailJobsTxt(List<POIJob> allFailJobs) {
        if (!hasStart) return;
        // 准备失败网格文件
        String filename = viewHolder.outputDirectory.getText();
        File failJobsFile = FileUtil.getNewFile(filename + "/fail_jobs.csv");
        if (failJobsFile == null) {
            appendMessage("输出路径有误，请检查后重试！");
            return;
        }
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(failJobsFile));
            writer.writeNext(new String[]{"polygon", "types", "keywords", "page", "size"});
            for (POIJob item : allFailJobs) {
                String[] entries = {item.polygon, item.types, item.keywords, String.valueOf(item.page), String.valueOf(item.size)};
                writer.writeNext(entries);
            }
            writer.close();
            appendMessage("失败任务写入成功，结果存储于" + failJobsFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            appendMessage("写入失败");
        }
    }

    private void writeToCsvOrTxt(List<POI.Info> res, String format) {
        if (!hasStart) return;
        String filename = filename(format);
        File csvFile = FileUtil.getNewFile(filename);
        if (csvFile == null) {
            appendMessage("输出路径有误，请检查后重试！");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            if (format.equals("csv"))
                writer.write('\ufeff');
            writer.write("name,type,typecode,address,tel,pname,cityname,adname,gcj02_lon,gcj02_lat,wgs84_lon,wgs84_lat\r\n");
            for (POI.Info info : res) {
                String[] lonlat = info.location.toString().split(",");
                if (lonlat.length == 2) {
                    double[] wgs84 = CoordinateTransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                    writer.write("\"" + info.name + "\"," + "\"" + info.type + "\"," + "\"" + info.typecode + "\"," + "\"" + info.address + "\"," + "\"" + info.tel + "\"," + "\"" + info.pname + "\"," + "\"" + info.cityname + "\"," + "\"" + info.adname + "\"," + "\"" + lonlat[0] + "\"," + "\"" + lonlat[1] + "\"," + "\"" + wgs84[0] + "\"," + "\"" + wgs84[1] + "\"\r\n");
                }
            }
            appendMessage("写入成功，结果存储于" + csvFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }

    private void writeToGeoJson(List<POI.Info> res) {
        if (!hasStart) return;
        String filename = filename(".json");
        GeoJSON geoJSON = parseResult(res);
        File jsonFile = FileUtil.getNewFile(filename);
        if (jsonFile == null) {
            appendMessage("输出路径有误，请检查后重试！");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            writer.write(geoJSON.toString());
            appendMessage("写入成功，结果存储于" + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }


    private void writeToShp(List<POI.Info> res) {
        if (!hasStart) return;
        String filename = filename(".shp");
        appendMessage("正在写入数据，请等待");
        try {
            final SimpleFeatureType type =
                    DataUtilities.createType(
                            "Location",
                            "the_geom:Point:srid=4326,name:String,type:String,typecode:String,address:String,tel:String,pname:String,cityname:String,adname:String,gcj02_lon:String,gcj02_lat:String,wgs84_lon:String,wgs84_lat:String"
                    );
            List<SimpleFeature> features = new ArrayList<>();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
            for (POI.Info info : res) {
                String[] lonlat = info.location.toString().split(",");
                if (lonlat.length == 2) {
                    double[] wgs84 = CoordinateTransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                    Point point = geometryFactory.createPoint(new Coordinate(wgs84[0], wgs84[1]));
                    featureBuilder.add(point);
                    featureBuilder.add(info.name);
                    featureBuilder.add(info.type);
                    featureBuilder.add(info.typecode);
                    featureBuilder.add(info.address != null ? info.address.toString() : "");
                    featureBuilder.add(info.tel != null ? info.tel.toString() : "");
                    featureBuilder.add(info.pname != null ? info.pname.toString() : "");
                    featureBuilder.add(info.cityname != null ? info.cityname.toString() : "");
                    featureBuilder.add(info.adname != null ? info.adname.toString() : "");
                    featureBuilder.add(String.valueOf(lonlat[0]));
                    featureBuilder.add(String.valueOf(lonlat[1]));
                    featureBuilder.add(String.valueOf(wgs84[0]));
                    featureBuilder.add(String.valueOf(wgs84[1]));
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    features.add(feature);
                }
            }
            File shpFile = FileUtil.getNewFile(filename);
            if (shpFile == null) {
                appendMessage("文件无法创建，写入失败！");
                return;
            }
            if (SpatialDataTransformUtil.saveFeaturesToShp(features, type, shpFile.getAbsolutePath())) {
                appendMessage("写入成功，结果存储于" + shpFile.getAbsolutePath());
            } else appendMessage("写入失败");
        } catch (SchemaException e) {
            e.printStackTrace();
            appendMessage("写入失败");
        }
    }

    private GeoJSON parseResult(List<POI.Info> res) {
        List<Feature> features = new ArrayList<>();
        for (POI.Info info : res) {
            if (info.location == null)
                continue;
            String[] lonlat = info.location.toString().split(",");
            if (lonlat.length == 2) {
                double[] wgs84 = CoordinateTransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                JsonObject geometry = new JsonObject();
                geometry.addProperty("type", "Point");
                JsonArray coordinates = new JsonArray();
                coordinates.add(wgs84[0]);
                coordinates.add(wgs84[1]);
                geometry.add("coordinates", coordinates);
                Feature feature = new Feature(geometry.toString());
                feature.addProperty("name", info.name);
                feature.addProperty("type", info.type);
                feature.addProperty("typecode", info.typecode);
                if (info.tel != null)
                    feature.addProperty("tel", info.tel.toString());
                else feature.addProperty("tel", "");
                if (info.address != null)
                    feature.addProperty("address", info.address.toString());
                else feature.addProperty("address", "");
                if (info.pname != null)
                    feature.addProperty("pname", info.pname.toString());
                else feature.addProperty("pname", "");
                if (info.cityname != null)
                    feature.addProperty("cityname", info.cityname.toString());
                else feature.addProperty("cityname", "");
                if (info.adname != null)
                    feature.addProperty("adname", info.adname.toString());
                else feature.addProperty("adname", "");
                feature.addProperty("gcj02_lon", lonlat[0]);
                feature.addProperty("gcj02_lat", lonlat[1]);
                feature.addProperty("wgs84_lon", String.valueOf(wgs84[0]));
                feature.addProperty("wgs84_lat", String.valueOf(wgs84[1]));
                features.add(feature);
            }
        }
        return new GeoJSON(features);
    }


    private boolean parseRect(String text) {
        String pattern = "^(-?\\d{1,3}(\\.\\d+)?),\\s?(-?\\d{1,3}(\\.\\d+)?)#(-?\\d{1,3}(\\.\\d+)?),\\s?(-?\\d{1,3}(\\.\\d+)?)$";
        return Pattern.matches(pattern, text);
    }

    private void clearMessage() {
        Platform.runLater(() -> viewHolder.messageDetail.clear());
    }

    private void appendMessage(String text) {
        Platform.runLater(() -> viewHolder.messageDetail.appendText(text + "\r\n"));
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
}
