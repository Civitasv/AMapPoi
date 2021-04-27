package com.civitasv.spider.util;

import com.civitasv.spider.dao.DataVDao;
import com.civitasv.spider.dao.impl.DataVDaoImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class BoundaryUtil {
    private final static GeometryFactory geometryFactory = new GeometryFactory();


    /**
     * 获取城市外接矩形区域范围和城市adname
     *
     * @param adCode 行政区代码
     * @return 城市矩形区域范围和城市adname
     */
    public static Map<String, Object> getBoundaryAndAdNameByAdCode(String adCode) {
        DataVDao dao = new DataVDaoImpl();
        JsonObject boundaryJson = dao.getBoundary(adCode);
        if (boundaryJson == null)
            return null;
        Geometry gcj02 = getBoundaryByGeoJson(boundaryJson.toString(), "gcj02");
        String adName = getAdNameFromAdGeoJson(boundaryJson.toString());
        HashMap<String, Object> message = new HashMap<>();
        message.put("geometry", gcj02);
        message.put("adname", adName);
        return message;
    }

    /**
     * 获取geojson的边界
     *
     * @param geojson geojson字符串
     * @param type    坐标类型
     * @return geojson边界
     */
    public static Geometry getBoundaryByGeoJson(String geojson, String type) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
        if (featureCollection == null) return null;

        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            if (featureIterator.hasNext()) {
                SimpleFeature boundary = featureIterator.next();
                Geometry defaultGeometry = (Geometry) boundary.getDefaultGeometry();
                if (defaultGeometry instanceof Polygon) {
                    Polygon polygon = (Polygon) defaultGeometry;
                    Coordinate[] coordinates = polygon.getCoordinates();
                    if ("wgs84".equals(type)) {
                        coordinates = Arrays.stream(coordinates)
                                .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                    } else if ("bd09".equals(type)) {
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
                        if ("wgs84".equals(type)) {
                            coordinates = Arrays.stream(coordinates)
                                    .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                        } else if ("bd09".equals(type)) {
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
     * 根据国家行政区geojson获取名字
     *
     * @param geojson 国家行政区geojson边界
     * @return 行政区名字
     */
    public static String getAdNameFromAdGeoJson(String geojson) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
        if (featureCollection == null) return null;

        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            if (featureIterator.hasNext()) {
                SimpleFeature boundary = featureIterator.next();
                return (String) boundary.getAttribute("name");
            }
        }
        return null;
    }
}
