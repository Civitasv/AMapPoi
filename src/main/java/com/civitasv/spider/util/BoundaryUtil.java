package com.civitasv.spider.util;

import com.civitasv.spider.dao.DataVDao;
import com.civitasv.spider.dao.impl.DataVDaoImpl;
import com.civitasv.spider.helper.Enum.CoordinateType;
import com.google.gson.JsonObject;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class BoundaryUtil {
    private final static GeometryFactory geometryFactory = new GeometryFactory();
    private final static DataVDao dataVDao = new DataVDaoImpl();


    /**
     * 根据行政区代码获取城市外接矩形区域范围和行政区名称
     *
     * @param adCode 行政区代码
     * @return 城市矩形区域范围和行政区名称
     */
    public static Map<String, Object> getBoundaryAndAdNameByAdCode(String adCode) {
        // 访问 DataV 服务获取行政区GeoJSON格式边界数据
        JsonObject boundaryGeoJson = dataVDao.getBoundary(adCode);
        if (boundaryGeoJson == null)
            return null;
        Geometry gcj02Boundary = getBoundaryByDataVGeoJSON(boundaryGeoJson.toString(), CoordinateType.GCJ02);
        String adName = getAdNameFromAdGeoJSON(boundaryGeoJson.toString());
        HashMap<String, Object> data = new HashMap<>();
        data.put("gcj02Boundary", gcj02Boundary);
        data.put("adName", adName);
        return data;
    }

    /**
     * 获取 DataV 返回的 GeoJSON 格式数据边界
     *
     * @param geojsonStr GeoJSON 格式字符串
     * @param type       坐标类型 {@link CoordinateType}
     * @return 边界数据
     */
    public static Geometry getBoundaryByDataVGeoJSON(String geojsonStr, CoordinateType type) {
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
     * 根据国家行政区 DataV GeoJSON 获取名字
     *
     * @param geojsonStr 国家行政区 GeoJSON 格式边界
     * @return 行政区名字
     */
    public static String getAdNameFromAdGeoJSON(String geojsonStr) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojsonStr);
        if (featureCollection == null) return null;

        try (FeatureIterator<SimpleFeature> featureIterator = featureCollection.features()) {
            if (featureIterator.hasNext()) { // 只有一个feature
                SimpleFeature boundary = featureIterator.next();
                return (String) boundary.getAttribute("name");
            }
        }
        return null;
    }
}
