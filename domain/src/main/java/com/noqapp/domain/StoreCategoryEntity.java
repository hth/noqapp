package com.noqapp.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 3/22/18 9:43 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "STORE_CATEGORY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "store_category_idx", def = "{'BN': -1, 'BS': -1}", unique = false),
        @CompoundIndex(name = "store_category_name_idx", def = "{'BN': -1, 'BS': -1, 'CN' : -1}", unique = true),
})
public class StoreCategoryEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryEntity.class);

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field("BS")
    private String bizStoreId;

    @NotNull
    @Field("CN")
    private String categoryName;

    public String getBizNameId() {
        return bizNameId;
    }

    public StoreCategoryEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public StoreCategoryEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public StoreCategoryEntity setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }
}
