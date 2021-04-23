package com.civitasv.spider.util;

import com.civitasv.spider.dao.DataVDao;
import com.civitasv.spider.dao.impl.DataVDaoImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;


public class AMapPoiUtil {

    /**
     * 获取城市外接矩形区域范围
     *
     * @param adCode 行政区代码
     * @return 城市矩形区域范围
     */
    public static double[] getBoundary(String adCode) {
        DataVDao dao = new DataVDaoImpl();
        if (dao.getBoundary(adCode) == null)
            return null;
        return getBoundaryByGeoJson(dao.getBoundary(adCode).toString());
    }

    /**
     * 使用geojson字符串获取城市外接矩形区域范围
     *
     * @param geojson geojson字符串
     * @return 城市矩形区域范围
     */
    public static double[] getBoundaryByGeoJson(String geojson) {
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

    public static String getCrs84() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "name");
        JsonObject properties = new JsonObject();
        properties.addProperty("name", "urn:ogc:def:crs:OGC:1.3:CRS84");
        jsonObject.add("properties", properties);
        return jsonObject.toString();
    }
}
