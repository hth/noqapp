package com.noqapp.search.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class ElasticsearchClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchClientConfiguration.class);

    /* Always lower case for Index and Type. */
    public static final String INDEX = "noqapp";

    @Bean
    public RestHighLevelClient createRestHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }
}
