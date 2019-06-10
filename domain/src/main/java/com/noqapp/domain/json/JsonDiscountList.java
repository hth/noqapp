package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-10 13:07
 */
@SuppressWarnings ({
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
public class JsonDiscountList extends AbstractDomain {

    @JsonProperty("ds")
    private List<JsonDiscount> discounts = new ArrayList<>();

    public List<JsonDiscount> getDiscounts() {
        return discounts;
    }

    public JsonDiscountList setDiscounts(List<JsonDiscount> discounts) {
        this.discounts = discounts;
        return this;
    }

    public JsonDiscountList addDiscount(JsonDiscount discount) {
        this.discounts.add(discount);
        return this;
    }
}
