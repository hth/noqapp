package com.noqapp.search.elastic.config;

import org.apache.http.HttpHost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class ElasticsearchClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchClientConfiguration.class);

    /* Helps in migrating to new index by adding new name like v1 to v2 to string array. */
    private static final String[] INDEX_VERSION = {"v1", "v2"};

    /* Always lower case for Index and Type. */
    public static final String INDEX = "noqueue_" + INDEX_VERSION[INDEX_VERSION.length - 1];

    @Value("${elastic.host}")
    private String elasticHost;

    @Value("${elastic.port}")
    private int elasticPort;

    @Bean
    public RestHighLevelClient createRestHighLevelClient() {
        LOG.info("Host={} Port={}", elasticHost, elasticPort);
        return new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(elasticHost, elasticPort, "http")));
    }

    public String[] previousIndices() {
        String[] previous = new String[INDEX_VERSION.length - 1];
        System.arraycopy(INDEX_VERSION, 0, previous, 0, previous.length);
        return previous;
    }
}
