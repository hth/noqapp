package com.noqapp.repository;

import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.types.AppointmentStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-22 16:21
 */
public interface ScheduleAppointmentManager extends RepositoryManager<ScheduleAppointmentEntity> {

    List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, Date scheduleDate);

    List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, Date startOfMonth, Date endOfMonth);

    void cancelAppointment(String id, String qid, String codeQR);

    ScheduleAppointmentEntity updateSchedule(String id, AppointmentStatusEnum appointmentStatus, String qid, String codeQR);

    boolean doesAppointmentExists(String qid, String codeQR, Date scheduleDate);

    List<ScheduleAppointmentEntity> findAllUpComingAppointments(String qid, int untilDaysInFuture);
    List<ScheduleAppointmentEntity> findAllPastAppointments(String qid, int untilDaysInPast);

    List<ScheduleAppointmentEntity> findAllFutureAppointments(String qid);

    ScheduleAppointmentEntity findAppointment(String id, String qid, String codeQR);
}
