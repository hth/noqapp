package com.noqapp.service;

import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.QueueManager;
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
    private UserProfileManager userProfileManager;
    private QueueManager queueManager;

    @Autowired
    public BusinessCustomerService(
        BusinessCustomerManager businessCustomerManager,
        UserProfileManager userProfileManager,
        QueueManager queueManager
    ) {
        this.businessCustomerManager = businessCustomerManager;
        this.userProfileManager = userProfileManager;
        this.queueManager = queueManager;
    }

    /**
     * Business Customer Id mapping with NoQueue QID.
     */
    @Mobile
    public void addBusinessCustomer(String qid, String codeQR, String bizNameId, String businessCustomerId) {
        BusinessCustomerEntity businessCustomer = new BusinessCustomerEntity(
            qid,
            bizNameId,
            businessCustomerId
        );
        businessCustomerManager.save(businessCustomer);

        /* Update queue with business customer id. */
        QueueEntity queue = queueManager.findOneQueueByQid(qid, codeQR);
        queue.setBusinessCustomerId(businessCustomerId)
            .setBusinessCustomerIdChangeCount(businessCustomer.getVersion());
        queueManager.save(queue);
    }

    @Mobile
    public void editBusinessCustomer(String qid, String codeQR, String bizNameId, String businessCustomerId) {
        BusinessCustomerEntity businessCustomer = businessCustomerManager.findOneByQid(qid, bizNameId);
        businessCustomer.setBusinessCustomerId(businessCustomerId);
        businessCustomerManager.save(businessCustomer);

        /* Update queue with business customer id. */
        QueueEntity queue = queueManager.findOneQueueByQid(qid, codeQR);
        queue.setBusinessCustomerId(businessCustomerId)
            .setBusinessCustomerIdChangeCount(businessCustomer.getVersion());
        queueManager.save(queue);
    }

    @Mobile
    public UserProfileEntity findByBusinessCustomerIdAndBizNameId(String businessCustomerId, String bizNameId) {
        BusinessCustomerEntity businessCustomer = findOneByCustomerId(businessCustomerId, bizNameId);
        if (null == businessCustomer) {
            return null;
        }
        return userProfileManager.findByQueueUserId(businessCustomer.getQueueUserId());
    }

    public BusinessCustomerEntity findOneByQid(String qid, String bizNameId) {
        return businessCustomerManager.findOneByQid(qid, bizNameId);
    }

    @Mobile
    public BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId) {
        return businessCustomerManager.findOneByCustomerId(businessCustomerId, bizNameId);
    }
}
