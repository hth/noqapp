package com.token.repository;

import com.token.domain.UserPreferenceEntity;
import com.token.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 11/19/16 1:54 AM
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {

    UserPreferenceEntity getById(String id);

    UserPreferenceEntity getByRid(String rid);

    UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile);
}

