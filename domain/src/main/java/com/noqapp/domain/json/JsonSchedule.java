package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.AppointmentStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.StringJoiner;

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

    @JsonProperty("id")
    private String scheduleAppointmentId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty("dy")
    private String day;

    @JsonProperty("ta")
    private int totalAppointments;

    @JsonProperty("st")
    private int startTime;

    @JsonProperty("et")
    private int endTime;

    @JsonProperty("nm")
    private String name;

    @JsonProperty("qid")
    private String qid;

    @JsonProperty("as")
    private AppointmentStatusEnum appointmentStatus;

    @JsonProperty("jp")
    private JsonProfile jsonProfile;

    public String getScheduleAppointmentId() {
        return scheduleAppointmentId;
    }

    public JsonSchedule setScheduleAppointmentId(String scheduleAppointmentId) {
        this.scheduleAppointmentId = scheduleAppointmentId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonSchedule setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

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

    public int getStartTime() {
        return startTime;
    }

    public JsonSchedule setStartTime(int startTime) {
        this.startTime = startTime;
        return this;
    }

    public int getEndTime() {
        return endTime;
    }

    public JsonSchedule setEndTime(int endTime) {
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

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public JsonSchedule setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public JsonProfile getJsonProfile() {
        return jsonProfile;
    }

    public JsonSchedule setJsonProfile(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonSchedule.class.getSimpleName() + "[", "]")
            .add("day='" + day + "'")
            .add("totalAppointments=" + totalAppointments)
            .add("startTime='" + startTime + "'")
            .add("endTime='" + endTime + "'")
            .add("name='" + name + "'")
            .add("qid='" + qid + "'")
            .toString();
    }
}
