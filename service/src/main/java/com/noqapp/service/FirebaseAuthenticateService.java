package com.noqapp.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.config.FirebaseConfig;

/**
 * User: hitender
 * Date: 12/28/16 10:56 PM
 */
@Service
public class FirebaseAuthenticateService {
    private static final Logger LOG = LoggerFactory.getLogger(FirebaseAuthenticateService.class);

    private FirebaseAuth firebaseAuth;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public FirebaseAuthenticateService(
            FirebaseConfig firebaseConfig,
            UserProfilePreferenceService userProfilePreferenceService
    ) {
        this.firebaseAuth = FirebaseAuth.getInstance(firebaseConfig.firebaseConfigTemplate());
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
        Task<UserRecord> task = firebaseAuth.getUser(uid)
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
