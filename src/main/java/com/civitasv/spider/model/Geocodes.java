package com.civitasv.spider.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 地理编码
 */
public class Geocodes {
    /**
     * 响应参数
     */
    public static final class Response {

        /**
         * 返回结果状态值
         * <p>
         * 返回值为 0 或 1，0 表示请求失败；1 表示请求成功
         */
        private final Integer status;

        /**
         * 返回状态说明
         * <p>
         * 当 status 为 0 时，info 会返回具体错误原因，否则返回OK
         */
        private final String info;

        /**
         * 返回状态码
         */
        private final String infocode;

        /**
         * 返回结果数目
         */
        private final Integer count;

        /**
         * 地理编码信息列表
         */
        private final Info[] geocodes;

        public Response(Integer status, String info, String infocode, Integer count, Info[] geocodes) {
            this.status = status;
            this.info = info;
            this.infocode = infocode;
            this.count = count;
            this.geocodes = geocodes;
        }

        public Integer getCount() {
            return count;
        }

        public Info[] getGeocodes() {
            return geocodes;
        }

        public Integer getStatus() {
            return status;
        }

        public String getInfo() {
            return info;
        }


        public String getInfocode() {
            return infocode;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "status=" + status +
                    ", info='" + info + '\'' +
                    ", infoCode='" + infocode + '\'' +
                    ", count=" + count +
                    ", geocodes=" + Arrays.toString(geocodes) +
                    '}';
        }
    }

    public static class Info {
        @SerializedName("formatted_address")
        public String formattedAddress;
        public String country;
        public String province;
        @SerializedName("citycode")
        public String cityCode;
        public String city;
        public Object district;
        public Object township;
        public Neighborhood neighborhood;
        public Building building;
        @SerializedName("adcode")
        public String adCode;
        public Object street;
        public Object number;
        public String location;
        public String level;

        @Override
        public String toString() {
            return "Info{" +
                    "formattedAddress='" + formattedAddress + '\'' +
                    ", country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", cityCode='" + cityCode + '\'' +
                    ", city='" + city + '\'' +
                    ", district=" + district +
                    ", township=" + township +
                    ", neighborhood=" + neighborhood +
                    ", building=" + building +
                    ", adCode='" + adCode + '\'' +
                    ", street=" + street +
                    ", number=" + number +
                    ", location='" + location + '\'' +
                    ", level='" + level + '\'' +
                    '}';
        }
    }

    public static class Neighborhood {
        public String[] name;
        public String[] type;

        @Override
        public String toString() {
            return "Neighborhood{" +
                    "name=" + Arrays.toString(name) +
                    ", type=" + Arrays.toString(type) +
                    '}';
        }
    }

    public static class Building {
        public String[] name;
        public String[] type;

        @Override
        public String toString() {
            return "Building{" +
                    "name=" + Arrays.toString(name) +
                    ", type=" + Arrays.toString(type) +
                    '}';
        }
    }
}


