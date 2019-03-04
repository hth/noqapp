package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-02-28 14:09
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
public class JsonPurchaseToken extends AbstractDomain {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cftoken")
    private String cftoken;

    @JsonProperty("orderAmount")
    private String orderAmount;

    public String getStatus() {
        return status;
    }

    public JsonPurchaseToken setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonPurchaseToken setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCftoken() {
        return cftoken;
    }

    public JsonPurchaseToken setCftoken(String cftoken) {
        this.cftoken = cftoken;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public JsonPurchaseToken setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
        return this;
    }
}
