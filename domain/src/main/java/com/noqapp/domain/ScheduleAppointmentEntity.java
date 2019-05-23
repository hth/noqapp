package com.noqapp.domain;

import com.noqapp.domain.types.AppointmentStatusEnum;

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
    /* Business name with address and phone makes it a unique store. */
    @CompoundIndex(name = "schedule_appointment_qr_dy_idx", def = "{'QR': 1, 'DY': 1}", unique = false, background = true),
    @CompoundIndex(name = "schedule_appointment_qid_idx", def = "{'QID': 1}", unique = false, background = true),
})
public class ScheduleAppointmentEntity extends BaseEntity {

    @Field("QR")
    private String codeQR;

    @Field("DY")
    private String day;

    @Field("ST")
    private int startTime;

    @Field("ET")
    private int endTime;

    @Field("QID")
    private String qid;

    @Field("AS")
    private AppointmentStatusEnum appointmentStatus;

    /* Temp field used only for mongo aggregation framework. */
    @Field("TA")
    private int totalAppointments;

    public String getCodeQR() {
        return codeQR;
    }

    public ScheduleAppointmentEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDay() {
        return day;
    }

    public ScheduleAppointmentEntity setDay(String day) {
        this.day = day;
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

    public String getQid() {
        return qid;
    }

    public ScheduleAppointmentEntity setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public AppointmentStatusEnum getAppointmentStatus() {
        return appointmentStatus;
    }

    public ScheduleAppointmentEntity setAppointmentStatus(AppointmentStatusEnum appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
        return this;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public ScheduleAppointmentEntity setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduleAppointmentEntity.class.getSimpleName() + "[", "]")
            .add("codeQR='" + codeQR + "'")
            .add("day='" + day + "'")
            .add("startTime=" + startTime)
            .add("endTime=" + endTime)
            .add("qid='" + qid + "'")
            .add("appointmentStatus=" + appointmentStatus)
            .add("totalAppointments=" + totalAppointments)
            .toString();
    }
}
