package com.noqapp.domain.json.marketplace;

import com.noqapp.domain.types.catgeory.HouseholdItemCategoryEnum;
import com.noqapp.domain.types.catgeory.ItemConditionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 3/7/21 12:06 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonHouseholdItem extends JsonMarketplace {

    @JsonProperty("ic")
    private ItemConditionEnum itemCondition;

    @JsonProperty("hc")
    private HouseholdItemCategoryEnum householdItemCategory;

    public ItemConditionEnum getItemCondition() {
        return itemCondition;
    }

    public JsonHouseholdItem setItemCondition(ItemConditionEnum itemCondition) {
        this.itemCondition = itemCondition;
        return this;
    }

    public HouseholdItemCategoryEnum getHouseholdItemCategory() {
        return householdItemCategory;
    }

    public JsonHouseholdItem setHouseholdItemCategory(HouseholdItemCategoryEnum householdItemCategory) {
        this.householdItemCategory = householdItemCategory;
        return this;
    }
}
