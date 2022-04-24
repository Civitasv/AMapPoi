package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.helper.BaseController;
import com.civitasv.spider.util.FileUtil;
import com.civitasv.spider.util.MessageUtil;
import com.civitasv.spider.util.SpatialDataTransformUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 空间数据转换
 * <p>
 * csv shp geojson 互相转换
 */
public class SpatialDataTransformController extends BaseController {
    private static Scene scene;
    public TextField inputFile;
    public ChoiceBox<String> format;
    public TextField outputDirectory;
    public TextArea messageDetail;
    public Button execute, cancel;
    private ExecutorService worker;

    public void show() throws IOException {
        init();
        stage.show();
    }

    private void init() {
        inputFile.textProperty().addListener((observable, oldValue, newValue) -> {
            String inputFormat = FileUtil.getExtension(inputFile.getText());
            if (inputFormat == null) return;
            switch (inputFormat) {
                case "geojson":
                case "json":
                    format.getItems().clear();
                    format.getItems().add("shp");
                    format.getSelectionModel().selectFirst();
                    break;
                case "shp":
                    format.getItems().clear();
                    format.getItems().addAll("json", "csv");
                    format.getSelectionModel().selectFirst();
                    break;
            }
        });
    }

    public void chooseInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("geojson", "*.geojson"),
                new FileChooser.ExtensionFilter("json", "*.json"),
                new FileChooser.ExtensionFilter("shp", "*.shp")
        );
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null)
            inputFile.setText(file.getAbsolutePath());
    }

    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(scene.getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    public void execute() {
        messageDetail.clear();
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            // 检查输入文件、输出文件夹是否指定
            if (!check()) return;
            analysis(true);
            // 根据输入文件格式扩展format choicebox
            String inputFormat = FileUtil.getExtension(inputFile.getText());
            String outPutFormat = format.getValue();
            if ("geojson".equals(inputFormat) || "json".equals(inputFormat)) {
                if ("shp".equals(outPutFormat)) {
                    File shpFile = FileUtil.getNewFile(outputDirectory.getText() + "/" + FileUtil.getFileName(inputFile.getText()) + "_transform2shp" + ".shp");
                    if (shpFile == null) {
                        appendMessage("文件无法创建，写入失败！");
                        analysis(false);
                        return;
                    }
                    if (SpatialDataTransformUtil.transformGeoJsonToShp(inputFile.getText(), shpFile.getAbsolutePath())) {
                        appendMessage("格式转换成功，保存文件路径为" + shpFile.getAbsolutePath());
                    } else {
                        appendMessage("格式转换失败，请检查后重试");
                    }
                }
            } else if ("shp".equals(inputFormat)) {
                if ("json".equals(outPutFormat)) {
                    File geojsonFile = FileUtil.getNewFile(outputDirectory.getText() + "/" + FileUtil.getFileName(inputFile.getText()) + "_transform2geojson" + ".json");
                    if (geojsonFile == null) {
                        appendMessage("文件无法创建，写入失败！");
                        analysis(false);
                        return;
                    }
                    if (SpatialDataTransformUtil.transformShpToGeoJSON(inputFile.getText(), geojsonFile.getAbsolutePath())) {
                        appendMessage("格式转换成功，保存文件路径为" + geojsonFile.getAbsolutePath());
                    } else {
                        appendMessage("格式转换失败，请检查后重试");
                    }
                } else if ("csv".equals(outPutFormat)) {
                    File csvFile = FileUtil.getNewFile(outputDirectory.getText() + "/" + FileUtil.getFileName(inputFile.getText()) + "_transform2csv" + ".csv");
                    if (csvFile == null) {
                        appendMessage("文件无法创建，写入失败！");
                        analysis(false);
                        return;
                    }
                    if (SpatialDataTransformUtil.transformShpToCsv(inputFile.getText(), csvFile.getAbsolutePath())) {
                        appendMessage("格式转换成功，保存文件路径为" + csvFile.getAbsolutePath());
                    } else {
                        appendMessage("格式转换失败，请检查后重试");
                    }
                }
            }
            analysis(false);
        });
    }

    private void analysis(boolean isAnalysis) {
        Platform.runLater(() -> {
            execute.setDisable(isAnalysis);
            inputFile.setDisable(isAnalysis);
            format.setDisable(isAnalysis);
            outputDirectory.setDisable(isAnalysis);
        });
        appendMessage(isAnalysis ? "开始格式转换，请勿操作" : "停止格式转换");
        if (!isAnalysis)
            worker.shutdownNow();
    }

    private boolean check() {
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
        if (format.getValue().isEmpty()) {
            // 输出格式为空
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输出格式", null, "输出格式不能为空！"));
            return false;
        }
        // 获取输入文件格式
        String extension = FileUtil.getExtension(inputFile.getText());
        if (extension != null) {
            extension = extension.toLowerCase(Locale.ROOT);
            if (!extension.equals("shp") && !extension.equals("geojson") && !extension.equals("json")) {
                Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择shp或geojson或json格式文件！"));
                return false;
            }
        } else {
            Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "输入文件", null, "输入文件格式有误，请选择shp或geojson或json格式文件！"));
            return false;
        }
        return true;
    }

    public void cancel() {
        analysis(false);
        messageDetail.clear();
    }

    private void appendMessage(String text) {
        Platform.runLater(() -> messageDetail.appendText(text + "\r\n"));
    }
}
