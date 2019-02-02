package com.noqapp.domain.stats;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/15/18 8:58 AM
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
public class YearlyData extends AbstractDomain {

    @JsonProperty("m")
    private int yearMonth;

    @JsonProperty("y")
    private int year;

    @JsonProperty("v")
    private int value;

    public int getYearMonth() {
        return yearMonth;
    }

    public YearlyData setYearMonth(int yearMonth) {
        this.yearMonth = yearMonth;
        return this;
    }

    public int getYear() {
        return year;
    }

    public YearlyData setYear(int year) {
        this.year = year;
        return this;
    }

    public int getValue() {
        return value;
    }

    public YearlyData setValue(int value) {
        this.value = value;
        return this;
    }
}
