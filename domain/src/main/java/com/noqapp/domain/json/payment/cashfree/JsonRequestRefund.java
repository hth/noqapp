package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 2019-03-05 10:05
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
public class JsonRequestRefund extends AbstractDomain {

    /* CashFree reference Id. */
    @JsonProperty("referenceId")
    private String referenceId;

    /* Amount to be refunded. Should be lesser than equal to transaction amount. */
    @JsonProperty("refundAmount")
    private String refundAmount;

    /* A refund note for your reference. */
    @JsonProperty("refundNote")
    private String refundNote;

    /* A merchant generated unique key to identify this refund. Will be auto generated if left blank. */
    @JsonProperty("merchantRefundId")
    private String merchantRefundId;

    public String getReferenceId() {
        return referenceId;
    }

    public JsonRequestRefund setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public JsonRequestRefund setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
        return this;
    }

    public String getRefundNote() {
        return refundNote;
    }

    public JsonRequestRefund setRefundNote(String refundNote) {
        this.refundNote = refundNote;
        return this;
    }

    public String getMerchantRefundId() {
        return merchantRefundId;
    }

    public JsonRequestRefund setMerchantRefundId(String merchantRefundId) {
        this.merchantRefundId = merchantRefundId;
        return this;
    }
}
