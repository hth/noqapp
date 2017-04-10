package com.noqapp.repository;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.domain.types.ProviderEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/19/16 1:41 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity getById(String id);

    UserAccountEntity findByReceiptUserId(String rid);

    UserAccountEntity findByUserId(String mail);

    UserAccountEntity findByProviderUserId(String providerUserId);

    UserAccountEntity findByAuthorizationCode(ProviderEnum provider, String authorizationCode);

    int inactiveNonValidatedAccount(Date pastActivationDate);

    List<UserAccountEntity> findRegisteredAccountWhenRegistrationIsOff(int registrationInviteDailyLimit);

    void removeRegistrationIsOffFrom(String id);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);

    List<UserAccountEntity> findAllForBilling(int skipDocuments, int limit);

    List<UserAccountEntity> findAllTechnician();

    List<UserAccountEntity> getLastSoManyRecords(int limit);
}