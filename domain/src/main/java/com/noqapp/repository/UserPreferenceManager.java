package com.noqapp.repository;

import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;

/**
 * User: hitender
 * Date: 11/19/16 1:54 AM
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {

    UserPreferenceEntity getById(String id);

    UserPreferenceEntity getByRid(String qid);

    UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile);
}

