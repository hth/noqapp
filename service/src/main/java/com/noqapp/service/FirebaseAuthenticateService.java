package com.noqapp.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.UserRecord;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.config.FirebaseConfig;
import com.noqapp.common.utils.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: hitender
 * Date: 12/28/16 10:56 PM
 */
@Service
public class FirebaseAuthenticateService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseAuthenticateService.class);

    private FirebaseConfig firebaseConfig;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public FirebaseAuthenticateService(
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
}
