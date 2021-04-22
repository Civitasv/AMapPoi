package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.model.Feature;
import com.civitasv.spider.model.GeoJSON;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.POI;
import com.civitasv.spider.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class POIController {
    public TextField threadNum;
    public TextField keywords;
    public TextArea keys;
    public TextField types;
    public TextField city;
    public TextField rectangle;
    public TextField grids;
    public TextField threshold;
    public ChoiceBox<String> format;
    public TextField outputDirectory;
    public TextArea messageDetail;
    public TextField userFile;
    public TabPane tabs;
    public Button directoryBtn;
    public Button execute;
    public Button poiType;
    private final AMapDao mapDao = new AMapDaoImpl();
    private ExecutorService worker, executorService;

    public void init() {
        this.threadNum.setTextFormatter(getFormatter());
        this.grids.setTextFormatter(getFormatter());
        this.threshold.setTextFormatter(getFormatter());
    }

    private TextFormatter<Integer> getFormatter() {
        return new TextFormatter<>(
                c -> Pattern.matches("\\d*", c.getText()) ? c : null);
    }

    public void openPOITypes() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://lbs.amap.com/api/webservice/download"));
    }

    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(MainApplication.getScene().getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    public void execute() {
        messageDetail.clear();
        analysis(true);
        if (keys.getText().isEmpty()) {
            // keys为空
            MessageUtil.alert(Alert.AlertType.ERROR, "高德key", null, "高德key池不能为空！");
            analysis(false);
            return;
        }
        if (keywords.getText().isEmpty() && types.getText().isEmpty()) {
            // 关键字和类型均为空
            MessageUtil.alert(Alert.AlertType.ERROR, "参数设置", null, "POI关键字和类型两者至少必填其一！");
            analysis(false);
            return;
        }
        if (grids.getText().isEmpty()) {
            // 输出文件夹为空
            MessageUtil.alert(Alert.AlertType.ERROR, "初始网格", null, "初始网格不能为空！");
            analysis(false);
            return;
        }
        if (threadNum.getText().isEmpty()) {
            // 输出文件夹为空
            MessageUtil.alert(Alert.AlertType.ERROR, "线程数目", null, "线程数目不能为空！");
            analysis(false);
            return;
        }
        if (threshold.getText().isEmpty()) {
            // 输出文件夹为空
            MessageUtil.alert(Alert.AlertType.ERROR, "阈值", null, "阈值不能为空！");
            analysis(false);
            return;
        }
        if (outputDirectory.getText().isEmpty()) {
            // 输出文件夹为空
            MessageUtil.alert(Alert.AlertType.ERROR, "输出文件夹", null, "输出文件夹不能为空！");
            analysis(false);
            return;
        }
        appendMessage("读取线程个数中");
        int threadNum = Integer.parseInt(this.threadNum.getText());
        appendMessage("读取成功");
        appendMessage("读取初始网格数中");
        int grids = Integer.parseInt(this.grids.getText());
        appendMessage("读取成功");
        appendMessage("读取阈值中");
        int threshold = Integer.parseInt(this.threshold.getText());
        appendMessage("读取成功");
        List<String> keys = new ArrayList<>(Arrays.asList(this.keys.getText().split(",")));
        appendMessage("读取POI关键字中");
        StringBuilder keywords = new StringBuilder();
        String[] keywordArr = this.keywords.getText().split(",");
        for (int i = 0; i < keywordArr.length; i++) {
            keywords.append(keywordArr[i]);
            if (i != keywordArr.length - 1)
                keywords.append("|");
        }
        appendMessage("读取成功");
        appendMessage("读取POI类型中");
        StringBuilder types = new StringBuilder();
        String[] typeArr = this.types.getText().split(",");
        for (int i = 0; i < typeArr.length; i++) {
            types.append(typeArr[i]);
            if (i != typeArr.length - 1)
                types.append("|");
        }
        appendMessage("读取成功");
        double[] boundary;
        switch (tabs.getSelectionModel().getSelectedItem().getText()) {
            case "行政区":
                if (city.getText().isEmpty()) {
                    // 行政区为空
                    MessageUtil.alert(Alert.AlertType.ERROR, "行政区代码", null, "请设置行政区代码！");
                    analysis(false);
                    return;
                }
                appendMessage("获取行政区 " + city.getText() + " 区域边界中");
                boundary = getBoundaryByAdCode(city.getText());
                if (boundary == null) {
                    MessageUtil.alert(Alert.AlertType.ERROR, "行政区边界", null, "无法获取行政区边界，请检查行政区代码或稍后重试！");
                    analysis(false);
                    return;
                }
                appendMessage("成功获取行政区 " + city.getText() + " 区域边界");
                getPoiDataByRectangle(boundary, grids, threadNum, threshold, keywords.toString(), types.toString(), keys, tabs.getSelectionModel().getSelectedItem().getText());
                break;
            case "矩形":
                if (rectangle.getText().isEmpty()) {
                    // 行政区为空
                    MessageUtil.alert(Alert.AlertType.ERROR, "矩形", null, "请设置矩形范围！");
                    analysis(false);
                    return;
                }
                appendMessage("解析矩形区域中");
                boundary = getBoundaryByRectangle(rectangle.getText());
                if (boundary == null) {
                    MessageUtil.alert(Alert.AlertType.ERROR, "矩形", null, "无法获取矩形边界，请检查矩形格式或稍后重试！");
                    analysis(false);
                    return;
                }
                appendMessage("解析矩形区域成功");
                getPoiDataByRectangle(boundary, grids, threadNum, threshold, keywords.toString(), types.toString(), keys, tabs.getSelectionModel().getSelectedItem().getText());
                break;
            case "自定义":
                if (userFile.getText().isEmpty()) {
                    // 行政区为空
                    MessageUtil.alert(Alert.AlertType.ERROR, "自定义", null, "请设置geojson文件路径！");
                    analysis(false);
                    return;
                }
                appendMessage("解析用户geojson文件中");
                boundary = getBoundaryByUserFile(userFile.getText());
                if (boundary == null) {
                    MessageUtil.alert(Alert.AlertType.ERROR, "自定义", null, "无法获取边界，请检查GeoJSON格式或稍后重试！");
                    analysis(false);
                    return;
                }
                appendMessage("成功解析用户文件");
                getPoiDataByRectangle(boundary, grids, threadNum, threshold, keywords.toString(), types.toString(), keys, tabs.getSelectionModel().getSelectedItem().getText());
                break;
        }
    }


    private void analysis(boolean isAnalysis) {
        execute.setDisable(isAnalysis);
        threadNum.setDisable(isAnalysis);
        keys.setDisable(isAnalysis);
        keywords.setDisable(isAnalysis);
        types.setDisable(isAnalysis);
        tabs.setDisable(isAnalysis);
        grids.setDisable(isAnalysis);
        threshold.setDisable(isAnalysis);
        format.setDisable(isAnalysis);
        outputDirectory.setDisable(isAnalysis);
        directoryBtn.setDisable(isAnalysis);
        poiType.setDisable(isAnalysis);
        appendMessage(isAnalysis ? "开始POI爬取，请勿操作" : "停止POI爬取");
    }

    public void cancel() {
        // 停止解析
        if (executorService != null)
            executorService.shutdownNow();
        if (worker != null)
            worker.shutdownNow();
        analysis(false);
        messageDetail.clear();
    }


    public void chooseAdCode() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("http://www.mca.gov.cn//article/sj/xzqh/2020/202006/202008310601.shtml"));
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("geojson", "*.json")
        );
        File file = fileChooser.showOpenDialog(MainApplication.getScene().getWindow());
        if (file != null)
            userFile.setText(file.getAbsolutePath());
    }

    private double[] getBoundaryByAdCode(String adCode) {
        return AMapPoiUtil.getBoundary(adCode);
    }

    private double[] getBoundaryByUserFile(String path) {
        return AMapPoiUtil.getBoundaryByGeoJson(FileUtil.readFile(path));
    }

    private double[] getBoundaryByRectangle(String text) {
        String[] str = text.split("#");
        if (str.length == 2) {
            String[] leftTop = str[0].split(",");
            String[] rightBottom = str[1].split(",");
            if (leftTop.length == 2 && rightBottom.length == 2) {
                try {
                    return new double[]{Double.parseDouble(leftTop[0]), Double.parseDouble(rightBottom[1]), Double.parseDouble(rightBottom[0]), Double.parseDouble(leftTop[1])};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void getPoiDataByRectangle(double[] boundary, int grids, int threadNum, int threshold, String keywords, String types, List<String> keys, String tab) {
        List<POI.Info> res = new ArrayList<>();
        // 1. 获取边界
        double left = boundary[0], bottom = boundary[1], right = boundary[2], top = boundary[3];
        double itemWidth = (right - left) / grids;
        double itemHeight = (top - bottom) / grids;
        // 2. 获取初始切分网格
        Deque<double[]> analysisGrid = new ArrayDeque<>(); // 网格剖分
        appendMessage("切分初始网格中");
        for (int i = 0; i < grids; i++) {
            for (int j = 0; j < grids; j++) {
                analysisGrid.push(new double[]{left + i * itemWidth, bottom + j * itemHeight, left + (i + 1) * itemWidth, bottom + (j + 1) * itemHeight});
            }
        }
        appendMessage("初始网格切分成功");
        // 3. 开始爬取
        appendMessage("开始POI爬取，" + (keywords.isEmpty() ? "POI关键字：" + keywords : "") + (types.isEmpty() ? (" POI类型：" + types) : ""));
        worker = Executors.newSingleThreadExecutor();
        executorService = Executors.newFixedThreadPool(threadNum);
        worker.execute(() -> {
            boolean success = true;
            while (!analysisGrid.isEmpty()) {
                appendMessage("正在爬取，任务队列剩余" + analysisGrid.size() + "个");
                List<POI.Info> item = getPoi(analysisGrid.pop(), threadNum, threshold, keywords, types, keys, analysisGrid);
                if (item == null) {
                    success = false;
                    break;
                }
                if (item.size() > 0)
                    res.addAll(item);
            }
            appendMessage(success ? "POI爬取完毕" : "未完全爬取");
            // 导出res
            // 导出res
            switch (format.getValue()) {
                case "csv":
                case "txt":
                    writeToCsvOrTxt(res, format.getValue(), tab);
                    break;
                case "geojson":
                    writeToGeoJson(res, tab);
                    break;
            }
            executorService.shutdown();
            worker.shutdown();
            analysis(false);
        });
    }

    private List<POI.Info> getPoi(double[] boundary, int threadNum, int threshold, String keywords, String types, List<String> keys, Deque<double[]> analysisGrid) {
        List<POI.Info> res = new ArrayList<>();
        int page = 1, size = 20; // 页码、每页个数
        double left = boundary[0], bottom = boundary[1], right = boundary[2], top = boundary[3];
        String polygon = left + "," + top + "|" + right + "," + bottom;
        POI poi = getPoi(polygon, keywords, types, page, size, keys); // 访问第一页
        if (poi == null) return null;
        if (poi.getCount() == 0)
            return res;
        if (poi.getCount() > threshold) { // 第一页用于验证是否超过阈值
            appendMessage("超出阈值，继续四分");
            // 继续四分
            double itemWidth = (right - left) / 2;
            double itemHeight = (top - bottom) / 2;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    analysisGrid.push(new double[]{left + i * itemWidth, bottom + j * itemHeight, left + (i + 1) * itemWidth, bottom + (j + 1) * itemHeight});
                }
            }
            appendMessage("四分完成");
            return res;
        }
        res.addAll(Arrays.asList(poi.getPois()));
        int total = poi.getCount();
        for (int i = 2; i <= total / size + 1; i += threadNum) {
            List<Callable<POI>> call = new ArrayList<>();
            for (int j = 0; j < threadNum && (i + j) <= total / size + 1; j++) {
                int finalPage = i + j;
                call.add(() -> getPoi(polygon, keywords, types, finalPage, size, keys));
            }
            try {
                List<Future<POI>> futures = executorService.invokeAll(call);
                for (Future<POI> future : futures) {
                    POI item = future.get();
                    if (item != null) res.addAll(Arrays.asList(item.getPois()));
                }
            } catch (InterruptedException | ExecutionException e) {
                appendMessage("爬取线程已中断");
            }
        }
        return res;
    }

    private void writeToCsvOrTxt(List<POI.Info> res, String format, String tab) {
        String filename = outputDirectory.getText();
        switch (tab) {
            case "行政区":
                filename = filename + "/解析结果_" + city.getText() + "types_" + types.getText() + "keywords_" + keywords.getText() + "." + format;
                break;
            case "矩形":
                filename = filename + "/解析结果_" + rectangle.getText() + "types_" + types.getText() + "keywords_" + keywords.getText() + "." + format;
                break;
            case "自定义":
                filename = filename + "/解析结果_" + FileUtil.getFileName(userFile.getText()) + "types_" + types.getText() + "keywords_" + "." + keywords.getText() + format;
                break;
        }
        File jsonFile = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            if (format.equals("csv"))
                writer.write('\ufeff');
            writer.write("name,type,typecode,address,pname,cityname,adname,gcj02_lon,gcj02_lat,wgs84_lon,wgs84_lat\r\n");
            for (POI.Info info : res) {
                String[] lonlat = info.location.toString().split(",");
                if (lonlat.length == 2) {
                    double[] wgs84 = TransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                    writer.write(info.name + "," + info.type + "," + info.typecode + "," + info.address + "," + info.pname + "," + info.cityname + "," + info.adname + "," + lonlat[0] + "," + lonlat[1] + "," + wgs84[0] + "," + wgs84[1] + "\r\n");
                }
            }
            appendMessage("写入成功，结果存储于" + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }

    private void writeToGeoJson(List<POI.Info> res, String tab) {
        String filename = outputDirectory.getText();
        switch (tab) {
            case "行政区":
                filename = filename + "/解析结果_" + city.getText() + "types_" + types.getText() + "keywords_" + keywords.getText() + ".json";
                break;
            case "矩形":
                filename = filename + "/解析结果_" + rectangle.getText() + "types_" + types.getText() + "keywords_" + keywords.getText() + ".json";
                break;
            case "自定义":
                filename = filename + "/解析结果_" + FileUtil.getFileName(userFile.getText()) + "types_" + types.getText() + "keywords_" + keywords.getText() + ".json";
                break;
        }
        List<Feature> features = new ArrayList<>();
        for (POI.Info info : res) {
            if (info.location == null)
                continue;
            String[] lonlat = info.location.toString().split(",");
            if (lonlat.length == 2) {
                double[] wgs84 = TransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
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
                if (info.address != null)
                    feature.addProperty("address", info.address.toString());
                if (info.pname != null)
                    feature.addProperty("pname", info.pname.toString());
                if (info.cityname != null)
                    feature.addProperty("cityname", info.cityname.toString());
                if (info.adname != null)
                    feature.addProperty("adname", info.adname.toString());
                feature.addProperty("gcj02_lon", lonlat[0]);
                feature.addProperty("gcj02_lat", lonlat[1]);
                feature.addProperty("wgs84_lon", String.valueOf(wgs84[0]));
                feature.addProperty("wgs84_lat", String.valueOf(wgs84[1]));
                features.add(feature);
            }
        }
        GeoJSON geoJSON = new GeoJSON(features);
        File jsonFile = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            writer.write(geoJSON.toString());
            appendMessage("写入成功，结果存储于" + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }

    private POI getPoi(String polygon, String keywords, String types, int page, int size, List<String> keys) {
        if (keys.isEmpty()) {
            appendMessage("key池已耗尽，无法继续获取POI...");
            return null;
        }
        int index = (int) (Math.random() * keys.size());
        POI poi = mapDao.getPoi(keys.get(index), polygon, keywords, types, page, size);
        if ("10001".equals(poi.getInfocode()) || "10003".equals(poi.getInfocode())) {
            synchronized (this) {
                if ("10001".equals(poi.getInfocode())) {
                    appendMessage("key----" + keys.get(index) + "已经过期");
                }
                if ("10003".equals(poi.getInfocode())) {
                    appendMessage("key----" + keys.get(index) + "已达调用量上限");
                }
                // 去除过期的，使用其它key重新访问
                keys.remove(index);
                while (!keys.isEmpty()) {
                    appendMessage("正在尝试其它key");
                    index = (int) (Math.random() * keys.size());
                    String key = keys.get(index);
                    poi = mapDao.getPoi(key, polygon, keywords, types, page, size);
                    if ("10000".equals(poi.getInfocode())) {
                        appendMessage("切换key成功");
                        break;
                    } else {
                        keys.remove(index);
                    }
                }
                if (keys.isEmpty()) {
                    appendMessage("key池已耗尽，无法继续获取POI...");
                    return null;
                }
            }
        }
        return "10000".equals(poi.getInfocode()) ? poi : null;
    }

    private void appendMessage(String text) {
        Platform.runLater(() -> messageDetail.appendText(text + "\r\n"));
    }

    public void openGeocoding() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("geocoding.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("地理编码");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        stage.show();
    }
}
