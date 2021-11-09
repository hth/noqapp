package com.noqapp.domain;

import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.QueueJoinDeniedEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.StringJoiner;

/**
 * User: hitender
 * Date: 2019-05-22 16:20
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "SCHEDULE_APPOINTMENT")
@CompoundIndexes(value = {
    @CompoundIndex(name = "schedule_appointment_qr_sd_idx", def = "{'QR': 1, 'SD': 1}", unique = false, background = true),
    @CompoundIndex(name = "schedule_appointment_qid_idx", def = "{'QID': 1}", unique = false, background = true),
})
public class ScheduleAppointmentEntity extends BaseEntity {

    @Field("QR")
    private String codeQR;

    @Field("SD")
    private String scheduleDate;

    @Field("ST")
    private int startTime;

    @Field("ET")
    private int endTime;

    @Field("QID")
    private String queueUserId;

    @Field("GQ")
    private String guardianQid;

    @Field("AS")
    private AppointmentStatusEnum appointmentStatus;

    @Field("QJD")
    private QueueJoinDeniedEnum queueJoinDenied;

    @Field("CC")
    private String chiefComplain;

    /* Temp field used only for mongo aggregation framework. */
    @Field("TA")
    private int totalAppointments;

    @Field("RS")
    private int rescheduleCount;

    @Field("PS")
    private AppointmentStateEnum appointmentState;

    public String getCodeQR() {
        return codeQR;
    }

    public ScheduleAppointmentEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public ScheduleAppointmentEntity setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
        return this;
    }

    public int getStartTime() {
        return startTime;
    }

    public ScheduleAppointmentEntity setStartTime(int startTime) {
        this.startTime = startTime;
        return this;
    }

    public int getEndTime() {
        return endTime;
    }

    public ScheduleAppointmentEntity setEndTime(int endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public ScheduleAppointmentEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getGuardianQid() {
        return guardianQid;
    }

    public ScheduleAppointmentEntity setGuardianQid(String guardianQid) {
        this.guardianQid = guardianQid;
        return this;
    }

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public ScheduleAppointmentEntity setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public QueueJoinDeniedEnum getQueueJoinDenied() {
        return queueJoinDenied;
    }

    public ScheduleAppointmentEntity setQueueJoinDenied(QueueJoinDeniedEnum queueJoinDenied) {
        this.queueJoinDenied = queueJoinDenied;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public ScheduleAppointmentEntity setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public ScheduleAppointmentEntity setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public int getRescheduleCount() {
        return rescheduleCount;
    }

    public ScheduleAppointmentEntity setRescheduleCount(int rescheduleCount) {
        this.rescheduleCount = rescheduleCount;
        return this;
    }

    public AppointmentStateEnum getAppointmentState() {
        return appointmentState;
    }

    public ScheduleAppointmentEntity setAppointmentState(AppointmentStateEnum appointmentState) {
        this.appointmentState = appointmentState;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduleAppointmentEntity.class.getSimpleName() + "[", "]")
            .add("codeQR='" + codeQR + "'")
            .add("scheduleDate='" + scheduleDate + "'")
            .add("startTime=" + startTime)
            .add("endTime=" + endTime)
            .add("queueUserId='" + queueUserId + "'")
            .add("appointmentStatus=" + appointmentStatus)
            .add("totalAppointments=" + totalAppointments)
            .toString();
    }
}
