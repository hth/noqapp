package com.noqapp.domain;

import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * Provides a place holder for businesses to link their customer id's with NoQ qid.
 * hitender
 * 6/17/18 2:02 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "BUSINESS_CUSTOMER")
@CompoundIndexes(value = {
        @CompoundIndex(name = "business_customer_idx", def = "{'QID': -1, 'BN': -1}", unique = false),
        @CompoundIndex(name = "business_customer_bb_idx", def = "{'BN': -1, 'BC': -1}", unique = true, background = true),
})
public class BusinessCustomerEntity extends BaseEntity {
    @NotNull
    @Field("QID")
    private String queueUserId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field ("BC")
    private String businessCustomerId;

    @Field("PL")
    private CustomerPriorityLevelEnum customerPriorityLevel;

    @Field("CA")
    private Set<BusinessCustomerAttributeEnum> businessCustomerAttributes = new HashSet<>();

    @Field("LC")
    private String limitBusinessCategory;

    public BusinessCustomerEntity(
        String queueUserId,
        String bizNameId,
        String businessCustomerId,
        CustomerPriorityLevelEnum customerPriorityLevel
    ) {
        this.queueUserId = queueUserId;
        this.bizNameId = bizNameId;
        this.businessCustomerId = businessCustomerId;
        this.customerPriorityLevel = customerPriorityLevel;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public BusinessCustomerEntity setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public CustomerPriorityLevelEnum getCustomerPriorityLevel() {
        return customerPriorityLevel;
    }

    public BusinessCustomerEntity setCustomerPriorityLevel(CustomerPriorityLevelEnum customerPriorityLevel) {
        this.customerPriorityLevel = customerPriorityLevel;
        return this;
    }

    public Set<BusinessCustomerAttributeEnum> getBusinessCustomerAttributes() {
        return businessCustomerAttributes;
    }

    public BusinessCustomerEntity setBusinessCustomerAttributes(Set<BusinessCustomerAttributeEnum> businessCustomerAttributes) {
        this.businessCustomerAttributes = businessCustomerAttributes;
        return this;
    }

    public BusinessCustomerEntity addBusinessCustomerAttributes(BusinessCustomerAttributeEnum businessCustomerAttribute) {
        this.businessCustomerAttributes.add(businessCustomerAttribute);
        return this;
    }

    public String getLimitBusinessCategory() {
        return limitBusinessCategory;
    }

    public BusinessCustomerEntity setLimitBusinessCategory(String limitBusinessCategory) {
        this.limitBusinessCategory = limitBusinessCategory;
        return this;
    }
}
