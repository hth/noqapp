package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.QueueJoinDeniedEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.springframework.data.annotation.Transient;

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

    @JsonProperty("sd")
    private String scheduleDate;

    @JsonProperty("ta")
    private int totalAppointments;

    @JsonProperty("st")
    private int startTime;

    @JsonProperty("et")
    private int endTime;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty ("gq")
    private String guardianQid;

    @JsonProperty("as")
    private AppointmentStatusEnum appointmentStatus;

    @JsonProperty("qjd")
    private QueueJoinDeniedEnum queueJoinDenied;

    @JsonProperty("cc")
    private String chiefComplain;

    @JsonProperty("ps")
    private AppointmentStateEnum appointmentState;

    @JsonProperty("jp")
    private JsonProfile jsonProfile;

    @JsonProperty("qd")
    private JsonQueueDisplay jsonQueueDisplay;

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

    public String getScheduleDate() {
        return scheduleDate;
    }

    public JsonSchedule setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
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

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonSchedule setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getGuardianQid() {
        return guardianQid;
    }

    public JsonSchedule setGuardianQid(String guardianQid) {
        this.guardianQid = guardianQid;
        return this;
    }

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public JsonSchedule setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public QueueJoinDeniedEnum getQueueJoinDenied() {
        return queueJoinDenied;
    }

    public JsonSchedule setQueueJoinDenied(QueueJoinDeniedEnum queueJoinDenied) {
        this.queueJoinDenied = queueJoinDenied;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public JsonSchedule setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public JsonSchedule setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    public JsonProfile getJsonProfile() {
        return jsonProfile;
    }

    public JsonSchedule setJsonProfile(JsonProfile jsonProfile) {
        this.jsonProfile = jsonProfile;
        return this;
    }

    public JsonQueueDisplay getJsonQueueDisplay() {
        return jsonQueueDisplay;
    }

    public JsonSchedule setJsonQueueDisplay(JsonQueueDisplay jsonQueueDisplay) {
        this.jsonQueueDisplay = jsonQueueDisplay;
        return this;
    }

    @Transient
    public static JsonSchedule populateJsonSchedule(ScheduleAppointmentEntity scheduleAppointment, JsonProfile jsonProfile) {
        return new JsonSchedule()
            .setScheduleAppointmentId(scheduleAppointment.getId())
            .setCodeQR(scheduleAppointment.getCodeQR())
            .setScheduleDate(scheduleAppointment.getScheduleDate())
            .setStartTime(scheduleAppointment.getStartTime())
            .setEndTime(scheduleAppointment.getEndTime())
            .setQueueUserId(scheduleAppointment.getQueueUserId())
            .setGuardianQid(scheduleAppointment.getGuardianQid())
            .setAppointmentStatus(scheduleAppointment.getAppointmentStatus())
            .setQueueJoinDenied(scheduleAppointment.getQueueJoinDenied())
            .setChiefComplain(scheduleAppointment.getChiefComplain())
            .setAppointmentState(scheduleAppointment.getAppointmentState())
            .setJsonProfile(jsonProfile);
    }

    @Transient
    public static JsonSchedule populateJsonSchedule(ScheduleAppointmentEntity scheduleAppointment, JsonProfile jsonProfile, JsonQueueDisplay jsonQueueDisplay) {
        JsonSchedule jsonSchedule = populateJsonSchedule(scheduleAppointment, jsonProfile);
        jsonSchedule.setJsonQueueDisplay(jsonQueueDisplay);
        return jsonSchedule;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonSchedule.class.getSimpleName() + "[", "]")
            .add("scheduleDate='" + scheduleDate + "'")
            .add("totalAppointments=" + totalAppointments)
            .add("startTime='" + startTime + "'")
            .add("endTime='" + endTime + "'")
            .add("queueUserId='" + queueUserId + "'")
            .toString();
    }
}
