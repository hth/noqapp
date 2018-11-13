package com.noqapp.repository;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/19/16 12:34 AM
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
    UserProfileEntity getById(String id);

    UserProfileEntity findByQueueUserId(String qid);

    UserProfileEntity forProfilePreferenceFindByQueueUserId(String qid);

    /**
     * Used for searching user based on name. Search could be based on First Name or Last Name.
     * The list is sorted based on First Name. Displayed with format First Name, Last Name.
     *
     * @param name
     * @return
     */
    List<UserProfileEntity> searchAllByName(String name);

    UserProfileEntity findOneByMail(String email);

    /**
     * Phone number should come with country code.
     *
     * @param phone
     * @return
     */
    UserProfileEntity findOneByPhone(String phone);

    @Mobile
    UserProfileEntity getProfileUpdateSince(String qid, Date since);

    @Mobile
    void updateCountryShortName(String country, String qid);

    UserProfileEntity inviteCodeExists(String inviteCode);

    List<UserProfileEntity> findDependentProfilesByPhone(String phone);

    long countDependentProfilesByPhone(String phone);

    void addUserProfileImage(String qid, String profileImage);

    void unsetUserProfileImage(String qid);

    boolean updateDependentDetailsOnPhoneMigration(String qid, String newPhone, String countryShortName, String timeZone);

    void unsetMailOTP(String id);

    @Deprecated
    List<UserProfileEntity> findAll();
}
