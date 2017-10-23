package com.noqapp.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.tasks.Task;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.config.FirebaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        final String[] phoneNumber = {""};
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

        phoneNumber[0] = userRecord.getProviderData()[0].getUid();
        if (null != phoneNumber[0]) {
            return userProfilePreferenceService.checkUserExistsByPhone(phoneNumber[0]);
        }

//        Task<UserRecord> task = firebaseConfig.getFirebaseAuth().getUser(uid)
//                .addOnSuccessListener(userRecord -> {
//                    LOG.info("Successfully found user data for uid={}", userRecord.getUid());
//                    phoneNumber[0] = userRecord.getProviderData()[0].getUid();
//                })
//                .addOnFailureListener(e -> {
//                    LOG.warn("Not found user={} reason={}", uid, e.getLocalizedMessage());
//                    throw new UsernameNotFoundException("Error in retrieving user");
//                });
//
//        while (!task.isComplete()) {
//            try {
//                //TODO remove sleep method.
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                LOG.error("Thread failed on sleep for uid={} reason={}", uid, e.getLocalizedMessage());
//            }
//        }
//
//        if (null != phoneNumber[0]) {
//            return userProfilePreferenceService.checkUserExistsByPhone(phoneNumber[0]);
//        }

        return null;
    }
}
