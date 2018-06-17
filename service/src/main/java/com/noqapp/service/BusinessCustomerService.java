package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.repository.BusinessCustomerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 6/17/18 2:14 PM
 */
@Service
public class BusinessCustomerService {

    private BusinessCustomerManager businessCustomerManager;
    private BizService bizService;
    private AccountService accountService;

    @Autowired
    public BusinessCustomerService(
            BusinessCustomerManager businessCustomerManager,
            BizService bizService,
            AccountService accountService
    ) {
        this.businessCustomerManager = businessCustomerManager;
        this.bizService = bizService;
        this.accountService = accountService;
    }

    /**
     * Business Customer Id mapping with NoQueue QID.
     */
    public void addBusinessCustomer(String qid, String codeQR, String businessCustomerId) {
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        businessCustomerManager.save(new BusinessCustomerEntity(
                qid,
                bizStore.getBizName().getId(),
                businessCustomerId
        ));
    }

    public UserProfileEntity findByBusinessCustomerId(String businessCustomerId, String codeQR) {
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        BusinessCustomerEntity businessCustomer = businessCustomerManager.findOne(businessCustomerId, bizStore.getBizName().getId());
        if (null == businessCustomer) {
            return null;
        }
        return accountService.findProfileByQueueUserId(businessCustomer.getQueueUserId());
    }


}
