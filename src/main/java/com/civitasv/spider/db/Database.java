package com.civitasv.spider.db;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.civitasv.spider.MainApplication.isDEV;

/**
 * SQLITE 数据操作类
 */
public class Database {
    // database URL
    public static String url = isDEV ? "jdbc:sqlite:" + MainApplication.class.getResource("db/poi.db") : "jdbc:sqlite:app/assets/poi.db";

    // Constructor
    public Database() {
        System.out.println(url);
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
            while (result.next()) {
                city = new City(result.getString("CITY_ID"), result.getString("Name"));
                arrCity.add(city);
            }
        } finally {
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


