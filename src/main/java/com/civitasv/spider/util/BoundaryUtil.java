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

import java.io.IOException;
import java.util.Arrays;


public class BoundaryUtil {

    private final static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 获取城市外接矩形区域范围
     *
     * @param adCode 行政区代码
     * @return 城市矩形区域范围
     */
    public static double[] getBoundary(String adCode) {
        DataVDao dao = new DataVDaoImpl();
        JsonObject boundaryJson = dao.getBoundary(adCode);
        if (boundaryJson == null)
            return null;
        return getBoundaryByGeoJson(boundaryJson.toString(), "gcj02");
    }

    public static Geometry getRealBoundary(String adCode) {
        DataVDao dao = new DataVDaoImpl();
        JsonObject boundaryJson = dao.getBoundary(adCode);
        if (boundaryJson == null)
            return null;
        return getRealBoundaryByGeoJson(boundaryJson.toString(), "gcj02");
    }

    public static String getAdName(String adCode) {
        DataVDao dao = new DataVDaoImpl();
        JsonObject boundaryJson = dao.getBoundary(adCode);
        if (boundaryJson == null)
            return null;
        return getAdNameFromAdGeoJson(boundaryJson.toString());
    }

    /**
     * 使用geojson字符串获取城市外接矩形区域范围
     *
     * @param geojson geojson字符串
     * @return 城市矩形区域范围
     */
    public static double[] getBoundaryByGeoJson(String geojson, String type) {
        boolean success = false;
        double maxLon = -180, minLon = 180, maxLat = -90, minLat = 90;
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(geojson, JsonObject.class);
        if (jsonObject.has("features")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("features");
            if (jsonArray.size() > 0) {
                JsonObject feature = jsonArray.get(0).getAsJsonObject();
                if (feature.has("geometry")) {
                    JsonObject geometry = feature.getAsJsonObject("geometry");
                    if (geometry.has("coordinates")) {
                        success = true;
                        try {
                            double[][][][] coordinates = gson.fromJson(geometry.get("coordinates"), double[][][][].class);
                            double[][] lonlats = coordinates[0][0];
                            for (double[] lonlat : lonlats) {
                                // 将坐标转换为gcj02
                                if ("wgs84".equals(type)) {
                                    lonlat = CoordinateTransformUtil.transformWGS84ToGCJ02(lonlat[0], lonlat[1]);
                                }
                                if ("bd09".equals(type)) {
                                    lonlat = CoordinateTransformUtil.transformBD09ToGCJ02(lonlat[0], lonlat[1]);
                                }
                                maxLon = Math.max(maxLon, lonlat[0]);
                                minLon = Math.min(minLon, lonlat[0]);
                                maxLat = Math.max(maxLat, lonlat[1]);
                                minLat = Math.min(minLat, lonlat[1]);
                            }
                        } catch (JsonSyntaxException e) {
                            return null;
                        }
                    }
                }
            }
        }
        return success ? new double[]{minLon, minLat, maxLon, maxLat} : null;
    }


    public static Geometry getRealBoundaryByGeoJson(String geojson, String type) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
        try {
            featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FeatureIterator<SimpleFeature> featureIterator = featureCollection.features();
        SimpleFeature boundary = null;
        while(featureIterator.hasNext()){
            boundary = featureIterator.next();
        }
        Geometry defaultGeometry = (Geometry) boundary.getDefaultGeometry();
        Coordinate[] gcj02Coos = null;
        if(defaultGeometry instanceof Polygon){
            Polygon polygon = (Polygon) defaultGeometry;
            Coordinate[] coordinates = polygon.getCoordinates();
            if ("wgs84".equals(type)) {
                gcj02Coos = Arrays.stream(coordinates)
                        .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
            }
            if ("bd09".equals(type)) {
                gcj02Coos = Arrays.stream(coordinates)
                        .map(CoordinateTransformUtil::transformBD09ToGCJ02).toArray(Coordinate[]::new);
            }else{
                gcj02Coos = coordinates;
            }
            return geometryFactory.createPolygon(gcj02Coos);
        }else if(defaultGeometry instanceof MultiPolygon){
            MultiPolygon multiPolygon = (MultiPolygon) defaultGeometry;
            Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Geometry geometryN = multiPolygon.getGeometryN(i);
                Coordinate[] coordinates = geometryN.getCoordinates();
                if ("wgs84".equals(type)) {
                    gcj02Coos = Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformWGS84ToGCJ02).toArray(Coordinate[]::new);
                }
                if ("bd09".equals(type)) {
                    gcj02Coos = Arrays.stream(coordinates)
                            .map(CoordinateTransformUtil::transformBD09ToGCJ02).toArray(Coordinate[]::new);
                }else{
                    gcj02Coos = coordinates;
                }
                Polygon polygon = geometryFactory.createPolygon(gcj02Coos);
                polygons[i] = polygon;
            }
            geometryFactory.createMultiPolygon(polygons);
        }
        return null;
    }

    public static String getAdNameFromAdGeoJson(String geojson) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
        try {
            featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(geojson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FeatureIterator<SimpleFeature> featureIterator = featureCollection.features();
        SimpleFeature boundary = null;
        while(featureIterator.hasNext()){
            boundary = featureIterator.next();
        }
        return (String) boundary.getAttribute("name");
    }
}
