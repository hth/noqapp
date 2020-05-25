package com.noqapp.service;

import com.noqapp.domain.BusinessCustomerPriorityEntity;
import com.noqapp.domain.json.JsonBusinessCustomerPriority;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;
import com.noqapp.domain.types.OnOffEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessCustomerPriorityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 5/15/20 5:58 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class BusinessCustomerPriorityService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerPriorityService.class);

    private BusinessCustomerPriorityManager businessCustomerPriorityManager;
    private BizNameManager bizNameManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public BusinessCustomerPriorityService(
        BusinessCustomerPriorityManager businessCustomerPriorityManager,
        BizNameManager bizNameManager,
        BizStoreManager bizStoreManager
    ) {
        this.businessCustomerPriorityManager = businessCustomerPriorityManager;
        this.bizNameManager = bizNameManager;
        this.bizStoreManager = bizStoreManager;
    }

    public void addCustomerPriority(String bizNameId, String priorityName, CustomerPriorityLevelEnum customerPriorityLevel) {
        if (!businessCustomerPriorityManager.existPriorityCode(bizNameId, customerPriorityLevel)) {
            BusinessCustomerPriorityEntity businessCustomerPriority = new BusinessCustomerPriorityEntity()
                .setBizNameId(bizNameId)
                .setPriorityName(priorityName)
                .setCustomerPriorityLevel(customerPriorityLevel);
            businessCustomerPriorityManager.save(businessCustomerPriority);
        } else {
            throw new RuntimeException("Existing Record");
        }
    }

    public List<BusinessCustomerPriorityEntity> findAll(String bizNameId) {
        return businessCustomerPriorityManager.findAll(bizNameId);
    }

    public List<JsonBusinessCustomerPriority> findAllAsJson(String bizNameId) {
        List<JsonBusinessCustomerPriority> jsonBusinessCustomerPriorities = new ArrayList<>();
        List<BusinessCustomerPriorityEntity> businessCustomerPriorities = findAll(bizNameId);
        for (BusinessCustomerPriorityEntity businessCustomerPriority : businessCustomerPriorities) {
            jsonBusinessCustomerPriorities.add(
                new JsonBusinessCustomerPriority()
                .setPriorityName(businessCustomerPriority.getPriorityName())
                .setCustomerPriorityLevel(businessCustomerPriority.getCustomerPriorityLevel())
            );
        }

        return jsonBusinessCustomerPriorities;
    }

    public void changePriorityAccess(String bizNameId, OnOffEnum onOff) {
        bizNameManager.updatePriorityAccess(bizNameId, onOff);;
    }
}
