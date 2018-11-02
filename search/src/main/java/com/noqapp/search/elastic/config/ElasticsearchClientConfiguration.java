package com.noqapp.search.elastic.config;

import org.apache.http.HttpHost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * User: hitender
 * Date: 3/9/17 8:36 AM
 */
@Configuration
public class ElasticsearchClientConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchClientConfiguration.class);

    /* Helps in migrating to new index by adding new name like v1 to v2 to string array. */
    private static final String[] INDEX_VERSION = {"v4", "v5"};

    /* Always lower case for Index and Type. */
    public static final String INDEX = "noqapp_" + INDEX_VERSION[INDEX_VERSION.length - 1];

    @Value("${elastic.host}")
    private String[] elasticHosts;

    @Value("${elastic.port}")
    private int elasticPort;

    @Bean
    public RestHighLevelClient createRestHighLevelClient() {
        HttpHost[] httpHosts = new HttpHost[elasticHosts.length];
        for (int i = 0; i < elasticHosts.length; i++) {
            try {
                InetAddress a = InetAddress.getByName(elasticHosts[i]);
                LOG.info("address {} {} {} {}", a.getHostName(), a.getHostAddress(), a.getCanonicalHostName(), a.isReachable(1000));
                httpHosts[i] = new HttpHost(a, elasticPort, "http");
            } catch (UnknownHostException e) {
                LOG.error("Reading ip address reason={}", e.getLocalizedMessage(), e);
            } catch (IOException e) {
                LOG.error("Reachable failed for ip address reason={}", e.getLocalizedMessage(), e);
            }
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    public String[] previousIndices() {
        String[] previous = new String[INDEX_VERSION.length - 1];
        System.arraycopy(INDEX_VERSION, 0, previous, 0, previous.length);
        return previous;
    }
}
