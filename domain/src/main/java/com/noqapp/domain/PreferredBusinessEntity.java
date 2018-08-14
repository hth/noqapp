package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 8/12/18 3:18 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "PREFERRED_BUSINESS")
@CompoundIndexes(value = {
    @CompoundIndex(name = "preferred_business_idx", def = "{'BN' : 1, 'PB' : 1}", unique = true),
})
public class PreferredBusinessEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field("PB")
    private String preferredBizNameId;

    @Field("BT")
    private BusinessTypeEnum businessType;

    @Transient
    private String preferredBusinessName;

    public PreferredBusinessEntity(String bizNameId, String preferredBizNameId, BusinessTypeEnum businessType) {
        this.bizNameId = bizNameId;
        this.preferredBizNameId = preferredBizNameId;
        this.businessType = businessType;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public String getPreferredBizNameId() {
        return preferredBizNameId;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public String getPreferredBusinessName() {
        return preferredBusinessName;
    }

    public PreferredBusinessEntity setPreferredBusinessName(String preferredBusinessName) {
        this.preferredBusinessName = preferredBusinessName;
        return this;
    }
}
