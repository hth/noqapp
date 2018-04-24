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
 * 12/20/17 3:27 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "BIZ_CATEGORY")
@CompoundIndexes(value = {
        @CompoundIndex(name = "biz_category_idx", def = "{'BN': -1}", unique = false),
        @CompoundIndex(name = "biz_category_name_idx", def = "{'BN': -1, 'CN' : -1}", unique = true),
})
public class BizCategoryEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(BizCategoryEntity.class);

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field("CN")
    private String categoryName;

    @Field ("DI")
    private String displayImage;

    public String getBizNameId() {
        return bizNameId;
    }

    public BizCategoryEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BizCategoryEntity setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public BizCategoryEntity setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }
}
