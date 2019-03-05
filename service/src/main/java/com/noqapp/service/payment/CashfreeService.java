package com.noqapp.service.payment;

import com.noqapp.common.config.PaymentGatewayConfiguration;
import com.noqapp.common.utils.Constants;
import com.noqapp.domain.json.payment.cashfree.JsonRequestPurchaseOrderCF;
import com.noqapp.domain.json.payment.cashfree.JsonResponseRefund;
import com.noqapp.domain.json.payment.cashfree.JsonResponseWithCFToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * hitender
 * 2019-02-28 11:10
 */
@Service
public class CashfreeService {
    private static final Logger LOG = LoggerFactory.getLogger(CashfreeService.class);

    private Map<String, String> cashfreeMap;
    private String cashfreeEndpoint;

    private OkHttpClient okHttpClient;

    @Autowired
    public CashfreeService(
        @Value("${cashfree.endpoint}")
        String cashfreeEndpoint,

        OkHttpClient okHttpClient,
        PaymentGatewayConfiguration paymentGatewayConfiguration
    ) {
        this.cashfreeEndpoint = cashfreeEndpoint;
        this.okHttpClient = okHttpClient;
        this.cashfreeMap = paymentGatewayConfiguration.cashfreeGateway();
    }

    public JsonResponseWithCFToken createTokenForPurchaseOrder(JsonRequestPurchaseOrderCF jsonRequestPurchaseOrderCF) {
        LOG.info("Sending FCM message with body={}", jsonRequestPurchaseOrderCF.asJson());

        RequestBody body = RequestBody.create(Constants.JSON, jsonRequestPurchaseOrderCF.asJson());
        Request request = new Request.Builder()
            .url(cashfreeEndpoint + "/api/v2/cftoken/order")
            .addHeader("content-type", Constants.JSON.toString())
            .addHeader("x-client-id", cashfreeMap.get("api"))
            .addHeader("x-client-secret", cashfreeMap.get("secretKey"))
            .post(body)
            .build();
        Response response = null;
        JsonResponseWithCFToken jsonResponseWithCFToken;
        try {
            response = okHttpClient.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            jsonResponseWithCFToken = mapper.readValue(response.body() != null ? response.body().string() : null, JsonResponseWithCFToken.class);
            jsonResponseWithCFToken.setOrderAmount(jsonRequestPurchaseOrderCF.getOrderAmount());
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to FCM host while making FCM request reason={}", e.getLocalizedMessage(), e);
            return null;
        } catch (IOException e) {
            LOG.error("Failed making FCM request reason={}", e.getLocalizedMessage(), e);
            return null;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        LOG.debug("Cashfree success HTTP={} headers={} message={} body={}",
            response.code(),
            response.headers(),
            response.message(),
            jsonResponseWithCFToken.toString());

        return jsonResponseWithCFToken;
    }

    public JsonResponseRefund refundInitiatedByClient() {
        return new JsonResponseRefund();
    }
}
