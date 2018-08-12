package com.noqapp.domain;

import com.noqapp.domain.types.BusinessTypeEnum;

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
@Document(collection = "PREFERRED_BIZ")
@CompoundIndexes(value = {
        @CompoundIndex(name = "preferred_biz_idx", def = "{'BN' : 1}", unique = false),
})
public class PreferredBusinessEntity extends BaseEntity {

    @Field ("BN")
    private String bizNameId;

    @Field("PB")
    private String preferredBizNameId;

    @Field("BT")
    private BusinessTypeEnum businessType;

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
}
