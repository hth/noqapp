package com.noqapp.common.config;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 11/17/17 10:04 PM
 */
@Configuration
public class OkHttpClientConfiguration {

    @Bean
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }
}
