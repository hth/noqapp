package com.noqapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * hitender
 * 2019-02-28 09:40
 */
@Configuration
public class PaymentGatewayConfiguration {

    @Value("${cashfree.api.id.sandbox}")
    private String sandboxCashfreeApiId;

    @Value("${cashfree.api.id.prod}")
    private String prodCashfreeApiId;

    @Value("${cashfree.secretKey.sandbox}")
    private String sandboxCashfreeSecretKey;

    @Value("${cashfree.secretKey.prod}")
    private String prodCashfreeSecretKey;

    @Value("${cashfree.payout.sandbox.clientId}")
    private String sandboxClientId;

    @Value("${cashfree.payout.prod.clientId}")
    private String prodClientId;

    @Value("${cashfree.payout.sandbox.clientSecret}")
    private String sandboxClientSecret;

    @Value("${cashfree.payout.prod.clientSecret}")
    private String prodClientSecret;

    @Bean
    public Map<String, String> cashfreeGateway(Environment environment) {
        return new HashMap<String, String>() {{
            put("api",  environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodCashfreeApiId : sandboxCashfreeApiId);
            put("secretKey",  environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodCashfreeSecretKey : sandboxCashfreeSecretKey);
        }};
    }

    @Bean
    public Map<String, String> cashfreePayoutGateway(Environment environment) {
        return new HashMap<String, String>() {{
            put("clientId", environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodClientId : sandboxClientId);
            put("clientSecret", environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodClientSecret : sandboxClientSecret);
        }};
    }
}
