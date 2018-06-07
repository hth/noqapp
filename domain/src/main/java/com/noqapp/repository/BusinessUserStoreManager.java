package com.noqapp.repository;

import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.types.UserLevelEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
public interface BusinessUserStoreManager extends RepositoryManager<BusinessUserStoreEntity> {

    boolean hasAccess(String qid, String codeQR);
    boolean hasAccessUsingStoreId(String qid, String bizStoreId);

    List<BusinessUserStoreEntity> getQueues(String qid, int limit);

    long findNumberOfPeopleAssignedToQueue(String bizStoreId);

    long findNumberOfPeoplePendingApprovalToQueue(String bizStoreId);

    List<BusinessUserStoreEntity> getAllManagingStore(String bizStoreId);

    BusinessUserStoreEntity findUserManagingStoreWithUserLevel(String qid, UserLevelEnum userLevel);

    List<BusinessUserStoreEntity> findAllManagingStoreWithUserLevel(String bizStoreId, UserLevelEnum userLevel);

    long deleteAllManagingStore(String bizStoreId);

    void activateAccount(String qid, String bizNameId);

    void removeFromBusiness(String qid, String bizNameId);

    void removeFromStore(String qid, String bizStoreId);

    /* Temp code until release. */
    @Deprecated
    List<BusinessUserStoreEntity> findAll();
}
