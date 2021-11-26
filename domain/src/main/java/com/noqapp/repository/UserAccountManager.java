package com.noqapp.repository;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

import java.util.Date;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/19/16 1:41 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity getById(String id);

    UserAccountEntity findByQueueUserId(String qid);

    UserAccountEntity findByUserId(String userId);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);

    UserAccountEntity markAccountAsValid(String qid);

    long countRegisteredBetweenDates(Date from, Date to);

    boolean isPhoneValidated(String qid);

    boolean existWithAuth(String id);

    void updateName(String firstName, String lastName, String displayName, String qid);

    void increaseOTPCount(String qid);

    void resetOTPCount(String qid);

    Stream<UserAccountEntity> getAccountsWithLimitedAccess(AccountInactiveReasonEnum accountInactiveReason);
}
