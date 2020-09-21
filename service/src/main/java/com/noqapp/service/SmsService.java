package com.noqapp.service;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.json.sms.textlocal.BalanceResponse;
import com.noqapp.domain.json.sms.textlocal.SendResponse;
import com.noqapp.domain.types.LocaleEnum;
import com.noqapp.domain.types.MessageCodeEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Resource;

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
    private String smsSenderName;

    private OkHttpClient okHttpClient;
    private ApiHealthService apiHealthService;
    private Environment environment;

    private boolean sendSMSTurnedOn;

    @Resource(name="communication")
    private Properties communication;

    @Autowired
    public SmsService(
        @Value("${textLocal.sms.apiKey}")
        String smsApiKey,

        @Value("${sms.sender.noqueue}")
        String smsSenderName,

        OkHttpClient okHttpClient,
        ApiHealthService apiHealthService,
        Environment environment
    ) {
        try {
            this.smsApiKey = "apikey=" + URLEncoder.encode(smsApiKey, ScrubbedInput.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed encoding sms key {}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed encoding sms key");
        }
        this.smsSenderName = smsSenderName;

        this.okHttpClient = okHttpClient;
        this.apiHealthService = apiHealthService;
        this.environment = environment;
        if (environment.getProperty("build.env").equalsIgnoreCase("prod")) {
            this.sendSMSTurnedOn = true;
        }
    }

    public int findAvailableSMS() {
        Instant start = Instant.now();
        Response response = null;
        BalanceResponse balanceResponse;
        try {
            Request request = new Request.Builder()
                .url("https://api.textlocal.in/balance/?" + smsApiKey)
                .build();
            response = okHttpClient.newCall(request).execute();

            ObjectMapper mapper = new ObjectMapper();
            balanceResponse = mapper.readValue(response.body() != null ? Objects.requireNonNull(response.body()).string() : null, BalanceResponse.class);
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
                Objects.requireNonNull(response.body()).close();
            }
        }

        return 0;
    }

    /**
     * You are registered on NoQueue. For future Doctor appointments from home download NoQueue
     * https://play.google.com/store/apps/details?id=com.noqapp.android.client
     */
    public String sendPromotionalSMS(String phoneWithCountryCode, String messageToSend) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        Response response = null;
        SendResponse sendResponse;
        try {
            String message = "&message=" + URLEncoder.encode(messageToSend, ScrubbedInput.UTF_8);
            String sender = "&sender=" + URLEncoder.encode(smsSenderName, ScrubbedInput.UTF_8);
            String numbers = "&numbers=" + URLEncoder.encode(phoneWithCountryCode, ScrubbedInput.UTF_8);

            Request request = new Request.Builder()
                .url("https://api.textlocal.in/send/?" + smsApiKey + numbers + message + sender)
                .build();

            if (sendSMSTurnedOn) {
                response = okHttpClient.newCall(request).execute();
                ObjectMapper mapper = new ObjectMapper();
                sendResponse = mapper.readValue(response.body() != null ? Objects.requireNonNull(response.body()).string() : null, SendResponse.class);
                LOG.info("SMS promotional sent {} sms=\"{}\" length={} {} {} {}",
                    phoneWithCountryCode, messageToSend, messageToSend.length(),
                    response.message(), sendResponse.getStatus(), sendResponse.getBalance());
                if (sendResponse.getStatus().equalsIgnoreCase("failure")) {
                    methodStatusSuccess = false;
                }
                return sendResponse.getStatus();
            } else {
                LOG.info("SMS sent skipped {} {}", phoneWithCountryCode, messageToSend);
                return "success";
            }
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to SMS host reason={}", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed sending SMS request reason={}", e.getLocalizedMessage(), e);
        } finally {
            apiHealthService.insert(
                "/sendPromotionalSMS",
                "sendPromotionalSMS",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);

            if (response != null) {
                Objects.requireNonNull(response.body()).close();
            }
        }

        return "failure";
    }

    public String sendTransactionalSMS(String phoneWithCountryCode, String messageToSend) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        Response response = null;
        SendResponse sendResponse;
        try {
            String message = "&message=" + URLEncoder.encode(messageToSend, ScrubbedInput.UTF_8);
            String sender = "&sender=" + URLEncoder.encode(smsSenderName, ScrubbedInput.UTF_8);
            String numbers = "&numbers=" + URLEncoder.encode(phoneWithCountryCode, ScrubbedInput.UTF_8);

            Request request = new Request.Builder()
                .url("https://api.textlocal.in/send/?" + smsApiKey + numbers + message + sender)
                .build();

            if (sendSMSTurnedOn) {
                response = okHttpClient.newCall(request).execute();
                ObjectMapper mapper = new ObjectMapper();
                sendResponse = mapper.readValue(response.body() != null ? Objects.requireNonNull(response.body()).string() : null, SendResponse.class);
                LOG.info("SMS transactional sent {} sms=\"{}\" length={} {} {} {} {}",
                    phoneWithCountryCode, messageToSend, messageToSend.length(),
                    response.message(), sendResponse.getStatus(), sendResponse.getBalance(), message);
                if (sendResponse.getStatus().equalsIgnoreCase("failure")) {
                    methodStatusSuccess = false;
                }
                return sendResponse.getStatus();
            } else {
                LOG.info("SMS sent skipped {} {}", phoneWithCountryCode, messageToSend);
                return "success";
            }
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to SMS host reason={}", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed sending SMS request reason={}", e.getLocalizedMessage(), e);
        } finally {
            apiHealthService.insert(
                "/sendTransactionalSMS",
                "sendTransactionalSMS",
                this.getClass().getName(),
                Duration.between(start, Instant.now()),
                methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);

            if (response != null) {
                Objects.requireNonNull(response.body()).close();
            }
        }

        return "failure";
    }

    public String smsMessage(MessageCodeEnum messageCode, LocaleEnum locale, Object ... args) {
        String smsTemplate = communication.getProperty(messageCode.name() + "." + messageCode.getVersion() + "." + locale.name());
        String smsMessageToUTF8 = new String(smsTemplate.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return String.format(smsMessageToUTF8, args);
    }
}
