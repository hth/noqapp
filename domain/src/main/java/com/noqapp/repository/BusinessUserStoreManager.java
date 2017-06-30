package com.noqapp.repository;

import com.noqapp.domain.BusinessUserStoreEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
public interface BusinessUserStoreManager extends RepositoryManager<BusinessUserStoreEntity> {

    boolean hasAccess(String rid, String codeQR);

    List<BusinessUserStoreEntity> getQueues(String rid, int limit);

    long findNumberOfPeopleAssignedToQueue(String storeId);

    List<BusinessUserStoreEntity> getAllQueueManagers(String storeId);
}
