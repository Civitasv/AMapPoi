package com.civitasv.spider.util;

import com.civitasv.spider.helper.Enum.CoordinateType;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.Arrays;

public class BoundaryUtil {
    private final static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 获取 DataV 返回的 GeoJSON 格式数据边界
     *
     * @param geojsonStr GeoJSON 格式字符串
     * @param type       坐标类型 {@link CoordinateType}
     * @return 边界数据
     */
    public static Geometry getBoundaryByGeoJSONStr(String geojsonStr, CoordinateType type) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojsonStr);
        if (featureCollection == null) return null;

        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            if (featureIterator.hasNext()) {
                SimpleFeature boundary = featureIterator.next();
                Geometry defaultGeometry = (Geometry) boundary.getDefaultGeometry();
                if (defaultGeometry instanceof Polygon) {
                    Polygon polygon = (Polygon) defaultGeometry;
                    Coordinate[] coordinates = polygon.getCoordinates();
                    if (type == CoordinateType.WGS84) {
                        coordinates = Arrays.stream(coordinates)
                                .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                    } else if (type == CoordinateType.BD09) {
                        coordinates = Arrays.stream(coordinates)
                                .map(CoordinateTransformUtil::transformBD09ToGCJ02).toArray(Coordinate[]::new);
                    }
                    return geometryFactory.createPolygon(coordinates);
                } else if (defaultGeometry instanceof MultiPolygon) {
                    MultiPolygon multiPolygon = (MultiPolygon) defaultGeometry;
                    Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
                    for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                        Geometry geometryN = multiPolygon.getGeometryN(i);
                        Coordinate[] coordinates = geometryN.getCoordinates();
                        if (type == CoordinateType.WGS84) {
                            coordinates = Arrays.stream(coordinates)
                                    .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                        } else if (type == CoordinateType.BD09) {
                            coordinates = Arrays.stream(coordinates)
                                    .map(CoordinateTransformUtil::transformBD09ToGCJ02).toArray(Coordinate[]::new);
                        }
                        Polygon polygon = geometryFactory.createPolygon(coordinates);
                        polygons[i] = polygon;
                    }
                    return geometryFactory.createMultiPolygon(polygons);
                }
            }
        }
        return null;
    }

    /**
     * 根据用户输入路径获取外接矩形边界
     * @param path 用户文件路径
     * @param type GeoJSON 文件坐标格式
     * @return Geometry 数据
     * @throws IOException 文件不存在时报错
     */
    public static Geometry getBoundaryByUserFile(String path, CoordinateType type) throws IOException {
        String geojsonStr = FileUtil.readFile(path);
        return getBoundaryByGeoJSONStr(geojsonStr, type);
    }

    /**
     * 解析外接矩形字符串，获取边界数组
     * @param text 格式：左上角经度,左上角纬度#右下角经度,右下角纬度
     * @param type 坐标格式，{@link CoordinateType}
     * @return 外接矩形字符串，[左上角经度, 左上角纬度, 右下角经度, 右下角纬度]
     */
    public static Double[] getBoundaryByRectangle(String text, CoordinateType type) {
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
                    return new Double[]{leftTopLonlat[0], rightBottomLonlat[1], rightBottomLonlat[0], leftTopLonlat[1]};
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
