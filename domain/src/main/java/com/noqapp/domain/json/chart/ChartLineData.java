package com.noqapp.domain.json.chart;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

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

    @JsonProperty("d")
    private long date;

    @JsonProperty("sc")
    private String sentimentColor;

    @JsonProperty("l")
    private String location;

    public String getValue() {
        return value;
    }

    public ChartLineData setValue(String value) {
        this.value = value;
        return this;
    }

    public long getDate() {
        return date;
    }

    public ChartLineData setDate(long date) {
        this.date = date;
        return this;
    }

    public String getSentimentColor() {
        return sentimentColor;
    }

    public ChartLineData setSentimentColor(String sentimentColor) {
        this.sentimentColor = sentimentColor;
        return this;
    }

    public void populateLocation(String area, String town) {
        this.location = area + ": ";
        if (StringUtils.isNotBlank(town)) {
            this.location = area + ", " + town + ": ";
        }
    }

    @Override
    public String toString() {
        return "ChartLineData{" +
            "value='" + value + '\'' +
            ", date=" + date +
            '}';
    }
}
