package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.util.FileUtil;
import com.civitasv.spider.util.MessageUtil;
import com.civitasv.spider.util.MyProgressBar;
import com.civitasv.spider.util.ParseUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    public TextField inputKey;
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
    public void execute() throws IOException {
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
            if (!extension.equals("json") && !extension.equals("csv") && !extension.equals("txt")) {
                MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择json、csv或txt格式文件！");
                return;
            }
        } else {
            MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择json、csv或txt格式文件！");
            return;
        }
        startAnalysis();
        // 解析输入文件
        List<Map<String, String>> parseRes = null;
        switch (extension) {
            case "csv":
            case "txt":
                parseRes = ParseUtil.parseTxtOrCsv(inputFile.getText());
                break;
            case "json":
                break;
        }
        // 解析地址
        if (parseRes != null && parseRes.size() > 0) {
            progressBar = new MyProgressBar(messageDetail, parseRes.size() - 1, 50, "#");
            // 获取输出格式
            String outputFormat = format.getValue();
            switch (outputFormat) {
                case "csv":
                case "txt":
                    saveToCsvOrTxt(parseRes, outputFormat);
                    break;
                case "json":
                    saveToJson(parseRes);
                    break;
            }

        } else {
            MessageUtil.alert(Alert.AlertType.ERROR, "解析", null, "输入为空或解析失败，请检查后重试！");
            endAnalysis();
        }
    }

    /**
     * 选择文件
     */
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json"),
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

    private void saveToCsvOrTxt(@NotNull List<Map<String, String>> parseRes, String outputFormat) {
        // 创建工作线程执行保存文件工作
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            int threadNum = this.threadNum.getText().isEmpty() ? 2 : Integer.parseInt(this.threadNum.getText());
            // 创建线程池执行解析工作
            executorService = Executors.newFixedThreadPool(threadNum);
            final List<Integer> failIds = new ArrayList<>();
            for (int i = 0; i < parseRes.size(); i += threadNum) {
                List<Callable<Geocodes.Response>> call = new ArrayList<>();
                for (int j = 0; j < threadNum && (i + j) < parseRes.size(); j++) {
                    Map<String, String> item = parseRes.get(i + j);
                    if (item.containsKey("address"))
                        call.add(() ->
                                inputKey.getText().isEmpty() ?
                                        aMapDao.geocoding(item.get("address"), item.get("city"))
                                        : aMapDao.geocoding(inputKey.getText(), item.get("address"), item.get("city"))
                        );
                }
                try {
                    List<Future<Geocodes.Response>> futures = executorService.invokeAll(call);
                    for (int j = 0; j < futures.size(); j++) {
                        // 更新解析进度
                        int finalI = i, finalJ = j;
                        Platform.runLater(() -> {
                            progressBar.show(finalI + finalJ);
                        });
                        Future<Geocodes.Response> future = futures.get(j);
                        Map<String, String> item = parseRes.get(i + j);
                        Geocodes.Response response = future.get();
                        if (response == null) {
                            failIds.add(i + j);
                            continue;
                        }
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
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
            List<String> keys = new ArrayList<>(parseRes.get(0).keySet());
            File file = new File(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + (outputFormat.equals("txt") ? ".txt" : ".csv"));
            try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))) {
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
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            endAnalysis();
            // 解析完成，在主线程进行UI更新
            Platform.runLater(() -> finish(parseRes, failIds));
        });
        worker.shutdown();
    }

    private void saveToJson(@NotNull List<Map<String, String>> parseRes) {
        // 创建工作线程执行保存文件工作
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            int threadNum = this.threadNum.getText().isEmpty() ? 2 : Integer.parseInt(this.threadNum.getText());
            // 创建线程池执行解析工作
            executorService = Executors.newFixedThreadPool(threadNum);
            final List<Integer> failIds = new ArrayList<>();
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < parseRes.size(); i += threadNum) {
                List<Callable<Geocodes.Response>> call = new ArrayList<>();
                for (int j = 0; j < threadNum && (i + j) < parseRes.size(); j++) {
                    Map<String, String> item = parseRes.get(i + j);
                    if (item.containsKey("address"))
                        call.add(() ->
                                inputKey.getText().isEmpty() ?
                                        aMapDao.geocoding(item.get("address"), item.get("city"))
                                        : aMapDao.geocoding(inputKey.getText(), item.get("address"), item.get("city"))
                        );
                }
                try {
                    List<Future<Geocodes.Response>> futures = executorService.invokeAll(call);
                    for (int j = 0; j < futures.size(); j++) {
                        // 更新解析进度
                        int finalI = i, finalJ = j;
                        Platform.runLater(() -> {
                            progressBar.show(finalI + finalJ);
                        });
                        JsonObject jsonObject = new JsonObject();
                        Future<Geocodes.Response> future = futures.get(j);
                        Map<String, String> item = parseRes.get(i + j);
                        item.forEach(jsonObject::addProperty);
                        Geocodes.Response response = future.get();
                        if (response == null) {
                            failIds.add(i + j);
                            continue;
                        }
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
                            }
                            infoArray.add(jsonItem);
                        }
                        jsonObject.add("res", infoArray);
                        jsonArray.add(jsonObject);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
            String json = jsonArray.toString();
            File jsonFile = new File(outputDirectory.getText() + "\\解析结果_" + FileUtil.getFileName(inputFile.getText()) + ".json");
            try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(jsonFile.toPath(), StandardCharsets.UTF_8))) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            endAnalysis();
            // 解析完成，在主线程进行UI更新
            Platform.runLater(() -> finish(parseRes, failIds));
        });
        worker.shutdown();
    }

    private void finish(List<Map<String, String>> parseRes, List<Integer> failIds) {
        if (failIds.size() == 0) {
            setMessage("解析成功");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("成功了").append(parseRes.size() - failIds.size()).append("个，失败了").append(failIds.size()).append("个，失败条目如下：\r\n");
            for (int id : failIds) {
                Map<String, String> item = parseRes.get(id);
                builder.append(item.toString()).append("\r\n");
            }
            setMessage(builder.toString());
        }
    }

    private void setMessage(String text) {
        this.messageDetail.appendText(text);
    }

    private void startAnalysis() {
        execute.setDisable(true);
        inputKey.setDisable(true);
        inputFile.setDisable(true);
        format.setDisable(true);
        outputDirectory.setDisable(true);
    }

    private void endAnalysis() {
        execute.setDisable(false);
        inputKey.setDisable(false);
        inputFile.setDisable(false);
        format.setDisable(false);
        outputDirectory.setDisable(false);
    }

    public void cancel() {
        // 停止解析
        executorService.shutdownNow();
        worker.shutdownNow();
        main.setDisable(false);
    }
}
