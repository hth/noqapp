package com.noqapp.repository;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

/**
 * User: hitender
 * Date: 11/19/16 1:41 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity getById(String id);

    UserAccountEntity findByQueueUserId(String qid);

    UserAccountEntity findByUserId(String userId);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);
}