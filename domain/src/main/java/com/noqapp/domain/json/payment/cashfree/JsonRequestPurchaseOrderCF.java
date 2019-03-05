package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-02-28 14:21
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
public class JsonRequestPurchaseOrderCF extends AbstractDomain {

    /** This is transaction Id. */
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("orderAmount")
    private String orderAmount;

    @JsonProperty("orderCurrency")
    private String orderCurrency = "INR";

    public String getOrderId() {
        return orderId;
    }

    public JsonRequestPurchaseOrderCF setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public JsonRequestPurchaseOrderCF setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
        return this;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public JsonRequestPurchaseOrderCF setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
        return this;
    }
}
