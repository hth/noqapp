package com.noqapp.repository;

import com.noqapp.domain.BusinessCustomerEntity;

/**
 * hitender
 * 6/17/18 2:06 PM
 */
public interface BusinessCustomerManager extends RepositoryManager<BusinessCustomerEntity> {

    BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId);

    BusinessCustomerEntity findOneByQid(String qid, String bizNameId);
}
