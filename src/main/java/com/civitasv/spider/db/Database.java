package com.civitasv.spider.db;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.model.City;
import com.civitasv.spider.util.MessageUtil;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLITE 数据操作类
 */
public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getSimpleName());

    // database URL
    private String url = "jdbc:sqlite:" + "app/db/poi.db";

    // Constructor
    public Database() {
        System.out.println(this.url);
    }

    // 获取大类
    public List<String> getPoiCategory1() throws SQLException {
        List<String> arrCate1 = new ArrayList<>();

        Connection connection = DriverManager.getConnection(this.url);
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT DISTINCT CATE1 FROM POI_CATEGORY");
            ResultSet result = statement.executeQuery();

            while(result.next()){
                String cate1 = result.getString("CATE1");
                arrCate1.add(cate1);
            }
        } finally {
            this.closeResources(statement, connection);
        }
        LOGGER.log(Level.INFO, "查询POI大类列表: 成功！");
        return arrCate1;
    }

    // 获取中类
    public List<String> getPoiCategory2(String cate1) throws SQLException {

        List<String> arrCate2 = new ArrayList<>();

        Connection connection = DriverManager.getConnection(this.url);
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT DISTINCT CATE2 FROM POI_CATEGORY WHERE CATE1 = ?");
            statement.setString(1, cate1);
            ResultSet result = statement.executeQuery();

            while(result.next()){
                String cate2 = result.getString("CATE2");
                arrCate2.add(cate2);
            }
        } finally {
            this.closeResources(statement, connection);
        }
        LOGGER.log(Level.INFO, "查询POI中类列表: 成功！");
        return arrCate2;
    }

    // 获取小类
    public List<String> getPoiCategory3(String cate1, String cate2) throws SQLException {

        List<String> arrCate3 = new ArrayList<>();

        Connection connection = DriverManager.getConnection(this.url);
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT DISTINCT CATE3 FROM POI_CATEGORY WHERE CATE1 = ? AND CATE2 =?");
            statement.setString(1, cate1);
            statement.setString(2, cate2);
            ResultSet result = statement.executeQuery();

            while(result.next()){
                String cate3 = result.getString("CATE3");
                arrCate3.add(cate3);
            }
        } finally {
            this.closeResources(statement, connection);
        }
        LOGGER.log(Level.INFO, "查询POI小类列表: 成功！");
        return arrCate3;
    }

    // 获取类别ID
    public String getPoiCategoryId(String cate1, String cate2, String cate3) throws SQLException {

        String cateId = "";

        Connection connection = DriverManager.getConnection(this.url);
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("SELECT CATE_ID FROM POI_CATEGORY WHERE CATE1 = ? AND CATE2 =? AND CATE3 =?");
            statement.setString(1, cate1);
            statement.setString(2, cate2);
            statement.setString(3, cate3);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                cateId = result.getString("CATE_ID");
                break;
            }
        } finally {
            this.closeResources(statement, connection);
        }
        LOGGER.log(Level.INFO, "查询POI类别ID: 成功！");
        return cateId;
    }

    // 获取下级城市
    public List<City> getCitesBelong(String cityId) throws SQLException {

        List<City> arrCity = new ArrayList<>();
        City city;

        Connection connection = DriverManager.getConnection(this.url);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT CITY_ID,NAME FROM CITY_CODE WHERE PARENT_ID = ?");
            statement.setString(1, cityId);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                city = new City(result.getString("CITY_ID"), result.getString("Name"));
                arrCity.add(city);
            }
        }finally {
            this.closeResources(statement, connection);
        }
        return arrCity;
    }


    /**
     * 关闭数据库操作相关资源
     */
    private void closeResources(Statement stmt, Connection con) throws SQLException {
        if (stmt != null)
            stmt.close();
        if (con != null)
            con.close();
    }
}
