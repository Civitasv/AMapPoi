package com.civitasv.spider.util;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.civitasv.spider.mapper.JobMapper;
import com.civitasv.spider.mapper.PoiCategoryMapper;
import com.civitasv.spider.mapper.PoiMapper;
import com.civitasv.spider.mapper.TaskMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtils {

    private static SqlSessionFactory mybatisPlusSqlSessionFactory;

    public static SqlSessionFactory getDefaultMyBatis(){
        String resource = "com/civitasv/spider/mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SqlSessionFactoryBuilder().build(inputStream);
    }

    public static SqlSessionFactory getDefaultMybatisPlus() {
        if(mybatisPlusSqlSessionFactory != null){
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
//        configuration.setLogImpl(StdOutImpl.class);
        mybatisPlusSqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        return mybatisPlusSqlSessionFactory;
    }

    public static DataSource dataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:target/classes/com/civitasv/spider/db/poi.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
