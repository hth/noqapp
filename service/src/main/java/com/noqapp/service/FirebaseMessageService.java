package com.noqapp.service;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.fcm.JsonMessage;

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
import java.util.Objects;

/**
 * User: hitender
 * Date: 8/6/17 11:02 AM
 */
@Service
public class FirebaseMessageService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseMessageService.class);

    private String authorizationKey;
    private OkHttpClient okHttpClient;

    @Autowired
    public FirebaseMessageService(
        @Value("${firebase.server.key}")
        String firebaseServerKey,

        OkHttpClient okHttpClient
    ) {
        this.authorizationKey = "key=" + firebaseServerKey;

        this.okHttpClient = okHttpClient;
    }

    /** Sends message to topic when any change happens in queue. */
    @Mobile
    public boolean messageToTopic(JsonMessage jsonMessage) {
        LOG.info("Sending FCM message with body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(jsonMessage.asJson(), Constants.JSON);
        Request request = new Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader("Authorization", authorizationKey)
            .post(body)
            .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (UnknownHostException e) {
            LOG.error("Failed connecting to FCM host while making FCM request {} reason={}", jsonMessage, e.getLocalizedMessage(), e);
            return false;
        } catch (IOException e) {
            LOG.error("Failed making FCM request {} reason={}", jsonMessage, e.getLocalizedMessage(), e);
            return false;
        } finally {
            if (null != response) {
                Objects.requireNonNull(response.body()).close();
            }
        }

//        LOG.info("FCM success HTTP={} topic/token={} headers={} message={} body={}",
//            response.code(),
//            jsonMessage.getTo(),
//            response.headers(),
//            response.message(),
//            response.body());

        LOG.debug("FCM success HTTP=\"{}\" topic/token=\"{}\"", response.code(), jsonMessage.getTo());
        return response.isSuccessful();
    }
}
