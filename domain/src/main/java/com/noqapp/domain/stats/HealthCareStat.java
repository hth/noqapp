package com.noqapp.domain.stats;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * hitender
 * 5/15/18 9:47 AM
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
public class HealthCareStat extends AbstractDomain {

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("pp")
    private int productPrice;

    @JsonProperty("yearly")
    private List<YearlyData> twelveMonths;

    @JsonProperty("rc")
    private NewRepeatCustomers repeatCustomers;

    public String getCodeQR() {
        return codeQR;
    }

    public HealthCareStat setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public HealthCareStat setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public List<YearlyData> getTwelveMonths() {
        return twelveMonths;
    }

    public HealthCareStat setTwelveMonths(List<YearlyData> twelveMonths) {
        this.twelveMonths = twelveMonths;
        return this;
    }

    public NewRepeatCustomers getRepeatCustomers() {
        return repeatCustomers;
    }

    public HealthCareStat setRepeatCustomers(NewRepeatCustomers repeatCustomers) {
        this.repeatCustomers = repeatCustomers;
        return this;
    }
}
