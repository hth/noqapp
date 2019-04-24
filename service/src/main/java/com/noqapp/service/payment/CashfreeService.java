package com.noqapp.service.payment;

import com.noqapp.common.config.PaymentGatewayConfiguration;
import com.noqapp.common.utils.Constants;
import com.noqapp.domain.json.payment.cashfree.JsonRequestPurchaseOrderCF;
import com.noqapp.domain.json.payment.cashfree.JsonRequestRefund;
import com.noqapp.domain.json.payment.cashfree.JsonResponseRefund;
import com.noqapp.domain.json.payment.cashfree.JsonResponseWithCFToken;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
        Environment environment,
        PaymentGatewayConfiguration paymentGatewayConfiguration
    ) {
        this.cashfreeEndpoint = cashfreeEndpoint;
        this.okHttpClient = okHttpClient;
        this.cashfreeMap = paymentGatewayConfiguration.cashfreeGateway(environment);
    }

    public JsonResponseWithCFToken createTokenForPurchaseOrder(JsonRequestPurchaseOrderCF jsonRequestPurchaseOrderCF) {
        LOG.info("Send request to create token message with body={}", jsonRequestPurchaseOrderCF.asJson());

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
            LOG.error("Failed connecting to host while requesting for token reason={}", e.getLocalizedMessage(), e);
            return null;
        } catch (IOException e) {
            LOG.error("Failed making request for token reason={}", e.getLocalizedMessage(), e);
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

    public JsonResponseRefund refundInitiatedByClient(JsonRequestRefund jsonRequestRefund) {
        LOG.info("Send request for refund message with body={}", jsonRequestRefund.asJson());

        RequestBody formBody = new FormBody.Builder()
            .add("appId", cashfreeMap.get("api"))
            .add("secretKey", cashfreeMap.get("secretKey"))
            .add("referenceId", jsonRequestRefund.getReferenceId())
            .add("refundAmount", jsonRequestRefund.getRefundAmount())
            .add("refundNote", jsonRequestRefund.getRefundNote())
            .build();

        Request request = new Request.Builder()
            .url(cashfreeEndpoint + "/api/v1/order/refund")
            .addHeader("cache-control", "no-cache")
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .post(formBody)
            .build();
        Response response = null;
        JsonResponseRefund jsonResponseRefund;
        try {
            response = okHttpClient.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            jsonResponseRefund = mapper.readValue(response.body() != null ? response.body().string() : null, JsonResponseRefund.class);
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to host while requesting for refund reason={}", e.getLocalizedMessage(), e);
            return null;
        } catch (IOException e) {
            LOG.error("Failed making request for refund reason={}", e.getLocalizedMessage(), e);
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
            jsonResponseRefund.toString());

        return jsonResponseRefund;
    }
}
