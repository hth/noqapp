package com.noqapp.domain.market;

import com.noqapp.domain.types.catgeory.ItemConditionEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 2/24/21 4:33 PM
 */
@SuppressWarnings ({
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
}
