package com.token.repository;

import com.token.domain.UserAuthenticationEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.annotation.Mobile;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/19/16 12:34 AM
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
    UserProfileEntity getById(String id);

    UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object);

    UserProfileEntity findByEmail(String email);

    UserProfileEntity findByReceiptUserId(String rid);

    UserProfileEntity forProfilePreferenceFindByReceiptUserId(String rid);

    UserProfileEntity findByProviderUserId(String puid);

    UserProfileEntity findByProviderUserIdOrEmail(String puid, String email);

    /**
     * Used for searching user based on name. Search could be based on First Name or Last Name.
     * The list is sorted based on First Name. Displayed with format First Name, Last Name.
     *
     * @param name
     * @return
     */
    List<UserProfileEntity> searchAllByName(String name);

    UserProfileEntity findOneByMail(String emailId);

    @Mobile
    UserProfileEntity getProfileUpdateSince(String rid, Date since);

    @Mobile
    void updateCountryShortName(String country, String rid);
}
