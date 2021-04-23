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

    /**
     * 请求参数
     */
    public static final class Request {
        /**
         * 结构化地址信息 必填
         * <p>
         * 规则遵循：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦，
         * 如：北京市朝阳区阜通东大街6号。如果需要解析多个地址的话，
         * 请用"|"进行间隔，并且将 batch 参数设置为 true，最多支持 10 个地址进进行"|"分割形式的请求。
         */
        private String address;

        /**
         * 指定查询的城市 可选
         * <p>
         * 可选输入内容包括：
         * 1. 指定城市的中文（如北京）、
         * 2. 指定城市的中文全拼（beijing）
         * 3. citycode（010）
         * 4. adcode（110000）
         * 注意：不支持县级市
         * 当指定城市查询内容为空时，会进行全国范围内的地址转换检索
         */
        private String city;


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Request(String address, String city, boolean batch, String sig, String output) {
            this.address = address;
            this.city = city;
        }

        public Request(String address) {
            this.address = address;
        }

        public Request() {
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


