package com.noqapp.search.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class ElasticsearchClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchClientConfiguration.class);

    /* This should help in migrating to new index by changing the name of version from v1 to v2. */
    private static final String INDEX_VERSION = "v2";

    /* Always lower case for Index and Type. */
    public static final String INDEX = "noqapp_" + INDEX_VERSION;

    @Value("${elastic.host}")
    private String elasticHost;

    @Value("${elastic.port}")
    private int elasticPort;

    @Bean
    public RestHighLevelClient createRestHighLevelClient() {
        LOG.info("Host={} Port={}", elasticHost, elasticPort);
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticHost, elasticPort, "http")));

        return client;
    }
}
