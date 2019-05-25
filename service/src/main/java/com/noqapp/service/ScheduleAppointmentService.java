package com.noqapp.service;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfile;
import com.noqapp.domain.json.JsonSchedule;
import com.noqapp.domain.json.JsonScheduleList;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.exceptions.AppointmentBookingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-23 23:59
 */
@Service
public class ScheduleAppointmentService {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleAppointmentService.class);

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private UserProfileManager userProfileManager;
    private UserAccountManager userAccountManager;

    @Autowired
    public ScheduleAppointmentService(
        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        UserProfileManager userProfileManager,
        UserAccountManager userAccountManager
    ) {
        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.userProfileManager = userProfileManager;
        this.userAccountManager = userAccountManager;
    }

    @Mobile
    public JsonSchedule bookAppointment(String guardianQid, JsonSchedule jsonSchedule) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(jsonSchedule.getCodeQR());
        Date date = DateUtil.convertToDate(jsonSchedule.getScheduleDate(), bizStore.getTimeZone());
        StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), DateUtil.getDayOfWeekFromDate(date, bizStore.getTimeZone()));
        if (storeHour.getStartHour() > jsonSchedule.getStartTime()) {
            LOG.warn("Supplied start time is beyond range {} {} {} {}", jsonSchedule.getStartTime(), storeHour.getStartHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " opens at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getStartHour()));
        }

        if (storeHour.getEndHour() < jsonSchedule.getEndTime()) {
            LOG.warn("Supplied end time is beyond range {} {} {} {}", jsonSchedule.getEndTime(), storeHour.getEndHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " closes at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getEndHour()));
        }

        ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
            .setCodeQR(jsonSchedule.getCodeQR())
            .setScheduleDate(jsonSchedule.getScheduleDate())
            .setStartTime(jsonSchedule.getStartTime())
            .setEndTime(jsonSchedule.getEndTime())
            .setQueueUserId(jsonSchedule.getQueueUserId())
            .setGuardianQid(guardianQid)
            .setAppointmentStatus(AppointmentStatusEnum.U);

        scheduleAppointmentManager.save(scheduleAppointment);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        return new JsonSchedule()
            .setScheduleAppointmentId(scheduleAppointment.getId())
            .setCodeQR(scheduleAppointment.getCodeQR())
            .setScheduleDate(scheduleAppointment.getScheduleDate())
            .setStartTime(scheduleAppointment.getStartTime())
            .setEndTime(scheduleAppointment.getEndTime())
            .setQueueUserId(scheduleAppointment.getQueueUserId())
            .setGuardianQid(scheduleAppointment.getGuardianQid())
            .setAppointmentStatus(scheduleAppointment.getAppointmentStatus())
            .setJsonProfile(jsonProfile);
    }

    @Mobile
    public void cancelAppointment(String id, String qid, String codeQR) {
        scheduleAppointmentManager.cancelAppointment(id, qid, codeQR);
    }

    public List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String day) {
        return scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, day);
    }

    /**
     * Safe to use for client only.
     */
    @Mobile
    public JsonScheduleList findBookedAppointmentsForDayAsJson(String codeQR, String scheduleDate) {
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(
                new JsonSchedule()
                    .setScheduleAppointmentId(scheduleAppointment.getId())
                    .setScheduleDate(scheduleAppointment.getScheduleDate())
                    .setStartTime(scheduleAppointment.getStartTime())
                    .setEndTime(scheduleAppointment.getEndTime())
                    .setQueueUserId(scheduleAppointment.getQueueUserId())
                    .setGuardianQid(scheduleAppointment.getGuardianQid())
                    .setAppointmentStatus(scheduleAppointment.getAppointmentStatus())
            );
        }

        return jsonScheduleList;
    }

    /**
     * Contains profile information. To be used by merchant only.
     */
    @Mobile
    public JsonScheduleList findScheduleForDayAsJson(String codeQR, String scheduleDate) {
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

            jsonScheduleList.addJsonSchedule(
                new JsonSchedule()
                    .setScheduleAppointmentId(scheduleAppointment.getId())
                    .setScheduleDate(scheduleAppointment.getScheduleDate())
                    .setStartTime(scheduleAppointment.getStartTime())
                    .setEndTime(scheduleAppointment.getEndTime())
                    .setQueueUserId(scheduleAppointment.getQueueUserId())
                    .setGuardianQid(scheduleAppointment.getGuardianQid())
                    .setAppointmentStatus(scheduleAppointment.getAppointmentStatus())
                    .setJsonProfile(jsonProfile)
            );
        }

        return jsonScheduleList;
    }

    @Mobile
    public JsonScheduleList numberOfAppointmentsForMonth(String codeQR, String month) {
        LOG.info("Appointments for {} {}", month, codeQR);
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        Date date = DateUtil.convertToDate(month, bizStore.getTimeZone());
        Date startOfMonth = DateUtil.startOfMonth(date, bizStore.getTimeZone());
        Date endOfMonth = DateUtil.endOfMonth(date, bizStore.getTimeZone());

        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForMonth(codeQR, startOfMonth, endOfMonth);
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(
                new JsonSchedule()
                    .setScheduleDate(scheduleAppointment.getScheduleDate())
                    .setTotalAppointments(scheduleAppointment.getTotalAppointments())
            );
        }

        return jsonScheduleList;
    }
}
