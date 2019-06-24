package com.noqapp.service;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.json.sms.textlocal.BalanceResponse;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

/**
 * User: hitender
 * Date: 2019-06-23 20:42
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class SmsService {
    private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);

    private String smsApiKey;
    private OkHttpClient okHttpClient;
    private ApiHealthService apiHealthService;

    @Autowired
    public SmsService(
        @Value("${textLocal.sms.apiKey}")
        String smsApiKey,

        OkHttpClient okHttpClient,
        ApiHealthService apiHealthService
    ) {
        try {
            this.smsApiKey = "apikey=" + URLEncoder.encode(smsApiKey, ScrubbedInput.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed encoding sms key {}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed encoding sms key");
        }
        this.okHttpClient = okHttpClient;
        this.apiHealthService = apiHealthService;
    }

    public int findAvailableSMS() {
        Instant start = Instant.now();
        Request request = new Request.Builder()
            .url("https://api.textlocal.in/balance/?" + smsApiKey)
            .build();
        Response response = null;
        BalanceResponse balanceResponse;
        try {
            response = okHttpClient.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            balanceResponse = mapper.readValue(response.body() != null ? response.body().string() : null, BalanceResponse.class);
            LOG.info("{} {}", response.message(), balanceResponse.asJson());
            return balanceResponse.getBalance().getAvailableSMS();
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to SMS host reason={}", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed making SMS request reason={}", e.getLocalizedMessage(), e);
        } finally {
            apiHealthService.insert(
                "/findAvailableSMS",
                "findAvailableSMS",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);

            if (response != null) {
                response.body().close();
            }
        }

        return 0;
    }


}
