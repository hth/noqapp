package com.noqapp.common.config;

import com.zaxxer.hikari.HikariDataSource;

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

    @Value("${mysql-port}")
    private String mysql_port;

    @Value("${mysql.username}")
    private String mysqlUsername;

    @Value("${mysql.password}")
    private String mysqlPassword;

    @Bean
    DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://" + mysql_host + ":" + mysql_port + "/" + mysql_db);
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
        /* Since the time new driver class is com.mysql.cj.jdbc.Driver. */
        ds.addDataSourceProperty("verifyServerCertificate", false);
        ds.addDataSourceProperty("useSSL", false);
        ds.addDataSourceProperty("serverTimezone", "UTC");
        /* End of changes since new driver class. */
        ds.setConnectionInitSql("SET NAMES utf8mb4");
        ds.setMaximumPoolSize(10);
        ds.setMinimumIdle(2);
        /* 30 seconds. */
        ds.setIdleTimeout(30000);
        return ds;
    }
}
