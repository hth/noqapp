package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-05-22 10:44
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
public class JsonSchedule extends AbstractDomain {

    @JsonProperty("dy")
    private String day;

    @JsonProperty("ta")
    private String totalAppointments;

    @JsonProperty("fm")
    private String from;

    @JsonProperty("ut")
    private String until;

    @JsonProperty("nm")
    private String name;

    @JsonProperty("qid")
    private String qid;

    public String getDay() {
        return day;
    }

    public JsonSchedule setDay(String day) {
        this.day = day;
        return this;
    }

    public String getTotalAppointments() {
        return totalAppointments;
    }

    public JsonSchedule setTotalAppointments(String totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public JsonSchedule setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getUntil() {
        return until;
    }

    public JsonSchedule setUntil(String until) {
        this.until = until;
        return this;
    }

    public String getName() {
        return name;
    }

    public JsonSchedule setName(String name) {
        this.name = name;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public JsonSchedule setQid(String qid) {
        this.qid = qid;
        return this;
    }
}
