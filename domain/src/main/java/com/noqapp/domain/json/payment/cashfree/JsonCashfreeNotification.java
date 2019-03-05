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

    /* Time of the transaction. */
    @JsonProperty("txTime")
    private String txTime;

    /* Message related to the transaction. Will have the reason, if payment failed. */
    @JsonProperty("txMsg")
    private String txMsg;

    /* Cashfree generated unique transaction Id. Ex: 140388038803. */
    @JsonProperty("referenceId")
    private String referenceId;

    /* Payment mode used by customer to make the payment. Ex: DEBIT_CARD, MobiKwik, etc. */
    @JsonProperty("paymentMode")
    private String paymentMode;

    @JsonProperty("signature")
    private String signature;

    /* Amount of the order. */
    @JsonProperty("orderAmount")
    private String orderAmount;

    /* Payment status for that order. Values can be : SUCCESS, FLAGGED, PENDING, FAILED, CANCELLED. */
    @JsonProperty("txStatus")
    private String txStatus;

    /* Order id for which transaction has been processed. Ex: GZ-212. */
    @JsonProperty("orderId")
    private String orderId;

    public String getTxTime() {
        return txTime;
    }

    public JsonCashfreeNotification setTxTime(String txTime) {
        this.txTime = txTime;
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

    @Override
    public String toString() {
        return "JsonCashfreeNotification{" +
            "txTime='" + txTime + '\'' +
            ", txMsg='" + txMsg + '\'' +
            ", referenceId='" + referenceId + '\'' +
            ", paymentMode='" + paymentMode + '\'' +
            ", signature='" + signature + '\'' +
            ", orderAmount='" + orderAmount + '\'' +
            ", txStatus='" + txStatus + '\'' +
            ", orderId='" + orderId + '\'' +
            '}';
    }
}
