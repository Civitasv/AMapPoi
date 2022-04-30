package com.civitasv.spider.util;

import com.civitasv.spider.helper.Enum.CoordinateType;
import com.civitasv.spider.webdao.DataVDao;
import com.civitasv.spider.webdao.impl.DataVDaoImpl;
import com.google.gson.JsonObject;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DataVUtil {
    private final static DataVDao dataVDao = new DataVDaoImpl();

    /**
     * 根据行政区代码获取 DataV 城市外接矩形区域范围和行政区名称
     *
     * @param adCode 行政区代码
     * @return 城市矩形区域范围和行政区名称
     */
    public static Map<String, Object> getBoundaryAndAdNameByAdCodeFromDataV(String adCode) {
        // 访问 DataV 服务获取行政区GeoJSON格式边界数据
        JsonObject boundaryGeoJson = dataVDao.getBoundary(adCode);
        if (boundaryGeoJson == null)
            return null;
        Geometry gcj02Boundary = BoundaryUtil.getBoundaryByGeoJSONStr(boundaryGeoJson.toString(), CoordinateType.GCJ02);
        String adName = getAdNameFromDataVGeoJSON(boundaryGeoJson.toString());
        HashMap<String, Object> data = new HashMap<>();
        data.put("gcj02Boundary", gcj02Boundary);
        data.put("adName", adName);
        return data;
    }


    /**
     * 根据国家行政区 DataV GeoJSON 获取名字
     *
     * @param dataVGeoJSON 国家行政区 GeoJSON 格式边界
     * @return 行政区名字
     */
    public static String getAdNameFromDataVGeoJSON(String dataVGeoJSON) {
        FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = SpatialDataTransformUtil.geojsonStr2FeatureCollection(dataVGeoJSON);
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
