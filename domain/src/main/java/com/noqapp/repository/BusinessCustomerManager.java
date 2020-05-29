package com.noqapp.repository;

import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.types.BusinessCustomerAttributeEnum;

/**
 * hitender
 * 6/17/18 2:06 PM
 */
public interface BusinessCustomerManager extends RepositoryManager<BusinessCustomerEntity> {

    BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId);

    BusinessCustomerEntity findOneByQid(String qid, String bizNameId);

    BusinessCustomerEntity findOneByQidAndAttribute(String qid, String bizNameId, BusinessCustomerAttributeEnum businessCustomerAttribute);

    void addBusinessCustomerAttribute(String businessCustomerId, BusinessCustomerAttributeEnum businessCustomerAttribute);

    void clearBusinessCustomer(String qid, String bizNameId);
}
