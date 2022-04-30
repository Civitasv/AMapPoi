package com.civitasv.spider.util;

import com.civitasv.spider.helper.Enum.BoundaryType;
import com.civitasv.spider.helper.Enum.CoordinateType;
import com.civitasv.spider.model.bo.POI;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.IOException;
import java.util.function.Predicate;

public class TaskUtil {
    public static Predicate<? super POI.Info> generateFilter(String boundaryConfig, BoundaryType boundaryType) throws IOException {
        // 生成filter
        Predicate<? super POI.Info> result;
        final GeometryFactory geometryFactory = new GeometryFactory();
        String configContent = boundaryConfig.split(":")[1];
        String[] data = configContent.split(",");
        switch (boundaryType) {
            case ADCODE:
                String adCode = data[0];
                String adName = data[1];
                result = info -> {
                    int level = getLevel(adCode);
                    if (level == 0)
                        return "中华人民共和国".equals(adName);
                    else if (level == 1)
                        return info.provinceName().equals(adName);
                    else if (level == 2)
                        return info.cityName().equals(adName);
                    else return info.adName().equals(adName);
                };
                break;
            case RECTANGLE:
                result = info -> true;
                break;
            case CUSTOM:
                Geometry boundary =
                        BoundaryUtil.getBoundaryByUserFile(
                                data[0],
                                CoordinateType.getCoordinateType(data[1])
                        );
                result = info -> {
                    if (info.location() == null) return false;
                    String[] lnglat = info.location().toString().split(",");
                    if (lnglat.length != 2) {
                        return false;
                    }
                    Coordinate coordinate = new Coordinate(Double.parseDouble(lnglat[0]), Double.parseDouble(lnglat[1]));
                    return boundary.intersects(geometryFactory.createPoint(coordinate));
                };
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + boundaryType);
        }
        return result;
    }

    private static int getLevel(String adCode) {
        if ("100000".equals(adCode)) {
            return 0; // country
        } else if ("0000".equals(adCode.substring(2))) {
            return 1; // 省份
        } else if ("00".equals(adCode.substring(4))) {
            return 2; // 城市
        } else return 3; // 县/区
    }
}
