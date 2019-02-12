package com.noqapp.service;

import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * User: hitender
 * Date: 11/19/16 12:45 AM
 */
@SuppressWarnings({
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

    public UserProfileEntity findOneByMail(String mail) {
        return userProfileManager.findOneByMail(mail);
    }

    public UserProfileEntity findByQueueUserId(String qid) {
        return userProfileManager.findByQueueUserId(qid);
    }

    public UserProfileEntity checkUserExistsByPhone(String phone) {
        return userProfileManager.findOneByPhone(phone);
    }

    public UserProfileEntity forProfilePreferenceFindByQueueUserId(String qid) {
        return userProfileManager.forProfilePreferenceFindByQueueUserId(qid);
    }

    public void updateProfile(UserProfileEntity userProfile) {
        userProfileManager.save(userProfile);
    }

    public UserPreferenceEntity loadFromProfile(UserProfileEntity userProfileEntity) {
        return userPreferenceManager.getObjectUsingUserProfile(userProfileEntity);
    }

    @Mobile
    @SuppressWarnings("unused")
    public UserProfileEntity getProfileUpdateSince(String qid, Date since) {
        return userProfileManager.getProfileUpdateSince(qid, since);
    }

    public void deleteHard(UserProfileEntity userProfile) {
        userProfileManager.deleteHard(userProfile);
    }

    public void deleteHard(UserPreferenceEntity userPreference) {
        userPreferenceManager.deleteHard(userPreference);
    }
}
