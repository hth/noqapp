package com.noqapp.repository;

import com.noqapp.domain.EmailValidateEntity;

/**
 * User: hitender
 * Date: 11/25/16 10:04 AM
 */
public interface EmailValidateManager extends RepositoryManager<EmailValidateEntity> {
    EmailValidateEntity findByAuthenticationKey(String auth);

    void invalidateAllEntries(String queueUserId);

    EmailValidateEntity find(String email);
}
