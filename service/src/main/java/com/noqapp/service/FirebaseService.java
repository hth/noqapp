package com.noqapp.service;

import com.google.firebase.auth.UserRecord;
import com.google.firebase.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.service.config.FirebaseConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * User: hitender
 * Date: 12/28/16 10:56 PM
 */
@Service
public class FirebaseService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseService.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String authorizationKey;
    private OkHttpClient client;
    private FirebaseConfig firebaseConfig;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public FirebaseService(
            @Value ("${firebase.server.key}")
            String firebaseServerKey,

            FirebaseConfig firebaseConfig,
            UserProfilePreferenceService userProfilePreferenceService
    ) {
        this.authorizationKey = "key=" + firebaseServerKey;
        this.firebaseConfig = firebaseConfig;
        this.userProfilePreferenceService = userProfilePreferenceService;

        client = new OkHttpClient();
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

    /**
     * When user logs in through firebase phone authentication.
     *
     * @param uid
     * @return
     */
    public UserProfileEntity getUserWhenLoggedViaPhone(String uid) {
        final String[] phoneNumber = {""};
        Task<UserRecord> task = firebaseConfig.getFirebaseAuth().getUser(uid)
                .addOnSuccessListener(userRecord -> {
                    LOG.info("Successfully found user data for uid={}", userRecord.getUid());
                    phoneNumber[0] = userRecord.getProviderData()[0].getUid();
                })
                .addOnFailureListener(e -> {
                    LOG.warn("Not found user={} reason={}", uid, e.getLocalizedMessage());
                    throw new UsernameNotFoundException("Error in retrieving user");
                });

        while (!task.isComplete()) {
            try {
                //TODO remove sleep method.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Thread failed on sleep for uid={} reason={}", uid, e.getLocalizedMessage());
            }
        }

        if (null != phoneNumber[0]) {
            return userProfilePreferenceService.checkUserExistsByPhone(phoneNumber[0]);
        }

        return null;
    }
}
