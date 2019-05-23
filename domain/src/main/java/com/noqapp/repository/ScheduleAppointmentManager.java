package com.noqapp.repository;

import com.noqapp.domain.ScheduleAppointmentEntity;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-22 16:21
 */
public interface ScheduleAppointmentManager extends RepositoryManager<ScheduleAppointmentEntity> {

    List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String day);

    List<ScheduleAppointmentEntity> findBookedAppointmentsForMonth(String codeQR, Date startOfMonth, Date endOfMonth);

    void cancelAppointment(String id, String qid, String codeQR);
}
