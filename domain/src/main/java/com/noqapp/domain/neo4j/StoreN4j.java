package com.noqapp.domain.neo4j;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * hitender
 * 1/19/21 4:50 PM
 */
@NodeEntity("Store")
public class StoreN4j {
    private static final Logger LOG = LoggerFactory.getLogger(StoreN4j.class);

    /* A unique constraint exists on codeQR. */
    @Id @Index(unique = true)
    private String codeQR;

    @Property("storeName")
    private String storeName;

    @Property("bizNameId")
    private String bizNameId;

    @Property("businessType")
    private BusinessTypeEnum businessType;

    public String getCodeQR() {
        return codeQR;
    }

    public StoreN4j setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getStoreName() {
        return storeName;
    }

    public StoreN4j setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public StoreN4j setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public StoreN4j setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public static StoreN4j populate(BizStoreEntity bizStore) {
        return new StoreN4j()
            .setCodeQR(bizStore.getCodeQR())
            .setStoreName(bizStore.getDisplayName())
            .setBizNameId(bizStore.getBizName().getId())
            .setBusinessType(bizStore.getBusinessType());
    }
}
