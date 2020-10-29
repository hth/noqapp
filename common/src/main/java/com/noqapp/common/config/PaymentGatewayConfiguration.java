package com.noqapp.common.config;

import com.braintreegateway.BraintreeGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayConfiguration.class);

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

    private BraintreeGateway braintreeGateway;

    @Autowired
    public PaymentGatewayConfiguration(
        @Value ("${braintree.environment}")
        String brainTreeEnvironment,

        @Value ("${braintree.merchant_id}")
        String brainTreeMerchantId,

        @Value ("${braintree.public_key}")
        String brainTreePublicKey,

        @Value ("${braintree.private_key}")
        String brainTreePrivateKey
    ) {
        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            braintreeGateway = new BraintreeGateway(
                com.braintreegateway.Environment.PRODUCTION,
                brainTreeMerchantId,
                brainTreePublicKey,
                brainTreePrivateKey
            );
            LOG.info("{} braintree gateway initialized", brainTreeEnvironment);
        } else {
            braintreeGateway = new BraintreeGateway(
                com.braintreegateway.Environment.SANDBOX,
                brainTreeMerchantId,
                brainTreePublicKey,
                brainTreePrivateKey
            );
            LOG.info("{} braintree gateway initialized", brainTreeEnvironment);
        }
    }

    @Bean
    public Map<String, String> cashfreeGateway(Environment environment) {
        return new HashMap<>() {{
            put("api",  environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodCashfreeApiId : sandboxCashfreeApiId);
            put("secretKey",  environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodCashfreeSecretKey : sandboxCashfreeSecretKey);
        }};
    }

    @Bean
    public Map<String, String> cashfreePayoutGateway(Environment environment) {
        return new HashMap<>() {{
            put("clientId", environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodClientId : sandboxClientId);
            put("clientSecret", environment.getProperty("build.env").equalsIgnoreCase("prod") ? prodClientSecret : sandboxClientSecret);
        }};
    }

    @Bean
    public BraintreeGateway braintreeGateway() {
        return braintreeGateway;
    }
}
