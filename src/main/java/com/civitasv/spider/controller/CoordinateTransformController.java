package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.util.CoordinateTransformUtil;
import com.civitasv.spider.util.FileUtil;
import com.civitasv.spider.util.MessageUtil;
import com.civitasv.spider.util.SpatialDataTransformUtil;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.LiteCoordinateSequenceFactory;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoordinateTransformController {
    public TextField inputFile;
    public TextField outputDirectory;
    public ChoiceBox<String> inputCoordinateType;
    public TextArea messageDetail;
    public ChoiceBox<String> outputCoordinateType;
    public Button execute, cancel;
    private ExecutorService worker;


    public void chooseInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("geojson", "*.geojson"),
                new FileChooser.ExtensionFilter("json", "*.json"),
                new FileChooser.ExtensionFilter("shp", "*.shp")
        );
        File file = fileChooser.showOpenDialog(MainApplication.getScene().getWindow());
        if (file != null)
            inputFile.setText(file.getAbsolutePath());
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
        worker = Executors.newSingleThreadExecutor();
        worker.execute(() -> {
            // 检查输入文件、输出文件夹是否指定
            if (!check()) return;
            analysis(true);
            // 输入文件格式
            String inputFormat = FileUtil.getExtension(inputFile.getText());
            // 获取输入坐标格式、输出坐标格式
            String inputCoordinateType = this.inputCoordinateType.getValue();
            String outputCoordinateType = this.outputCoordinateType.getValue();
            if (inputCoordinateType.equals(outputCoordinateType)) {
                Platform.runLater(() -> MessageUtil.alert(Alert.AlertType.ERROR, "坐标转换", null, "坐标格式相同，无需转换！"));
                analysis(false);
                return;
            }
            // 根据输入文件格式转换
            if ("geojson".equals(inputFormat) || "json".equals(inputFormat)) { // 如果是geojson
                String geojson = FileUtil.readFile(inputFile.getText());
                if (geojson == null) {
                    appendMessage("无法读取geojson文件，请检查后重试！");
                    analysis(false);
                    return;
                }
                FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
                if (featureCollection == null) {
                    appendMessage("geojson格式有误，请检查后重试！");
                    analysis(false);
                    return;
                }
                transformFeatureCollection(featureCollection, inputCoordinateType, outputCoordinateType);
                appendMessage("正在保存，请稍候...");
                String outputGeoJson = SpatialDataTransformUtil.featureCollection2GeoJson(featureCollection);
                if (outputGeoJson == null) {
                    appendMessage("保存失败");
                    analysis(false);
                    return;
                }
                File ouputGeoJsonFile = FileUtil.getNewFile(outputDirectory.getText() + "/" + FileUtil.getFileName(inputFile.getText()) + "_" + inputCoordinateType + "transform2" + outputCoordinateType + "." + inputFormat);
                if (ouputGeoJsonFile == null) {
                    appendMessage("文件无法创建，写入失败！");
                    analysis(false);
                    return;
                }
                try (BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(ouputGeoJsonFile.toPath(), StandardCharsets.UTF_8))) {
                    writer.write(outputGeoJson);
                    appendMessage("坐标转换成功，保存文件路径为" + ouputGeoJsonFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    appendMessage("坐标转换失败，请检查后重试");
                }
            } else if ("shp".equals(inputFormat)) {
                File temp = new File("mid.json");
                // 转换为geojson
                if (!SpatialDataTransformUtil.transformShpToGeoJson(inputFile.getText(), temp.getAbsolutePath())) {
                    appendMessage("shp格式有误，请检查后重试！");
                    analysis(false);
                    return;
                }
                String geojson = FileUtil.readFile(temp.getAbsolutePath());
                if (geojson == null) {
                    appendMessage("shp格式有误，请检查后重试！");
                    analysis(false);
                    return;
                }
                FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
                if (featureCollection == null) {
                    appendMessage("shp格式有误，请检查后重试！");
                    analysis(false);
                    return;
                }
                transformFeatureCollection(featureCollection, inputCoordinateType, outputCoordinateType);
                String outputGeoJson = SpatialDataTransformUtil.featureCollection2GeoJson(featureCollection);
                if (outputGeoJson == null) {
                    appendMessage("保存失败");
                    analysis(false);
                    return;
                }
                appendMessage("正在保存，请稍候...");
                File outputShpFile = FileUtil.getNewFile(outputDirectory.getText() + "/" + FileUtil.getFileName(inputFile.getText()) + "_" + inputCoordinateType + "transform2" + outputCoordinateType + "." + inputFormat);
                if (outputShpFile == null) {
                    appendMessage("文件无法创建，写入失败！");
                    analysis(false);
                    return;
                }
                if (SpatialDataTransformUtil.transformGeoJsonStrToShp(outputGeoJson, outputShpFile.getAbsolutePath())) {
                    appendMessage("坐标转换成功，文件保存路径为" + outputShpFile.getAbsolutePath());
                } else {
                    appendMessage("坐标转换失败，请检查后重试");
                }
                if (temp.exists()) temp.delete();
            }
            analysis(false);
        });
    }

    private void transformFeatureCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection, String inputCoordinateType, String outputCoordinateType) {
        GeometryFactory geometryFactory = new GeometryFactory();
        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            while (featureIterator.hasNext()) {
                SimpleFeature feature = featureIterator.next();
                Geometry defaultGeometry = (Geometry) feature.getDefaultGeometry();
                if (defaultGeometry instanceof Point) {
                    Point point = (Point) defaultGeometry;
                    Coordinate coordinate = point.getCoordinate();
                    Coordinate[] coordinateNew = transformCoordinates(new Coordinate[]{coordinate}, inputCoordinateType, outputCoordinateType);

                    if (coordinateNew != null && coordinateNew.length == 1)
                        feature.setDefaultGeometry(geometryFactory.createPoint(coordinateNew[0]));
                } else if (defaultGeometry instanceof MultiPoint) {
                    MultiPoint multiPoint = (MultiPoint) defaultGeometry;
                    Point[] points = new Point[multiPoint.getNumGeometries()];
                    for (int i = 0; i < points.length; i++) {
                        Coordinate coordinate = multiPoint.getGeometryN(i).getCoordinate();
                        Coordinate[] coordinateNew = transformCoordinates(new Coordinate[]{coordinate}, inputCoordinateType, outputCoordinateType);
                        if (coordinateNew != null && coordinateNew.length == 1)
                            points[i] = geometryFactory.createPoint(coordinateNew[0]);
                    }
                    feature.setDefaultGeometry(geometryFactory.createMultiPoint(points));
                } else if (defaultGeometry instanceof LineString) {
                    LineString lineString = (LineString) defaultGeometry;
                    Coordinate[] coordinates = lineString.getCoordinates();
                    Coordinate[] coordinateNew = transformCoordinates(coordinates, inputCoordinateType, outputCoordinateType);
                    if (coordinateNew != null)
                        feature.setDefaultGeometry(geometryFactory.createLineString(coordinateNew));
                } else if (defaultGeometry instanceof MultiLineString) {
                    MultiLineString multiLineString = (MultiLineString) defaultGeometry;
                    LineString[] lineStrings = new LineString[multiLineString.getNumGeometries()];
                    for (int i = 0; i < lineStrings.length; i++) {
                        Coordinate[] coordinates = multiLineString.getGeometryN(i).getCoordinates();
                        Coordinate[] coordinateNew = transformCoordinates(coordinates, inputCoordinateType, outputCoordinateType);
                        if (coordinateNew != null)
                            lineStrings[i] = geometryFactory.createLineString(coordinateNew);
                    }
                    feature.setDefaultGeometry(geometryFactory.createMultiLineString(lineStrings));
                } else if (defaultGeometry instanceof Polygon) {
                    Polygon polygon = (Polygon) defaultGeometry;
                    Coordinate[] coordinates = polygon.getCoordinates();
                    Coordinate[] coordinateNew = transformCoordinates(coordinates, inputCoordinateType, outputCoordinateType);
                    if (coordinateNew != null)
                        feature.setDefaultGeometry(geometryFactory.createPolygon(coordinateNew));
                } else if (defaultGeometry instanceof MultiPolygon) {
                    MultiPolygon multiPolygon = (MultiPolygon) defaultGeometry;
                    Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
                    for (int i = 0; i < polygons.length; i++) {
                        Coordinate[] coordinates = multiPolygon.getGeometryN(i).getCoordinates();
                        Coordinate[] coordinateNew = transformCoordinates(coordinates, inputCoordinateType, outputCoordinateType);
                        if (coordinateNew != null)
                            polygons[i] = geometryFactory.createPolygon(coordinateNew);
                    }
                    feature.setDefaultGeometry(geometryFactory.createMultiPolygon(polygons));
                }
            }
        }
    }

    private Coordinate[] transformCoordinates(Coordinate[] coordinates, String inputType, String outputType) {
        switch (inputType) {
            case "wgs84":
                if ("bd09".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformWGS84ToBD09).toArray(Coordinate[]::new);
                } else if ("gcj02".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                }
                break;
            case "gcj02":
                if ("bd09".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformGCJ02ToBD09).toArray(Coordinate[]::new);
                } else if ("wgs84".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformGCJ02ToWGS84).toArray(Coordinate[]::new);
                }
                break;
            case "bd09":
                if ("gcj02".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformBD09ToGCJ02).toArray(Coordinate[]::new);
                } else if ("wgs84".equals(outputType)) {
                    return Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformBD09ToWGS84).toArray(Coordinate[]::new);
                }
                break;
        }
        return null;
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

    private void analysis(boolean isAnalysis) {
        Platform.runLater(() -> {
            execute.setDisable(isAnalysis);
            inputFile.setDisable(isAnalysis);
            inputCoordinateType.setDisable(isAnalysis);
            outputCoordinateType.setDisable(isAnalysis);
            outputDirectory.setDisable(isAnalysis);
        });
        appendMessage(isAnalysis ? "开始格式转换，请勿操作" : "停止格式转换");
        if (!isAnalysis)
            worker.shutdownNow();
    }

    private void appendMessage(String text) {
        Platform.runLater(() -> messageDetail.appendText(text + "\r\n"));
    }
}
