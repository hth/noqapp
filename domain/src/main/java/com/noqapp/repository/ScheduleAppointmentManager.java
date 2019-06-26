package com.noqapp.repository;

import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-22 16:21
 */
public interface ScheduleAppointmentManager extends RepositoryManager<ScheduleAppointmentEntity> {

    /** Appointment is for client. */
    List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String scheduleDate);

    /** Schedule is for merchant. */
    List<ScheduleAppointmentEntity> findScheduleForDay(String codeQR, String scheduleDate);

    List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, String startOfMonth, String endOfMonth);

    boolean cancelAppointment(String id, String qid, String codeQR);

    ScheduleAppointmentEntity updateSchedule(String id, AppointmentStatusEnum appointmentStatus, String qid, String codeQR);

    boolean doesAppointmentExists(String qid, String codeQR, String scheduleDate);

    List<ScheduleAppointmentEntity> findAllPastAppointments(String qid, int untilDaysInPast);
    List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid, int untilDaysInFuture);
    List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid);

    ScheduleAppointmentEntity findAppointment(String id, String qid, String codeQR);
}
