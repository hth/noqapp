package com.noqapp.repository;

import com.noqapp.domain.BusinessCustomerPriorityEntity;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;

import java.util.List;

/**
 * hitender
 * 5/15/20 4:56 PM
 */
public interface BusinessCustomerPriorityManager extends RepositoryManager<BusinessCustomerPriorityEntity> {
    boolean existPriorityCode(String bizNameId, CustomerPriorityLevelEnum customerPriorityLevel);

    BusinessCustomerPriorityEntity findOne(String bizNameId, String priorityName);

    List<BusinessCustomerPriorityEntity> findAll(String bizNameId);
}
