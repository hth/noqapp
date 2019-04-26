package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.PaymentPermissionEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-04-27 00:36
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
public class JsonPaymentPermission extends AbstractDomain {

    @JsonProperty("pps")
    private Map<String, PaymentPermissionEnum> paymentPermissions = new HashMap<>();

    public Map<String, PaymentPermissionEnum> getPaymentPermissions() {
        return paymentPermissions;
    }

    public JsonPaymentPermission setPaymentPermissions(Map<String, PaymentPermissionEnum> paymentPermissions) {
        this.paymentPermissions = paymentPermissions;
        return this;
    }

    public JsonPaymentPermission addPaymentPermissions(String userLevel, PaymentPermissionEnum paymentPermissions) {
        this.paymentPermissions.put(userLevel, paymentPermissions);
        return this;
    }
}
