package com.noqapp.domain.json.marketplace;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 3/7/21 12:09 PM
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
public class JsonMarketplaceList extends AbstractDomain {

    @JsonProperty("prs")
    private List<JsonPropertyRental> jsonPropertyRentals = new ArrayList<>();

    @JsonProperty("his")
    private List<JsonHouseholdItem> jsonHouseholdItems = new ArrayList<>();

    public List<JsonPropertyRental> getJsonPropertyRentals() {
        return jsonPropertyRentals;
    }

    public void setJsonPropertyRentals(List<JsonPropertyRental> jsonPropertyRentals) {
        this.jsonPropertyRentals = jsonPropertyRentals;
    }

    public void addJsonPropertyRentals(JsonPropertyRental jsonPropertyRental) {
        this.jsonPropertyRentals.add(jsonPropertyRental);
    }

    public List<JsonHouseholdItem> getJsonHouseholdItems() {
        return jsonHouseholdItems;
    }

    public void addJsonHouseholdItems(JsonHouseholdItem jsonHouseholdItem) {
        this.jsonHouseholdItems.add(jsonHouseholdItem);
    }
}
