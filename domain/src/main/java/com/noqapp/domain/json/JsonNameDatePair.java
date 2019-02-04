package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 5/31/18 5:13 PM
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
public class JsonNameDatePair {

    @JsonProperty("n")
    private String name;

    @JsonProperty("d")
    private String monthYear;

    public String getName() {
        return name;
    }

    public JsonNameDatePair setName(String name) {
        this.name = name;
        return this;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public JsonNameDatePair setMonthYear(String monthYear) {
        this.monthYear = monthYear;
        return this;
    }

    @Override
    public String toString() {
        return "JsonNameDatePair{" +
                "name='" + name + '\'' +
                ", monthYear='" + monthYear + '\'' +
                '}';
    }
}
