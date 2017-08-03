package com.noqapp.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class DataSourceConfiguration {

    @Value ("${mysql-db-name}")
    private String mysql_db;

    @Value ("${mysql-host}")
    private String mysql_host;

    @Value ("${mysql.username}")
    private String mysqlUsername;

    @Value ("${mysql.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://" + mysql_host + ":3306/" + mysql_db);
        ds.setUsername(mysqlUsername);
        ds.setPassword(mysqlPassword);
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", true);
        ds.addDataSourceProperty("useLocalSessionState", true);
        ds.addDataSourceProperty("useLocalTransactionState", true);
        ds.addDataSourceProperty("rewriteBatchedStatements", true);
        ds.addDataSourceProperty("cacheResultSetMetadata", true);
        ds.addDataSourceProperty("cacheServerConfiguration", true);
        ds.addDataSourceProperty("elideSetAutoCommits", true);
        ds.addDataSourceProperty("maintainTimeStats", false);
        //ds.setMaximumPoolSize(100);
        //ds.setMinimumIdle(5);
        return ds;
    }
}
