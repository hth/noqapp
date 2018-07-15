package com.noqapp.domain.analytic;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * Adds list of qid ids signed up by merchant.
 * hitender
 * 6/19/18 10:25 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "Z_CLIENT_SIGNUP_BY_MERCHANT")
@CompoundIndexes(value = {
        @CompoundIndex(name = "client_signup_by_merchant_idx", def = "{'BN': 1}", unique = false, background = true)
})
public class SignupByMerchantEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @NotNull
    @Field("BN")
    private String bizNameId;

    public String getQueueUserId() {
        return queueUserId;
    }

    public SignupByMerchantEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public SignupByMerchantEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }
}
