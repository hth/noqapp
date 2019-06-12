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
 * Date: 2019-06-12 06:49
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
public class JsonCouponList extends AbstractDomain {

    @JsonProperty("cs")
    private List<JsonCoupon> coupons = new ArrayList<>();

    public List<JsonCoupon> getCoupons() {
        return coupons;
    }

    public JsonCouponList setCoupons(List<JsonCoupon> coupons) {
        this.coupons = coupons;
        return this;
    }

    public JsonCouponList addCoupon(JsonCoupon coupon) {
        this.coupons.add(coupon);
        return this;
    }
}
