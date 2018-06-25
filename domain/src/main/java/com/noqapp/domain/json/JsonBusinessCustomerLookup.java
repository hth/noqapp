package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

/**
 * hitender
 * 6/17/18 3:46 PM
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
public class JsonBusinessCustomerLookup extends AbstractDomain {

    @JsonProperty ("p")
    private String customerPhone;

    @JsonProperty("bc")
    private String businessCustomerId;

    @JsonProperty("qr")
    private String codeQR;

    public String getCustomerPhone() {
        return customerPhone;
    }

    public JsonBusinessCustomerLookup setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public String getBusinessCustomerId() {
        return businessCustomerId;
    }

    public JsonBusinessCustomerLookup setBusinessCustomerId(String businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonBusinessCustomerLookup setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }
}
