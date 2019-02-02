package com.noqapp.domain.stats;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/15/18 9:25 AM
 */
@SuppressWarnings ({
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewRepeatCustomers extends AbstractDomain {

    @JsonProperty("n")
    private int customerNew;

    @JsonProperty("r")
    private int customerRepeat;

    @JsonProperty("mn")
    private int monthOfYear;

    public int getCustomerNew() {
        return customerNew;
    }

    public NewRepeatCustomers setCustomerNew(int customerNew) {
        this.customerNew = customerNew;
        return this;
    }

    public int getCustomerRepeat() {
        return customerRepeat;
    }

    public NewRepeatCustomers setCustomerRepeat(int customerRepeat) {
        this.customerRepeat = customerRepeat;
        return this;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public NewRepeatCustomers setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }
}
