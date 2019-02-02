package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
        @CompoundIndex(name = "business_customer_idx", def = "{'QID': -1, 'BN': -1}", unique = true),
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

    public BusinessCustomerEntity(@NotNull String queueUserId, @NotNull String bizNameId, @NotNull String businessCustomerId) {
        this.queueUserId = queueUserId;
        this.bizNameId = bizNameId;
        this.businessCustomerId = businessCustomerId;
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
}
