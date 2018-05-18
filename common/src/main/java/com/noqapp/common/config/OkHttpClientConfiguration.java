package com.noqapp.common.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hitender
 * 11/17/17 10:04 PM
 */
@Configuration
public class OkHttpClientConfiguration {

    @Bean
    OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }
}
