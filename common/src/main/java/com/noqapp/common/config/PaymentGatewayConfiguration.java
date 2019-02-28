package com.noqapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 2019-02-28 09:40
 */
@Configuration
public class PaymentGatewayConfiguration {

    @Value("${cashfree.api.id}")
    private String cashfreeApiId;

    @Value("${cashfree.secretKey}")
    private String cashfreeSecretKey;

    @Bean
    public Map<String, String> cashfreeGateway() {
        return new HashMap<String, String>() {{
            put("api", cashfreeApiId);
            put("secretKey", cashfreeSecretKey);
        }};
    }
}
