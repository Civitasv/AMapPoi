package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.POI;
import com.civitasv.spider.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    private ExecutorService worker, executorService;
    private MyProgressBar progressBar;

    public void init() {
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
        messageDetail.clear();
        if (keys.getText().isEmpty()) {
            // keys为空
            MessageUtil.alert(Alert.AlertType.ERROR, "高德key", null, "高德key池不能为空！");
            return;
        }
        if (inputFile.getText().isEmpty()) {
            // 输入文件为空
            MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件不能为空！");
            return;
        }
        if (outputDirectory.getText().isEmpty()) {
            // 输出文件夹为空
            MessageUtil.alert(Alert.AlertType.ERROR, "输出文件夹", null, "输出文件夹不能为空！");
            return;
        }
        // 获取输入文件格式
        String extension = FileUtil.getExtension(inputFile.getText());
        if (extension != null) {
            extension = extension.toLowerCase(Locale.ROOT);
            if (!extension.equals("csv") && !extension.equals("txt")) {
                MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择csv或txt格式文件！");
                return;
            }
        } else {
            MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择csv或txt格式文件！");
            return;
        }
        List<String> keys = new ArrayList<>(Arrays.asList(this.keys.getText().split(",")));
        analysis(true);
        // 解析输入文件
        List<Map<String, String>> parseRes = ParseUtil.parseTxtOrCsv(inputFile.getText());
        // 解析地址
        if (parseRes != null && parseRes.size() > 0) {
            if (!parseRes.get(0).containsKey("address")) {
                MessageUtil.alert(Alert.AlertType.ERROR, "解析", null, "输入文件不含address关键字，请修改后重试！");
                analysis(false);
                return;
            }
            progressBar = new MyProgressBar(messageDetail, 40, parseRes.size() - 1, "#", "-");
            // 获取输出格式
            String outputFormat = format.getValue();
            switch (outputFormat) {
                case "csv":
                case "txt":
                    saveToCsvOrTxt(parseRes, outputFormat, keys);
                    break;
                case "json":
                    saveToJson(parseRes, keys);
                    break;
            }
        } else {
            MessageUtil.alert(Alert.AlertType.ERROR, "解析", null, "输入文件为空或解析失败，请检查后重试！");
            analysis(false);
        }
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
        File file = fileChooser.showOpenDialog(MainApplication.getScene().getWindow());
        if (file != null)
            inputFile.setText(file.getAbsolutePath());
    }

    /**
     * 选择文件夹
     */
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(MainApplication.getScene().getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    private void saveToCsvOrTxt(@NotNull List<Map<String, String>> parseRes, String outputFormat, List<String> amapKeys) {
        // 创建工作线程执行保存文件工作
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            int threadNum = this.threadNum.getText().isEmpty() ? 2 : Integer.parseInt(this.threadNum.getText());
            // 创建线程池执行解析工作
            executorService = Executors.newFixedThreadPool(threadNum);
            job:
            for (int i = 0; i < parseRes.size(); i += threadNum) {
                List<Callable<Geocodes.Response>> call = new ArrayList<>();
                for (int j = 0; j < threadNum && (i + j) < parseRes.size(); j++) {
                    Map<String, String> item = parseRes.get(i + j);
                    if (item.containsKey("address")) {
                        call.add(() -> geocode(item.get("address"), item.get("city"), amapKeys));
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
                        item.put("infocode", response.getInfoCode());
                        Geocodes.Info[] infos = response.getGeocodes();
                        for (int k = 0; k < infos.length; k++) {
                            Geocodes.Info info = infos[k];
                            item.put("formatted_address_" + k, info.formattedAddress);
                            item.put("country_" + k, info.country);
                            item.put("province_" + k, info.province);
                            item.put("citycode_" + k, info.cityCode);
                            item.put("district_" + k, info.district.toString());
                            item.put("township_" + k, info.township.toString());
                            item.put("adcode_" + k, info.adCode);
                            item.put("street_" + k, info.street.toString());
                            item.put("number_" + k, info.number.toString());
                            item.put("level_" + k, info.level);
                            String[] lonlat = info.location.split(",");
                            if (lonlat.length == 2) {
                                item.put("gcj02_lon_" + k, lonlat[0]);
                                item.put("gcj02_lat_" + k, lonlat[1]);
                                double[] wgs84 = TransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
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
            List<String> keys = new ArrayList<>(parseRes.get(0).keySet());
            File file = new File(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + "." + outputFormat);
            try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))) {
                appendMessage("写入中");
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
                            writer.write(item.get(keys.get(i)));
                            if (i != keys.size() - 1)
                                writer.write(",");
                        }
                        writer.write("\r\n");
                    } catch (IOException e) {
                        appendMessage("写入失败");
                    }
                });
                appendMessage("写入成功");
            } catch (IOException e) {
                appendMessage("写入失败");
            }
            analysis(false);
        });
        worker.shutdown();
    }

    private void saveToJson(@NotNull List<Map<String, String>> parseRes, List<String> amapKeys) {
        // 创建工作线程执行保存文件工作
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            int threadNum = this.threadNum.getText().isEmpty() ? 2 : Integer.parseInt(this.threadNum.getText());
            // 创建线程池执行解析工作
            executorService = Executors.newFixedThreadPool(threadNum);
            JsonArray jsonArray = new JsonArray();
            job:
            for (int i = 0; i < parseRes.size(); i += threadNum) {
                List<Callable<Geocodes.Response>> call = new ArrayList<>();
                for (int j = 0; j < threadNum && (i + j) < parseRes.size(); j++) {
                    Map<String, String> item = parseRes.get(i + j);
                    if (item.containsKey("address")) {
                        call.add(() -> geocode(item.get("address"), item.get("city"), amapKeys));
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
                        JsonObject jsonObject = new JsonObject();
                        Map<String, String> item = parseRes.get(i + j);
                        item.forEach(jsonObject::addProperty);
                        // 更新解析进度
                        int finalI = i, finalJ = j;
                        Platform.runLater(() -> progressBar.show(finalI + finalJ));
                        jsonObject.addProperty("status", response.getStatus().toString());
                        jsonObject.addProperty("info", response.getInfo());
                        jsonObject.addProperty("infocode", response.getInfoCode());
                        JsonArray infoArray = new JsonArray();
                        Geocodes.Info[] infos = response.getGeocodes();
                        for (Geocodes.Info info : infos) {
                            JsonObject jsonItem = new JsonObject();
                            jsonItem.addProperty("formatted_address", info.formattedAddress);
                            jsonItem.addProperty("country", info.country);
                            jsonItem.addProperty("province", info.province);
                            jsonItem.addProperty("citycode", info.cityCode);
                            jsonItem.addProperty("district", info.district.toString());
                            jsonItem.addProperty("township", info.township.toString());
                            jsonItem.addProperty("adcode", info.adCode);
                            jsonItem.addProperty("street", info.street.toString());
                            jsonItem.addProperty("number", info.number.toString());
                            jsonItem.addProperty("level", info.level);
                            String[] lonlat = info.location.split(",");
                            if (lonlat.length == 2) {
                                jsonItem.addProperty("gcj02_lon", lonlat[0]);
                                jsonItem.addProperty("gcj02_lat", lonlat[1]);
                                double[] wgs84 = TransformUtil.transformGCJ02ToWGS84(Double.parseDouble(lonlat[0]), Double.parseDouble(lonlat[1]));
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
            File jsonFile = new File(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + ".json");
            try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
                appendMessage("写入中");
                writer.write(json);
                appendMessage("写入成功");
            } catch (IOException e) {
                appendMessage("写入失败");
            }
            analysis(false);
        });
        worker.shutdown();
    }

    private void finish(List<Map<String, String>> parseRes, List<Integer> failIds) {
        if (failIds.size() == 0) {
            appendMessage("地理编码解析成功");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("成功了").append(parseRes.size() - failIds.size()).append("个，失败了").append(failIds.size()).append("个，失败条目如下：\r\n");
            for (int id : failIds) {
                Map<String, String> item = parseRes.get(id);
                builder.append(item.toString()).append("\r\n");
            }
            appendMessage(builder.toString());
        }
    }

    private void analysis(boolean isAnalysis) {
        execute.setDisable(isAnalysis);
        keys.setDisable(isAnalysis);
        inputFile.setDisable(isAnalysis);
        format.setDisable(isAnalysis);
        outputDirectory.setDisable(isAnalysis);
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

    private void appendMessage(String text) {
        Platform.runLater(() -> messageDetail.appendText("\r\n" + text));
    }

    private Geocodes.Response geocode(String address, String city, List<String> keys) {
        if (keys.isEmpty()) {
            appendMessage("key池已耗尽，无法继续获取POI...");
            return null;
        }
        int index = (int) (Math.random() * keys.size());
        Geocodes.Response response = aMapDao.geocoding(keys.get(index), address, city);
        if ("10001".equals(response.getInfoCode()) || "10003".equals(response.getInfoCode())) {
            synchronized (this) {
                if ("10001".equals(response.getInfoCode())) {
                    appendMessage("key----" + keys.get(index) + "已经过期");
                }
                if ("10003".equals(response.getInfoCode())) {
                    appendMessage("key----" + keys.get(index) + "已达调用量上限");
                }
                // 去除过期的，使用其它key重新访问
                keys.remove(index);
                while (!keys.isEmpty()) {
                    appendMessage("正在尝试其它key");
                    index = (int) (Math.random() * keys.size());
                    String key = keys.get(index);
                    response = aMapDao.geocoding(key, address, city);
                    if ("10000".equals(response.getInfoCode())) {
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
        return "10000".equals(response.getInfoCode()) ? response : null;
    }
}
