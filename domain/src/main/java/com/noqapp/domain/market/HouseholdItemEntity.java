package com.noqapp.domain.market;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.domain.types.catgeory.ItemConditionEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * All fields are added to tag. Example IC_itemCondition: G_IC, P_IC, V_IC.
 * hitender
 * 2/24/21 4:33 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "MP_HOUSEHOLD_ITEM")
public class HouseholdItemEntity extends MarketplaceEntity {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemEntity.class);

    @Field("IC")
    private ItemConditionEnum itemCondition;

    public ItemConditionEnum getItemCondition() {
        return itemCondition;
    }

    public HouseholdItemEntity setItemCondition(ItemConditionEnum itemCondition) {
        this.itemCondition = itemCondition;
        return this;
    }

    @Override
    @Transient
    public String getFieldValueForTag() {
        return itemCondition.name() + UNDER_SCORE + "IC"
            + (StringUtils.isNotBlank(getTags()) ? " " + getTags() : "");
    }

    @Override
    public String[] getFieldTags() {
        return new String[] {
            itemCondition.getDescription()
        };
    }
}
