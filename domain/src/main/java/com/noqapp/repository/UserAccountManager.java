package com.noqapp.repository;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

/**
 * User: hitender
 * Date: 11/19/16 1:41 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity getById(String id);

    UserAccountEntity findByReceiptUserId(String rid);

    UserAccountEntity findByUserId(String mail);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);
}