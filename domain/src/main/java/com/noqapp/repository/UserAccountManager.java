package com.noqapp.repository;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

import java.util.Date;

/**
 * User: hitender
 * Date: 11/19/16 1:41 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity getById(String id);

    UserAccountEntity findByQueueUserId(String qid);

    UserAccountEntity findByUserId(String userId);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);

    long countRegisteredBetweenDates(Date from, Date to);

    boolean isPhoneValidated(String qid);

    boolean existWithAuth(String id);
}