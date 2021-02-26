package com.noqapp.view.form.marketplace;

import com.noqapp.domain.types.catgeory.ItemConditionEnum;
import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2/24/21 4:45 PM
 */
public class HouseholdItemMarketplaceForm extends MarketplaceForm {
    private static final Logger LOG = LoggerFactory.getLogger(HouseholdItemMarketplaceForm.class);

    @Transient
    private List<ItemConditionEnum> itemConditions = new ArrayList<>(ItemConditionEnum.itemConditionTypes);

    public List<ItemConditionEnum> getItemConditions() {
        return itemConditions;
    }

    public HouseholdItemMarketplaceForm setItemConditions(List<ItemConditionEnum> itemConditions) {
        this.itemConditions = itemConditions;
        return this;
    }
}
