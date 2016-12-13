package com.token.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 12/13/16 10:26 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BUSINESS_USER_STORE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "business_user_store_idx", def = "{'RID': -1, 'BS': -1, 'BN' : -1}", unique = true, background = true)
})
public class BusinessUserStoreEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("BS")
    private String bizStoreId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public void setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
    }
}
