package com.civitasv.spider.util;

/**
 * 坐标转换工具类
 */
public class TransformUtil {
    private static final double PIX = Math.PI * 3000 / 180;
    private static final double PI = Math.PI;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;


    public static double[] transformBD09ToGCJ02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PIX);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PIX);
        double gcj02Lng = z * Math.cos(theta);
        double gcj02Lat = z * Math.sin(theta);
        return new double[]{gcj02Lng, gcj02Lat};
    }

    public static double[] transformGCJ02ToBD09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * PIX);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * PIX);
        double bd09Lng = z * Math.cos(theta) + 0.0065;
        double bd09Lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd09Lng, bd09Lat};
    }

    public static double[] transformGCJ02ToWGS84(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double radLat = lat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1.0 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = dLat * 180.0 / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLng = dLng * 180.0 / (A / sqrtMagic * Math.cos(radLat) * PI);
            double wgs84Lat = lat - dLat;
            double wgs84Lng = lng - dLng;
            return new double[]{wgs84Lng, wgs84Lat};
        }
    }

    public static double[] transformWGS84ToGCJ02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        } else {
            double dLat = transformLat(lng - 105.0, lat - 35.0);
            double dLng = transformLng(lng - 105.0, lat - 35.0);
            double redLat = lat / 180.0 * PI;
            double magic = Math.sin(redLat);
            magic = 1.0 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = dLat * 180.0 / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLng = dLng * 180.0D / (A / sqrtMagic * Math.cos(redLat) * PI);
            double mgLat = lat + dLat;
            double mgLng = lng + dLng;
            return new double[]{mgLng, mgLat};
        }
    }

    public static double[] transformBD09ToWGS84(double lng, double lat) {
        double[] lngLat = transformBD09ToGCJ02(lng, lat);
        return transformGCJ02ToWGS84(lngLat[0], lngLat[1]);
    }

    public static double[] transformWGS84ToBD09(double lng, double lat) {
        double[] lngLat = transformWGS84ToGCJ02(lng, lat);
        return transformGCJ02ToBD09(lngLat[0], lngLat[1]);
    }

    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320.0 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    public static boolean outOfChina(double lng, double lat) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }
}
