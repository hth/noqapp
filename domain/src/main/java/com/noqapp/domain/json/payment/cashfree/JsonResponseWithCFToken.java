package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.SkipPaymentGatewayEnum;

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
public class JsonResponseWithCFToken extends AbstractDomain {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cftoken")
    private String cftoken;

    /** Order Amount for App to use when hitting Gateway. */
    @JsonProperty("orderAmount")
    private String orderAmount;

    @JsonProperty("spg")
    private SkipPaymentGatewayEnum skipPaymentGateway = SkipPaymentGatewayEnum.NO;

    public String getStatus() {
        return status;
    }

    public JsonResponseWithCFToken setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JsonResponseWithCFToken setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCftoken() {
        return cftoken;
    }

    public JsonResponseWithCFToken setCftoken(String cftoken) {
        this.cftoken = cftoken;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public JsonResponseWithCFToken setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
        return this;
    }

    public SkipPaymentGatewayEnum getSkipPaymentGateway() {
        return skipPaymentGateway;
    }

    public JsonResponseWithCFToken setSkipPaymentGateway(SkipPaymentGatewayEnum skipPaymentGateway) {
        this.skipPaymentGateway = skipPaymentGateway;
        return this;
    }

    @Override
    public String toString() {
        return "JsonResponseWithCFToken{" +
            "status='" + status + '\'' +
            ", message='" + message + '\'' +
            ", cftoken='" + cftoken + '\'' +
            ", orderAmount='" + orderAmount + '\'' +
            ", skipPaymentGateway=" + skipPaymentGateway +
            '}';
    }
}
