package com.noqapp.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.session.SessionFactory;

/**
 * hitender
 * 1/19/21 12:15 AM
 */
@Configuration
@EnableNeo4jRepositories(
    basePackages = "com.noqapp.repository.neo4j",
    sessionFactoryRef = "sessionFactory"
)
@EnableTransactionManagement
public class Neo4jConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jConfiguration.class);

    @Value("${neo4j.username}")
    private String username;

    @Value("${neo4j.password}")
    private String password;

    @Bean
    public SessionFactory sessionFactory() {
        // with domain entity base package(s)
        return new SessionFactory(configuration(), "com.noqapp.domain.neo4j");
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        ConfigurationSource properties = new ClasspathConfigurationSource("conf/ogm.properties");
        return new org.neo4j.ogm.config.Configuration.Builder(properties)
            .credentials(username, password)
            .build();
    }

    /** Note: @primary   To prevent confusion between transactionManager and neo4jTransactionManager. */
    @Bean("neo4jTransactionManager")
    @Primary
    public Neo4jTransactionManager neo4jTransactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}
