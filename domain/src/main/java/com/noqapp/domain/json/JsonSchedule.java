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
    private int totalAppointments;

    @JsonProperty("st")
    private String startTime;

    @JsonProperty("et")
    private String endTime;

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

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public JsonSchedule setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public JsonSchedule setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public JsonSchedule setEndTime(String endTime) {
        this.endTime = endTime;
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
