package com.noqapp.service;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 6/17/18 2:14 PM
 */
@Service
public class BusinessCustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerService.class);

    private BusinessCustomerManager businessCustomerManager;
    private UserProfileManager userProfileManager;
    private QueueManager queueManager;
    private BizNameManager bizNameManager;

    @Autowired
    public BusinessCustomerService(
        BusinessCustomerManager businessCustomerManager,
        UserProfileManager userProfileManager,
        QueueManager queueManager,
        BizNameManager bizNameManager
    ) {
        this.businessCustomerManager = businessCustomerManager;
        this.userProfileManager = userProfileManager;
        this.queueManager = queueManager;
        this.bizNameManager = bizNameManager;
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

    @Mobile
    public boolean addAuthorizedUserForDoingBusiness(String inviteCode, String qid) {
        if (StringUtils.isNotBlank(inviteCode)) {
            switch (inviteCode.toUpperCase()) {
                case "CSD@GURUGRAM":
                    BizNameEntity bizName = bizNameManager.getById("5eb3b9c0017c222cd473dded");
                    if (null != bizName) {
                        BusinessCustomerEntity businessCustomer = new BusinessCustomerEntity(qid, bizName.getId(), qid);
                        businessCustomerManager.save(businessCustomer);
                        LOG.info("Authorized user created successfully qid={} bizName={} bizNameId={}", qid, bizName.getBusinessName(), bizName.getId());
                    }
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }
}
