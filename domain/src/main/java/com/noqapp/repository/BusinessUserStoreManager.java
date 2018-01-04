package com.noqapp.repository;

import com.noqapp.domain.BusinessUserStoreEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
public interface BusinessUserStoreManager extends RepositoryManager<BusinessUserStoreEntity> {

    boolean hasAccess(String qid, String codeQR);

    List<BusinessUserStoreEntity> getQueues(String qid, int limit);

    long findNumberOfPeopleAssignedToQueue(String bizStoreId);

    long findNumberOfPeoplePendingApprovalToQueue(String bizStoreId);

    List<BusinessUserStoreEntity> getAllManagingStore(String bizStoreId);

    long deleteAllManagingStore(String bizStoreId);

    void activateAccount(String qid, String bizNameId);

    void removeFromBusiness(String qid, String bizNameId);

    void removeFromStore(String qid, String bizStoreId);
}
