package com.noqapp.service;

import com.noqapp.common.config.FirebaseConfig;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: hitender
 * Date: 12/28/16 10:56 PM
 */
@Service
public class FirebaseService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseService.class);

    private FirebaseConfig firebaseConfig;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public FirebaseService(
        FirebaseConfig firebaseConfig,
        UserProfilePreferenceService userProfilePreferenceService
    ) {
        this.firebaseConfig = firebaseConfig;
        this.userProfilePreferenceService = userProfilePreferenceService;
    }

    /**
     * When user logs in through firebase phone authentication.
     *
     * @param uid
     * @return
     */
    public UserProfileEntity getUserWhenLoggedViaPhone(String uid) {
        ApiFuture<UserRecord> future = firebaseConfig.getFirebaseAuth().getUserAsync(uid);

        UserRecord userRecord;
        try {
            userRecord = future.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            LOG.error("Failed to get UserRecord for uid={} reason={}", uid, e.getLocalizedMessage(), e);
            return null;
        }

        if (null == userRecord) {
            return null;
        }

        String phoneNumber = userRecord.getProviderData()[0].getUid();
        if (StringUtils.isNotBlank(phoneNumber)) {
            LOG.info("Found phone={} for uid={}", phoneNumber, uid);
            return userProfilePreferenceService.checkUserExistsByPhone(Formatter.phoneCleanup(phoneNumber));
        }

        return null;
    }

    public void subscribeToTopic(List<String> registrationTokens, String topic) {
        try {
            TopicManagementResponse response = firebaseConfig.getFirebaseMessaging().subscribeToTopic(registrationTokens, topic);
            LOG.info("Subscribed successCount={} topic={}", response.getSuccessCount(), topic);
        } catch (FirebaseMessagingException e) {
            LOG.error("Failed subscribing {} {} reason={}", topic, registrationTokens, e.getLocalizedMessage(), e);
        }
    }

    public boolean isFirebaseUserExists(String phone, String uid) {
        try {
            UserRecord userRecord = firebaseConfig.getFirebaseAuth().getUser(uid);
            if (userRecord != null && userRecord.getPhoneNumber().contains(phone)) {
                return true;
            }
        } catch (FirebaseAuthException e) {
            LOG.error("Failed finding firebase user phone={} uid={}", phone, uid);
        }

        return false;
    }

    public void subscribeToTopic(String subscribedTopic, RegisteredDeviceEntity registeredDevice) {
        String topic = CommonUtil.buildTopic(subscribedTopic, registeredDevice.getDeviceType().name());
        subscribeToTopic(new ArrayList<>() {{ add(registeredDevice.getToken()); }}, topic);
    }
}
