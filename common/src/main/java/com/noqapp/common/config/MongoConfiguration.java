package com.noqapp.common.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
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

    private Mongo mongo() {
        return new MongoClient(getMongoServers());
    }

    private List<ServerAddress> getMongoServers() {
        List<ServerAddress> serverAddresses = new ArrayList<>();

        if (StringUtils.isNotBlank(mongoReplicaSet)) {
            String[] mongoInternetAddresses = mongoReplicaSet.split(",");
            for (String mongoInternetAddress : mongoInternetAddresses) {
                String[] mongoIpAndPort = mongoInternetAddress.split(":");
                ServerAddress serverAddress = new ServerAddress(mongoIpAndPort[0], Integer.valueOf(mongoIpAndPort[1]));
                serverAddresses.add(serverAddress);
            }
        } else {
            throw new RuntimeException("No Mongo Server listed");
        }

        return serverAddresses;
    }

    /**
     * Template ready to use to operate on the database
     *
     * @return Mongo Template ready to use
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongo(), mongoDatabaseName);

        mongoTemplate.setReadPreference(ReadPreference.nearest());
        mongoTemplate.setWriteConcern(WriteConcern.JOURNALED);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);

        return mongoTemplate;
    }
}

















