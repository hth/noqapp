package com.noqapp.service;

import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BusinessCustomerPriorityManager;
import com.noqapp.repository.BusinessCustomerManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    private BusinessCustomerPriorityManager businessCustomerPriorityManager;

    @Autowired
    public BusinessCustomerService(
        BusinessCustomerManager businessCustomerManager,
        UserProfileManager userProfileManager,
        QueueManager queueManager,
        BizNameManager bizNameManager,
        BusinessCustomerPriorityManager businessCustomerPriorityManager
    ) {
        this.businessCustomerManager = businessCustomerManager;
        this.userProfileManager = userProfileManager;
        this.queueManager = queueManager;
        this.bizNameManager = bizNameManager;
        this.businessCustomerPriorityManager = businessCustomerPriorityManager;
    }

    /**
     * Business Customer Id mapping with NoQueue QID.
     */
    @Mobile
    public void addBusinessCustomer(String qid, String codeQR, String bizNameId, String businessCustomerId) {
        BusinessCustomerEntity businessCustomer = new BusinessCustomerEntity(
            qid,
            bizNameId,
            businessCustomerId,
            CustomerPriorityLevelEnum.I
        );
        businessCustomerManager.save(businessCustomer);

        /* Update queue with business customer id. */
        QueueEntity queue = queueManager.findOneWithoutState(qid, codeQR);
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
        QueueEntity queue = queueManager.findOneWithoutState(qid, codeQR);
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

    public BusinessCustomerEntity findOneByQidAndAttribute(String qid, String bizNameId, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        if (null == businessCustomerAttribute) {
            return findOneByQid(qid, bizNameId);
        } else {
            return businessCustomerManager.findOneByQidAndAttribute(qid, bizNameId, businessCustomerAttribute);
        }
    }

    @Mobile
    public BusinessCustomerEntity findOneByCustomerId(String businessCustomerId, String bizNameId) {
        return businessCustomerManager.findOneByCustomerId(businessCustomerId, bizNameId);
    }

    @Mobile
    public String addAuthorizedUserForDoingBusiness(String customerId, String bizNameId, String qid) {
        try {
            if (StringUtils.isNotBlank(customerId)) {
                BusinessCustomerEntity businessCustomer = new BusinessCustomerEntity(qid, bizNameId, customerId, CustomerPriorityLevelEnum.I);
                businessCustomerManager.save(businessCustomer);
                LOG.info("Authorized user created successfully qid={} bizNameId={}, customerId={}", qid, bizNameId, customerId);
                return businessCustomer.getId();
            }
        } catch (DuplicateKeyException e) {
            LOG.warn("Record duplicate exists for business customer {} {}", customerId, bizNameId);
        } catch (Exception e) {
            LOG.error("Error creating business customer {} {}", customerId, bizNameId);
        }

        return null;
    }

    @Mobile
    public void addBusinessCustomerAttribute(String businessCustomerId, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        businessCustomerManager.addBusinessCustomerAttribute(businessCustomerId, businessCustomerAttribute);
    }

    @Deprecated
    public void remove(BusinessCustomerEntity businessCustomer) {
        businessCustomerManager.deleteHard(businessCustomer);
    }

    public void save(BusinessCustomerEntity businessCustomer) {
        businessCustomerManager.save(businessCustomer);
    }

    public void clearBusinessCustomer(String qid, String bizNameId) {
        businessCustomerManager.clearBusinessCustomer(qid, bizNameId);
    }
}
