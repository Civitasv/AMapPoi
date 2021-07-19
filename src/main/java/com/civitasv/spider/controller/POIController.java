package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.dao.AMapDao;
import com.civitasv.spider.dao.impl.AMapDaoImpl;
import com.civitasv.spider.helper.CoordinateType;
import com.civitasv.spider.model.Feature;
import com.civitasv.spider.model.GeoJSON;
import com.civitasv.spider.model.POI;
import com.civitasv.spider.util.*;
import com.civitasv.spider.viewmodel.POIViewModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Time;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class POIController {
    private static Scene scene;

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
    public TabPane tabs; // tab 栏
    public Button directoryBtn; // 点击选择文件夹
    public Button execute; // 执行
    public Button poiType; // 点击查看 poi 类型
    public ChoiceBox<String> userType; // 用户类型
    public ChoiceBox<CoordinateType> rectangleCoordinateType; // 矩形坐标格式
    public ChoiceBox<CoordinateType> userFileCoordinateType; // 用户自定义文件坐标格式
    public MenuItem wechat; // 微信
    public MenuItem joinQQ; // QQ群

    private POIViewModel poiViewModel;

    public void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("poi.fxml"));
        Parent root = fxmlLoader.load();
        POIController controller = fxmlLoader.getController();
        controller.init();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("POIKit");
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("styles.css")).toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        stage.show();
    }

    private void init() {
        this.poiViewModel = new POIViewModel(threadNum, keywords, keys, types, adCode,
                rectangle, threshold, format, outputDirectory, messageDetail, userFile, tabs, directoryBtn,
                execute, poiType, userType, rectangleCoordinateType, userFileCoordinateType, wechat, joinQQ);
        this.threadNum.setTextFormatter(getFormatter());
        this.threshold.setTextFormatter(getFormatter());
        this.adCode.setTextFormatter(getFormatter());
        List<CoordinateType> list = Arrays.asList(CoordinateType.WGS84, CoordinateType.BD09, CoordinateType.GCJ02);
        this.rectangleCoordinateType.setItems(new ObservableListWrapper<>(list));
        this.userFileCoordinateType.setItems(new ObservableListWrapper<>(list));
        this.rectangleCoordinateType.getSelectionModel().selectFirst();
        this.userFileCoordinateType.getSelectionModel().selectFirst();
        wechat.setOnAction(event -> {
            try {
                openAbout(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        joinQQ.setOnAction(event -> {
            try {
                openAbout(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private TextFormatter<Integer> getFormatter() {
        return new TextFormatter<>(
                c -> Pattern.matches("\\d*", c.getText()) ? c : null);
    }

    public void execute() {
        poiViewModel.execute();
    }

    public void cancel() {
        poiViewModel.cancel();
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
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null)
            userFile.setText(file.getAbsolutePath());
    }

    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(scene.getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    public void openPOITypes() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://lbs.amap.com/api/webservice/download"));
    }

    public void openGeocoding() throws IOException {
        GeocodingController controller = new GeocodingController();
        controller.show();
    }

    public void openSpatialTransform() throws IOException {
        SpatialDataTransformController controller = new SpatialDataTransformController();
        controller.show();
    }

    public void openCoordinateTransform() throws IOException {
        CoordinateTransformController controller = new CoordinateTransformController();
        controller.show();
    }

    public void openAbout(boolean isQQ) throws IOException {
        AboutController controller = new AboutController();
        controller.show(isQQ);
    }

    public void starsMe() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/Civitasv/AMapPoi"));
    }
}
