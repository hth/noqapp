package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessCustomerPriorityEntity;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;
import com.noqapp.domain.types.OnOffEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 5/15/20 7:10 PM
 */
public class BusinessCustomerPriorityForm {

    private ScrubbedInput priorityName;
    private CustomerPriorityLevelEnum priorityLevel;

    private OnOffEnum priorityAccess;

    /** Pre-filled. */
    private Map<String, String> onOffTypes = OnOffEnum.asMapWithNameAsKey();
    private Map<String, String> customerPriorityLevels = CustomerPriorityLevelEnum.asMapWithNameAsKey();

    /** Existing priorities. */
    public List<BusinessCustomerPriorityEntity> businessCustomerPriorities = new ArrayList<>();

    private String bizNameId;

    public ScrubbedInput getPriorityName() {
        return priorityName;
    }

    public BusinessCustomerPriorityForm setPriorityName(ScrubbedInput priorityName) {
        this.priorityName = priorityName;
        return this;
    }

    public CustomerPriorityLevelEnum getPriorityLevel() {
        return priorityLevel;
    }

    public BusinessCustomerPriorityForm setPriorityLevel(CustomerPriorityLevelEnum priorityLevel) {
        this.priorityLevel = priorityLevel;
        return this;
    }

    public OnOffEnum getPriorityAccess() {
        return priorityAccess;
    }

    public BusinessCustomerPriorityForm setPriorityAccess(OnOffEnum priorityAccess) {
        this.priorityAccess = priorityAccess;
        return this;
    }

    public List<BusinessCustomerPriorityEntity> getBusinessCustomerPriorities() {
        return businessCustomerPriorities;
    }

    public BusinessCustomerPriorityForm setBusinessCustomerPriorities(List<BusinessCustomerPriorityEntity> businessCustomerPriorities) {
        this.businessCustomerPriorities = businessCustomerPriorities;
        return this;
    }

    public BusinessCustomerPriorityForm addBusinessCustomerPriority(BusinessCustomerPriorityEntity businessCustomerPriority) {
        this.businessCustomerPriorities.add(businessCustomerPriority);
        return this;
    }

    public Map<String, String> getOnOffTypes() {
        return onOffTypes;
    }

    public BusinessCustomerPriorityForm setOnOffTypes(Map<String, String> onOffTypes) {
        this.onOffTypes = onOffTypes;
        return this;
    }

    public Map<String, String> getCustomerPriorityLevels() {
        return customerPriorityLevels;
    }

    public BusinessCustomerPriorityForm setCustomerPriorityLevels(Map<String, String> customerPriorityLevels) {
        this.customerPriorityLevels = customerPriorityLevels;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BusinessCustomerPriorityForm setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }
}
