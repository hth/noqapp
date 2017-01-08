package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.token.domain.annotation.Mobile;
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

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String firebaseServerKey;
    private String authorizationKey;
    private OkHttpClient client;

    @Autowired
    public FirebaseService(
            @Value ("${firebase.server.key}")
            String firebaseServerKey
    ) {
        this.firebaseServerKey = firebaseServerKey;
        this.authorizationKey = "key=" + firebaseServerKey;
        client = new OkHttpClient();
    }

    /**
     * Called when new queue is created or first time business is approved with store.
     *
     * @param topic
     * @param message
     * @return
     */
    public boolean registerTopic(String topic, String message) {
        JsonMessage jsonMessage = new JsonMessage(topic);
        jsonMessage.getTopicData().setMessage(message);
        LOG.info("Message body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(JSON, jsonMessage.asJson());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", authorizationKey)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Error making FCM request reason={}", e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        LOG.debug("FCM success topic={} headers={} message={} body={}",
                jsonMessage.getTo(), response.headers(), response.message(), response.body());
        return response.isSuccessful();
    }

    /**
     * Sends message to topic when any change happens in queue
     *
     * @param jsonMessage
     * @return
     */
    @Mobile
    public boolean messageToTopic(JsonMessage jsonMessage) {
        LOG.info("Message body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(JSON, jsonMessage.asJson());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("Authorization", authorizationKey)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Error making FCM request reason={}", e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }

        LOG.debug("FCM success HTTP={} topic={} headers={} message={} body={}",
                response.code(), jsonMessage.getTo(), response.headers(), response.message(), response.body());
        return response.isSuccessful();
    }
}
