package com.noqapp.domain.config;

import org.apache.commons.dbcp.BasicDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class DataSourceConfiguration {

    @Value("${mysql-db-name}")
    private String mysql_db;

    @Value("${mysql-host}")
    private String mysql_host;

    @Value("${mysql.username}")
    private String mysqlUsername;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://" + mysql_host + ":3306/" + mysql_db);
        ds.setUsername(mysqlUsername);
        ds.setPassword(mysqlPassword);
        ds.setInitialSize(5);
        ds.setMaxActive(10);
        return ds;
    }
}
