package com.noqapp.domain.json.chart;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 10/24/19 6:50 AM
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
public class ChartLineData extends AbstractDomain {

    @JsonProperty("v")
    private String value;

    @JsonProperty("n")
    private String name;

    @JsonProperty("d")
    private long date;

    @JsonProperty("a")
    private String area;

    @JsonProperty("t")
    private String town;

    public String getValue() {
        return value;
    }

    public ChartLineData setValue(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public ChartLineData setName(String name) {
        this.name = name;
        return this;
    }

    public long getDate() {
        return date;
    }

    public ChartLineData setDate(long date) {
        this.date = date;
        return this;
    }

    public String getArea() {
        return area;
    }

    public ChartLineData setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public ChartLineData setTown(String town) {
        this.town = town;
        return this;
    }
}
