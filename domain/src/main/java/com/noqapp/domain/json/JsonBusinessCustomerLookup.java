package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.ScrubbedInput;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private ScrubbedInput customerPhone;

    @JsonProperty("bc")
    private ScrubbedInput businessCustomerId;

    @JsonProperty("qr")
    private ScrubbedInput codeQR;

    @JsonProperty("cn")
    private ScrubbedInput customerName;

    @JsonProperty("ru")
    private boolean registeredUser;

    public ScrubbedInput getCustomerPhone() {
        return customerPhone;
    }

    public JsonBusinessCustomerLookup setCustomerPhone(ScrubbedInput customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public ScrubbedInput getBusinessCustomerId() {
        return businessCustomerId;
    }

    public JsonBusinessCustomerLookup setBusinessCustomerId(ScrubbedInput businessCustomerId) {
        this.businessCustomerId = businessCustomerId;
        return this;
    }

    public ScrubbedInput getCodeQR() {
        return codeQR;
    }

    public JsonBusinessCustomerLookup setCodeQR(ScrubbedInput codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public ScrubbedInput getCustomerName() {
        return customerName;
    }

    public JsonBusinessCustomerLookup setCustomerName(ScrubbedInput customerName) {
        this.customerName = customerName;
        return this;
    }

    public boolean isRegisteredUser() {
        return registeredUser;
    }

    public JsonBusinessCustomerLookup setRegisteredUser(boolean registeredUser) {
        this.registeredUser = registeredUser;
        return this;
    }
}
