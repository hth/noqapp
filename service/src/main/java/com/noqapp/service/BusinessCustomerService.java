package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.UserProfileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 6/17/18 2:14 PM
 */
@Service
public class BusinessCustomerService {

    private BusinessCustomerManager businessCustomerManager;
    private BizStoreManager bizStoreManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public BusinessCustomerService(
            BusinessCustomerManager businessCustomerManager,
            BizStoreManager bizStoreManager,
            UserProfileManager userProfileManager
    ) {
        this.businessCustomerManager = businessCustomerManager;
        this.bizStoreManager = bizStoreManager;
        this.userProfileManager = userProfileManager;
    }

    /**
     * Business Customer Id mapping with NoQueue QID.
     */
    @Mobile
    public void addBusinessCustomer(String qid, String codeQR, String businessCustomerId) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        businessCustomerManager.save(new BusinessCustomerEntity(
                qid,
                bizStore.getBizName().getId(),
                businessCustomerId
        ));
    }

    @Mobile
    public UserProfileEntity findByBusinessCustomerId(String businessCustomerId, String codeQR) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        BusinessCustomerEntity businessCustomer = findOneByCustomerId(businessCustomerId, bizStore.getBizName().getId());
        if (null == businessCustomer) {
            return null;
        }
        return userProfileManager.findByQueueUserId(businessCustomer.getQueueUserId());
    }

    @Mobile
    public UserProfileEntity findByBusinessCustomerIdAndBizNameId(String businessCustomerId, String bizNameId) {
        BusinessCustomerEntity businessCustomer = findOneByCustomerId(businessCustomerId, bizNameId);
        if (null == businessCustomer) {
            return null;
        }
        return userProfileManager.findByQueueUserId(businessCustomer.getQueueUserId());
    }

    protected BusinessCustomerEntity findOneByQid(String qid, String bizNameId) {
       return businessCustomerManager.findOneByQid(qid, bizNameId);
    }

    @Mobile
    public BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId) {
        return businessCustomerManager.findOneByCustomerId(businessCustomerId, bizNameId);
    }
}
