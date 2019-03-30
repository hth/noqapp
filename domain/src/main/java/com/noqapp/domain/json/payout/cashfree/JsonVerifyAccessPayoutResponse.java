package com.noqapp.domain.json.payout.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.beans.Transient;

/**
 * hitender
 * 2019-03-30 12:11
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
public class JsonVerifyAccessPayoutResponse extends AbstractDomain {

    @JsonProperty("status")
    private String status;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("subCode")
    private String subCode;

    @JsonProperty("data")
    private JsonVerifyAccessPayoutData data;

    public String getStatus() {
        return status;
    }

    public JsonVerifyAccessPayoutResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public JsonVerifyAccessPayoutResponse setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getSubCode() {
        return subCode;
    }

    public JsonVerifyAccessPayoutResponse setSubCode(String subCode) {
        this.subCode = subCode;
        return this;
    }

    public JsonVerifyAccessPayoutData getData() {
        return data;
    }

    public JsonVerifyAccessPayoutResponse setData(JsonVerifyAccessPayoutData data) {
        this.data = data;
        return this;
    }

    @Transient
    public boolean isOk() {
        return status.equalsIgnoreCase("SUCCESS");
    }

    @Override
    public String toString() {
        return "JsonVerifyAccessPayoutResponse{" +
            "status='" + status + '\'' +
            ", reason='" + reason + '\'' +
            ", subCode='" + subCode + '\'' +
            ", data=" + data +
            '}';
    }
}
