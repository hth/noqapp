package com.noqapp.service;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.common.config.OkHttpClientConfiguration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * User: hitender
 * Date: 8/6/17 11:02 AM
 */
@Service
public class FirebaseMessageService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseMessageService.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String authorizationKey;
    private OkHttpClient okHttpClient;

    @Autowired
    public FirebaseMessageService(
            @Value ("${firebase.server.key}")
            String firebaseServerKey,

            OkHttpClient okHttpClient
    ) {
        this.authorizationKey = "key=" + firebaseServerKey;

        this.okHttpClient = okHttpClient;
    }

    /**
     * Sends message to topic when any change happens in queue.
     *
     * @param jsonMessage
     * @return
     */
    @Mobile
    public boolean messageToTopic(JsonMessage jsonMessage) {
        LOG.info("Sending FCM message with body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(JSON, jsonMessage.asJson());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", authorizationKey)
                .post(body)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
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

        LOG.debug("FCM success HTTP={} topic/token={} headers={} message={} body={}",
                response.code(),
                jsonMessage.getTo(),
                response.headers(),
                response.message(),
                response.body());

        return response.isSuccessful();
    }
}
