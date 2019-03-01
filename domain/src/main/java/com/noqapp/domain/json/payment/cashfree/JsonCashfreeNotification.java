package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-03-01 17:09
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
public class JsonCashfreeNotification extends AbstractDomain {

    @JsonProperty("xTime")
    private String xTime;

    @JsonProperty("txMsg")
    private String txMsg;

    @JsonProperty("referenceId")
    private String referenceId;

    @JsonProperty("paymentMode")
    private String paymentMode;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("orderAmount")
    private String orderAmount;

    @JsonProperty("txStatus")
    private String txStatus;

    @JsonProperty("orderId")
    private String orderId;

    public String getxTime() {
        return xTime;
    }

    public JsonCashfreeNotification setxTime(String xTime) {
        this.xTime = xTime;
        return this;
    }

    public String getTxMsg() {
        return txMsg;
    }

    public JsonCashfreeNotification setTxMsg(String txMsg) {
        this.txMsg = txMsg;
        return this;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public JsonCashfreeNotification setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public JsonCashfreeNotification setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public JsonCashfreeNotification setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public JsonCashfreeNotification setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
        return this;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public JsonCashfreeNotification setTxStatus(String txStatus) {
        this.txStatus = txStatus;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public JsonCashfreeNotification setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }
}
