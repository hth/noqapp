package com.noqapp.domain.json.payment.cashfree;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.beans.Transient;

/**
 * hitender
 * 2019-02-28 12:11
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
public class JsonResponse extends AbstractDomain {

    private String status;
    private String reason;

    public String getStatus() {
        return status;
    }

    public JsonResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public JsonResponse setReason(String reason) {
        this.reason = reason;
        return this;
    }

    @Transient
    public boolean isOk() {
        return status.equalsIgnoreCase("OK");
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
            "status='" + status + '\'' +
            ", reason='" + reason + '\'' +
            '}';
    }
}
