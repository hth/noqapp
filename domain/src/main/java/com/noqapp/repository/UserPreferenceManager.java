package com.noqapp.repository;

import com.noqapp.domain.UserPreferenceEntity;

/**
 * User: hitender
 * Date: 11/19/16 1:54 AM
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {

    UserPreferenceEntity findById(String id);

    UserPreferenceEntity findByQueueUserId(String qid);
}

