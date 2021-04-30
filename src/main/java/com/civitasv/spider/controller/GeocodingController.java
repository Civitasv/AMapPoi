package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class GeocodingController {
    private static Scene scene;
    // 输入文件
    public TextField inputFile;
    // 输出文件夹
    public TextField outputDirectory;
    // 输出格式
    public ChoiceBox<String> format;

    private final AMapDao aMapDao = new AMapDaoImpl();

    // 自定义高德key
    public TextArea keys;
    // 线程数目
    public TextField threadNum;
    // 面板
    public VBox main;
    // 信息
    public TextArea messageDetail;
    // 取消按钮，执行按钮
    public Button cancel, execute;
    public ChoiceBox<String> userType;
    private ExecutorService worker, executorService;
    private MyProgressBar progressBar;
    private boolean start = false;
    private int perExecuteTime;

    public void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("geocoding.fxml"));
        Parent root = fxmlLoader.load();
        GeocodingController controller = fxmlLoader.getController();
        controller.init();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("地理编码");
        scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        stage.show();
    }

    private void init() {
        TextFormatter<Integer> formatter = new TextFormatter<>(
                new IntegerStringConverter(),
                4,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null);
        this.threadNum.setTextFormatter(formatter);
    }

    /**
     * 执行地址解析
     */
    public void execute() {
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            Platform.runLater(() -> messageDetail.clear());
            if (!check()) {
                return;
            }
            analysis(true);
            start = true;

            appendMessage("读取高德key中");
            Queue<String> keys = new ArrayDeque<>(Arrays.asList(this.keys.getText().split(",")));
            appendMessage("高德key读取成功");

            appendMessage("读取线程数目中");
            Integer threadNum = ParseUtil.tryParse(this.threadNum.getText());
            if (threadNum == null) {
                appendMessage("解析线程数目失败，请检查！");
                analysis(false);
                return;
            }
            appendMessage("线程数目读取成功");

            // 读取开发者类型
            int qps = 0;
            appendMessage("您是" + userType.getValue());
            switch (userType.getValue()) {
                case "个人开发者":
                    qps = 100;
                    break;
                case "个人认证开发者":
                    qps = 200;
                    break;
                case "企业开发者":
                    qps = 1000;
                    break;
            }
            if (threadNum > qps * keys.size()) {
                int val = qps * keys.size();
                appendMessage(userType.getValue() + "线程数不能超过" + val);
                threadNum = val;
                appendMessage("设置线程数目为" + threadNum);
            }
            perExecuteTime = getPerExecuteTime(threadNum, qps, keys.size());

            // 解析输入文件
            appendMessage("解析输入文件中");
            List<Map<String, String>> parseRes = ParseUtil.parseTxtOrCsv(inputFile.getText());
            if (parseRes == null) {
                appendMessage("输入文件解析失败，请检查文件");
                analysis(false);
                return;
            }

            // 解析地址
            if (parseRes.size() > 0) {
                if (!parseRes.get(0).containsKey("address")) {
                    Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "解析", null, "输入文件不含address关键字，请修改后重试！"));
                    analysis(false);
                    return;
                }
                progressBar = new MyProgressBar(messageDetail, 40, parseRes.size() - 1, "#", "-");
                // 获取输出格式
                String outputFormat = format.getValue();
                switch (outputFormat) {
                    case "csv":
                    case "txt":
                        saveToCsvOrTxt(parseRes, outputFormat, threadNum, keys);
                        break;
                    case "json":
                        saveToJson(parseRes, threadNum, keys);
                        break;
                }
                analysis(false);
            } else {
                Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "解析", null, "输入文件为空或解析失败，请检查后重试！"));
                analysis(false);
            }
        });
    }

    /**
     * 根据线程数、key数目和QPS设置每次运行时间
     *
     * @param threadNum 线程数目
     * @param qps       用户qps
     * @param keysNum   key数量
     * @return 每次运行时间 ms
     */
    private int getPerExecuteTime(int threadNum, int qps, int keysNum) {
        return (int) (1000 * (threadNum * 1.0 / (qps * keysNum)));
    }

    /**
     * 选择文件
     */
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("csv", "*.csv"),
                new FileChooser.ExtensionFilter("txt", "*.txt")
        );
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null)
            inputFile.setText(file.getAbsolutePath());
    }

    /**
     * 选择文件夹
     */
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(scene.getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    private void saveToCsvOrTxt(@NotNull List<Map<String, String>> parseRes, String outputFormat, int threadNum, Queue<String> amapKeys) {
        // 创建工作线程执行保存文件工作
        executorService = Executors.newFixedThreadPool(threadNum);
        // 创建线程池执行解析工作
        job:
        for (int i = 0; start && i < parseRes.size(); i += threadNum) {
            List<Callable<Geocodes.Response>> call = new ArrayList<>();
            for (int j = 0; j < threadNum && i + j < parseRes.size(); j++) {
                Map<String, String> item = parseRes.get(i + j);
                if (item.containsKey("address") && item.containsKey("city")) {
                    call.add(() -> geocode(item.get("address"), item.get("city"), amapKeys));
                } else if (item.containsKey("address")) {
                    call.add(() -> geocode(item.get("address"), "", amapKeys));
                }
            }
            try {
                List<Future<Geocodes.Response>> futures = executorService.invokeAll(call);
                for (int j = 0; j < futures.size(); j++) {
                    Future<Geocodes.Response> future = futures.get(j);
                    Geocodes.Response response = future.get();
                    if (response == null) {
                        break job;
                    }
                    Map<String, String> item = parseRes.get(i + j);
                    // 更新解析进度
                    int finalI = i, finalJ = j;
                    Platform.runLater(() -> progressBar.show(finalI + finalJ));
                    item.put("status", response.getStatus().toString());
                    item.put("info", response.getInfo());
                    item.put("infocode", response.getInfocode());
                    Geocodes.Info[] infos = response.getGeocodes();
                    for (int k = 0; k < infos.length; k++) {
                        Geocodes.Info info = infos[k];
                        item.put("formatted_address_" + k, info.formattedAddress);
                        item.put("country_" + k, info.country);
                        item.put("province_" + k, info.province);
                        item.put("city_" + k, info.city);
                        item.put("citycode_" + k, info.cityCode);
                        item.put("district_" + k, info.district != null ? info.district.toString() : "");
                        item.put("adcode_" + k, info.adCode != null ? info.adCode : "");
                        item.put("street_" + k, info.street != null ? info.street.toString() : "");
                        item.put("number_" + k, info.number != null ? info.number.toString() : "");
                        item.put("level_" + k, info.level);
                        String[] lonlat = info.location.split(",");
                        if (lonlat.length == 2) {
                            item.put("gcj02_lon_" + k, lonlat[0]);
                            item.put("gcj02_lat_" + k, lonlat[1]);
                            double[] wgs84 = CoordinateTransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                            item.put("wgs84_lon_" + k, String.valueOf(wgs84[0]));
                            item.put("wgs84_lat_" + k, String.valueOf(wgs84[1]));
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                appendMessage("爬取线程已中断");
            }
        }
        executorService.shutdown();
        if (!start) return;
        List<String> keys = new ArrayList<>(parseRes.get(0).keySet());
        File file = FileUtil.getNewFile(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + "." + outputFormat);
        if (file == null) {
            appendMessage("输出路径有误，请检查后重试！");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            if (outputFormat.equals("csv"))
                writer.write('\ufeff');
            for (int i = 0; i < keys.size(); i++) {
                writer.write(keys.get(i));
                if (i != keys.size() - 1)
                    writer.write(",");
            }
            writer.write("\r\n");
            parseRes.forEach(item -> {
                try {
                    for (int i = 0; i < keys.size(); i++) {
                        writer.write("\"" + item.get(keys.get(i)) + "\"");
                        if (i != keys.size() - 1)
                            writer.write(",");
                    }
                    writer.write("\r\n");
                } catch (IOException e) {
                    appendMessage("写入失败");
                    appendMessage(e.getMessage());
                }
            });
            appendMessage("写入成功，结果存储于" + file.getAbsolutePath());
        } catch (IOException e) {
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }

    private void saveToJson(@NotNull List<Map<String, String>> parseRes, int threadNum, Queue<String> amapKeys) {
        // 创建工作线程执行保存文件工作
        executorService = Executors.newFixedThreadPool(threadNum);
        // 创建线程池执行解析工作
        JsonArray jsonArray = new JsonArray();
        job:
        for (int i = 0; i < parseRes.size(); i += threadNum) {
            List<Callable<Geocodes.Response>> call = new ArrayList<>();
            for (int j = 0; j < threadNum && (i + j) < parseRes.size(); j++) {
                Map<String, String> item = parseRes.get(i + j);
                if (item.containsKey("address") && item.containsKey("city")) {
                    call.add(() -> geocode(item.get("address"), item.get("city"), amapKeys));
                } else if (item.containsKey("address")) {
                    call.add(() -> geocode(item.get("address"), "", amapKeys));
                }
            }
            try {
                long startTime = System.currentTimeMillis();   //获取开始时间
                List<Future<Geocodes.Response>> futures = executorService.invokeAll(call);
                long endTime = System.currentTimeMillis(); //获取结束时间
                if (endTime - startTime < perExecuteTime) { // 严格控制每次执行perExecuteTime
                    TimeUnit.MILLISECONDS.sleep(perExecuteTime - (endTime - startTime));
                }
                for (int j = 0; j < futures.size(); j++) {
                    Future<Geocodes.Response> future = futures.get(j);
                    Geocodes.Response response = future.get();
                    if (response == null) {
                        break job;
                    }
                    JsonObject jsonObject = new JsonObject();
                    Map<String, String> item = parseRes.get(i + j);
                    item.forEach(jsonObject::addProperty);
                    // 更新解析进度
                    int finalI = i, finalJ = j;
                    Platform.runLater(() -> progressBar.show(finalI + finalJ));
                    jsonObject.addProperty("status", response.getStatus().toString());
                    jsonObject.addProperty("info", response.getInfo());
                    jsonObject.addProperty("infocode", response.getInfocode());
                    JsonArray infoArray = new JsonArray();
                    Geocodes.Info[] infos = response.getGeocodes();
                    for (Geocodes.Info info : infos) {
                        JsonObject jsonItem = new JsonObject();
                        jsonItem.addProperty("formatted_address", info.formattedAddress);
                        jsonItem.addProperty("country", info.country);
                        jsonItem.addProperty("province", info.province);
                        jsonItem.addProperty("city", info.city);
                        jsonItem.addProperty("citycode", info.cityCode);
                        jsonItem.addProperty("district", info.district != null ? info.district.toString() : "");
                        jsonItem.addProperty("adcode", info.adCode);
                        jsonItem.addProperty("street", info.street != null ? info.street.toString() : "");
                        jsonItem.addProperty("number", info.number != null ? info.number.toString() : "");
                        jsonItem.addProperty("level", info.level);
                        String[] lonlat = info.location.split(",");
                        if (lonlat.length == 2) {
                            jsonItem.addProperty("gcj02_lon", lonlat[0]);
                            jsonItem.addProperty("gcj02_lat", lonlat[1]);
                            double[] wgs84 = CoordinateTransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
                            jsonItem.addProperty("wgs84_lon", wgs84[0]);
                            jsonItem.addProperty("wgs84_lat", wgs84[1]);
                        }
                        infoArray.add(jsonItem);
                    }
                    jsonObject.add("res", infoArray);
                    jsonArray.add(jsonObject);
                }
            } catch (InterruptedException | ExecutionException e) {
                appendMessage("爬取线程已中断");
            }
        }
        executorService.shutdown();
        String json = jsonArray.toString();
        File jsonFile = FileUtil.getNewFile(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + ".json");
        if (jsonFile == null) {
            appendMessage("输出路径有误，请检查后重试！");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
            appendMessage("正在写入数据，请等待");
            writer.write(json);
            appendMessage("写入成功，结果存储于" + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            appendMessage("写入失败");
            appendMessage(e.getMessage());
        }
    }

    private void analysis(boolean isAnalysis) {
        Platform.runLater(() -> {
            execute.setDisable(isAnalysis);
            keys.setDisable(isAnalysis);
            inputFile.setDisable(isAnalysis);
            format.setDisable(isAnalysis);
            outputDirectory.setDisable(isAnalysis);
            userType.setDisable(isAnalysis);
        });
        if (!start) return;
        start = isAnalysis;
        appendMessage(isAnalysis ? "开始地理编码，请勿操作" : "停止地理编码");
        if (!isAnalysis && executorService != null)
            executorService.shutdownNow();
        if (!isAnalysis && worker != null)
            worker.shutdownNow();
    }

    public void cancel() {
        // 停止解析
        analysis(false);
        messageDetail.clear();
    }

    private boolean check() {
        if (keys.getText().isEmpty()) {
            // keys为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "高德key", null, "高德key池不能为空！"));
            return false;
        }
        if (threadNum.getText().isEmpty()) {
            // 线程数目为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "线程数目", null, "线程数目不能为空！"));
            return false;
        }
        if (inputFile.getText().isEmpty()) {
            // 输入文件为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件不能为空！"));
            return false;
        }
        if (outputDirectory.getText().isEmpty()) {
            // 输出文件夹为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输出文件夹", null, "输出文件夹不能为空！"));
            return false;
        }
        // 获取输入文件格式
        String extension = FileUtil.getExtension(inputFile.getText());
        if (extension != null) {
            extension = extension.toLowerCase(Locale.ROOT);
            if (!extension.equals("csv") && !extension.equals("txt")) {
                Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择csv或txt格式文件！"));
                return false;
            }
        } else {
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择csv或txt格式文件！"));
            return false;
        }
        return true;
    }

    private void appendMessage(String text) {
        Platform.runLater(() -> messageDetail.appendText(text + "\r\n"));
    }

    private synchronized String getKey(Queue<String> keys) {
        if (keys.isEmpty()) {
            return null;
        }
        String key = keys.poll();
        keys.offer(key);
        return key;
    }

    private Geocodes.Response geocode(String address, String city, Queue<String> keys) {
        if (!start) return null;
        String key = getKey(keys);
        if (key == null) {
            appendMessage("key池已耗尽，无法继续获取POI...");
            return null;
        }
        Geocodes.Response response = aMapDao.geocoding(key, address, city);
        if (start && (response == null || !"10000".equals(response.getInfocode()))) {
            synchronized (this) {
                if (response == null) {
                    // 如果返回null，重试
                    appendMessage("数据获取失败，正在重试中...");
                    for (int i = 0; i < 3; i++) {
                        appendMessage("重试第" + (i + 1) + "次...");
                        response = aMapDao.geocoding(key, address, city);
                        if (response != null && "10000".equals(response.getInfocode())) {
                            appendMessage("数据获取成功，继续爬取...");
                            return response;
                        }
                    }
                }
                if (response == null) {
                    appendMessage("数据获取失败");
                    appendMessage("错误数据---" + address + "--" + city);
                }else{
                    if ("10001".equals(response.getInfocode())) {
                        appendMessage("key----" + key + "已经过期");
                    } else if ("10003".equals(response.getInfocode())) {
                        appendMessage("key----" + key + "已达调用量上限");
                    } else {
                        appendMessage("错误代码：" + response.getInfocode() + "详细信息：" + response.getInfo());
                    }
                }
                // 去除过期的，使用其它key重新访问
                keys.poll();
                while (!keys.isEmpty()) {
                    appendMessage("正在尝试其它key");
                    key = getKey(keys);
                    appendMessage("切换key：" + key);
                    response = aMapDao.geocoding(key, address, city);

                    if (response == null) {
                        // 如果返回null，重试
                        appendMessage("数据获取失败，正在重试中...");
                        for (int i = 0; i < 3; i++) {
                            appendMessage("重试第" + (i + 1) + "次...");
                            response = aMapDao.geocoding(key, address, city);
                            if (response != null && "10000".equals(response.getInfocode())) {
                                appendMessage("数据获取成功，继续爬取...");
                                return response;
                            }
                        }
                    }
                    if (response == null) {
                        appendMessage("数据获取失败");
                        appendMessage("错误数据---" + address + "--" + city);
                    }else{
                        if ("10001".equals(response.getInfocode())) {
                            appendMessage("key----" + key + "已经过期");
                        } else if ("10003".equals(response.getInfocode())) {
                            appendMessage("key----" + key + "已达调用量上限");
                        }else{
                            appendMessage("错误代码：" + response.getInfocode() + "详细信息：" + response.getInfo());
                        }
                    }
                    keys.poll();
                }
                appendMessage("key池已耗尽，无法继续获取POI...");
                return null;
            }
        }
        return (response != null && "10000".equals(response.getInfocode())) ? response : null;
    }
}
