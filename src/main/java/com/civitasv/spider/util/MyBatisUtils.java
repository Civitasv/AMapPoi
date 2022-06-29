package com.civitasv.spider.util;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.civitasv.spider.MainApplication;
import com.civitasv.spider.mapper.*;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

import java.nio.file.Paths;

public class MyBatisUtils {
    // database URL
    public static String url = "jdbc:sqlite:" + Paths.get("vendor", "db", "poi.db");

    private static SqlSessionFactory mybatisPlusSqlSessionFactory;

    public static SqlSessionFactory getDefaultMybatisPlus() {
        if (mybatisPlusSqlSessionFactory != null) {
            return mybatisPlusSqlSessionFactory;
        }
        DataSource dataSource = dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.addMapper(JobMapper.class);
        configuration.addMapper(TaskMapper.class);
        configuration.addMapper(PoiMapper.class);
        configuration.addMapper(PoiCategoryMapper.class);
        configuration.addMapper(CityCodeMapper.class);
        // configuration.setLogImpl(StdOutImpl.class);
        mybatisPlusSqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        return mybatisPlusSqlSessionFactory;
    }

    public static DataSource dataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("org.sqlite.JDBC");
        dataSource.setUrl(url);
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
