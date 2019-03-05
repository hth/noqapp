package com.noqapp.service.payment;

import com.noqapp.common.config.PaymentGatewayConfiguration;
import com.noqapp.domain.json.payment.cashfree.JsonVerifyAccessResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * hitender
 * 2019-02-28 10:16
 */
@Service
public class PaymentGatewayService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

    private Map<String, String> cashfreeMap;
    private String cashfreeEndpoint;

    private OkHttpClient okHttpClient;

    @Autowired
    public PaymentGatewayService(
        @Value("${cashfree.endpoint}")
        String cashfreeEndpoint,

        OkHttpClient okHttpClient,
        PaymentGatewayConfiguration paymentGatewayConfiguration
    ) {
        this.cashfreeEndpoint = cashfreeEndpoint;
        this.okHttpClient = okHttpClient;
        this.cashfreeMap = paymentGatewayConfiguration.cashfreeGateway();
    }

    public boolean verifyCashfree() {
        RequestBody formBody = new FormBody.Builder()
            .add("appId", cashfreeMap.get("api"))
            .add("secretKey", cashfreeMap.get("secretKey"))
            .build();

        Request request = new Request.Builder()
            .url(cashfreeEndpoint + "/api/v1/credentials/verify")
            .addHeader("cache-control", "no-cache")
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .post(formBody)
            .build();
        Response response = null;
        JsonVerifyAccessResponse jsonVerifyAccessResponse;
        try {
            response = okHttpClient.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            jsonVerifyAccessResponse = mapper.readValue(response.body() != null ? response.body().string() : null, JsonVerifyAccessResponse.class);
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to FCM host while making FCM request reason={}", e.getLocalizedMessage(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Failed making FCM request reason={}", e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        LOG.debug("Cashfree success HTTP={} headers={} message={} body={}",
            response.code(),
            response.headers(),
            response.message(),
            jsonVerifyAccessResponse.toString());

        return jsonVerifyAccessResponse.isOk();
    }
}
