package com.noqapp.service.config;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 11/17/17 10:04 PM
 */
@Service
public class NetworkService {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkService.class);

    private OkHttpClient okHttpClient;

    @Autowired
    public NetworkService() {
        this.okHttpClient = new OkHttpClient();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
