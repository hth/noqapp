package com.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.UserPreferenceEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.annotation.Mobile;
import com.token.repository.UserPreferenceManager;
import com.token.repository.UserProfileManager;

import java.util.Date;

/**
 * User: hitender
 * Date: 11/19/16 12:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class UserProfilePreferenceService {

    private UserProfileManager userProfileManager;
    private UserPreferenceManager userPreferenceManager;

    @Autowired
    public UserProfilePreferenceService(
            UserProfileManager userProfileManager,
            UserPreferenceManager userPreferenceManager) {

        this.userProfileManager = userProfileManager;
        this.userPreferenceManager = userPreferenceManager;
    }

    public UserProfileEntity findByEmail(String email) {
        return userProfileManager.findByEmail(email);
    }

    public UserProfileEntity findByReceiptUserId(String rid) {
        return userProfileManager.findByReceiptUserId(rid);
    }

    public UserProfileEntity forProfilePreferenceFindByReceiptUserId(String rid) {
        return userProfileManager.forProfilePreferenceFindByReceiptUserId(rid);
    }

    public UserProfileEntity findByProviderUserId(String puid) {
        return userProfileManager.findByProviderUserId(puid);
    }

    public void updateProfile(UserProfileEntity userProfile) {
        userProfileManager.save(userProfile);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    @Mobile
    @SuppressWarnings ("unused")
    public UserProfileEntity getProfileUpdateSince(String rid, Date since) {
        return userProfileManager.getProfileUpdateSince(rid, since);
    }

    public void deleteHard(UserProfileEntity userProfile) {
        userProfileManager.deleteHard(userProfile);
    }

    public void deleteHard(UserPreferenceEntity userPreference) {
        userPreferenceManager.deleteHard(userPreference);
    }
}
