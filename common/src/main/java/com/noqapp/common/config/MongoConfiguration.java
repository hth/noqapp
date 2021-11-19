package com.noqapp.common.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 12/3/17 12:18 PM
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.noqapp")
public class MongoConfiguration {

    @Value("${mongo.replica.set}")
    private String mongoReplicaSet;

    @Value("${mongo.database.name}")
    private String mongoDatabaseName;

    /**
     * Template ready to use to operate on the database. Implements MongoOperations.
     *
     * @return Mongo Template ready to use
     */
    @Bean
    MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mongoConverter());

        mongoTemplate.setReadPreference(ReadPreference.nearest());
        mongoTemplate.setWriteConcern(WriteConcern.JOURNALED);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);

        return mongoTemplate;
    }

    /**
     * DB connection Factory
     *
     * @return a ready to use MongoDbFactory
     */
    @Bean
    MongoDatabaseFactory mongoDbFactory() {
        // Mongo Client
        MongoClient mongoClient = MongoClients.create(populateMongoClientSettings());

        // Mongo DB Factory
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoDatabaseName);
    }

    private MongoClientSettings populateMongoClientSettings() {
        return MongoClientSettings.builder()
            .applicationName("NoQueue")
            .applyToClusterSettings(builder -> builder.hosts(mongoHosts()))
            .build();
    }

    @Bean
    MongoMappingContext mongoMappingContext() {
        MongoMappingContext mongoMappingContext = new MongoMappingContext();

        /*
        * Index auto creation is false by default.
        * It is recommended setting up indices manually in an application ready block. You may use index derivation there as well.
        * This setting is enabled in {@link com.noqapp.view.listener.NoQueueEventListener#initIndicesAfterStartup(ContextRefreshedEvent)}
        */
        mongoMappingContext.setAutoIndexCreation(false);
        return mongoMappingContext;
    }

    @Bean
    MappingMongoConverter mongoConverter() {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()), mongoMappingContext());
        converter.setTypeMapper(mongoTypeMapper());
        return converter;
    }

    @Bean
    MongoTypeMapper mongoTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    @Bean
    List<ServerAddress> mongoHosts() {
        List<ServerAddress> serverAddresses = new ArrayList<>();

        if (StringUtils.isNotBlank(mongoReplicaSet)) {
            String[] mongoInternetAddresses = mongoReplicaSet.split(",");
            for (String mongoInternetAddress : mongoInternetAddresses) {
                String[] mongoIpAndPort = mongoInternetAddress.split(":");
                ServerAddress serverAddress = new ServerAddress(mongoIpAndPort[0], Integer.parseInt(mongoIpAndPort[1]));
                serverAddresses.add(serverAddress);
            }
        } else {
            throw new RuntimeException("No Mongo Seed(s) listed");
        }

        return serverAddresses;
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
