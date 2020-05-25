package com.noqapp.domain;

import com.noqapp.domain.types.CustomerPriorityLevelEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * Feature turned on by business admin. When turned on their customer gets special display in queue.
 * All store 'authorizedUser' turns on automatically and then every store validates if the person is authorized.
 * hitender
 * 5/15/20 12:50 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "BUSINESS_CUSTOMER_PRIORITY")
@CompoundIndexes(value = {
    @CompoundIndex(name = "biz_cp_idx", def = "{'BN' : 1}", unique = false, background = true),
})
public class BusinessCustomerPriorityEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerPriorityEntity.class);

    @NotNull
    @Field("BN")
    private String bizNameId;

    @NotNull
    @Field("PL")
    private CustomerPriorityLevelEnum customerPriorityLevel;

    @NotNull
    @Field("PN")
    private String priorityName;

    public String getBizNameId() {
        return bizNameId;
    }

    public BusinessCustomerPriorityEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public BusinessCustomerPriorityEntity setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
        return this;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public BusinessCustomerPriorityEntity setPriorityName(String priorityName) {
        this.priorityName = priorityName;
        return this;
    }
}
