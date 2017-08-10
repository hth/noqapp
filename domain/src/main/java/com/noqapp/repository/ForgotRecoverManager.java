package com.noqapp.repository;

import com.noqapp.domain.ForgotRecoverEntity;

/**
 * User: hitender
 * Date: 5/3/17 12:43 PM
 */
public interface ForgotRecoverManager extends RepositoryManager<ForgotRecoverEntity> {

    /**
     * Find ForgotRecoverEntity by authentication key
     *
     * @param key
     * @return
     */
    ForgotRecoverEntity findByAuthenticationKey(String key);

    /**
     * Make all the existing request invalid
     *
     * @param queueUserId
     */
    void invalidateAllEntries(String queueUserId);
}
