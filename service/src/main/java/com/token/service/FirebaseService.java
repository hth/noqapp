package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.token.domain.json.fcm.JsonMessage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * User: hitender
 * Date: 12/28/16 10:56 PM
 */
@Service
public class FirebaseService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseService.class);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String firebaseServerKey;
    private String authorizationKey;
    private OkHttpClient client;

    @Autowired
    public FirebaseService(
            @Value("${firebase.server.key}")
            String firebaseServerKey
    ) {
        this.firebaseServerKey = firebaseServerKey;
        this.authorizationKey = "key=" + firebaseServerKey;
        client = new OkHttpClient();
    }

    public boolean registerTopic(String topic, String message) {
        JsonMessage jsonMessage = new JsonMessage(topic, message);
        LOG.info("Message body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(JSON, jsonMessage.asJson());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", authorizationKey)
                .post(body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Error making FCM request reason={}", e.getLocalizedMessage(), e);
            return false;
        }

        LOG.debug("FCM success topic={} response={}", topic, response.body());
        return response.isSuccessful();
    }

    public boolean subscribeTopic(String topic, String phoneToken) {
        return false;
    }
}
