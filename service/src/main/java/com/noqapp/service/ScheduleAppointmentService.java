package com.noqapp.service;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfile;
import com.noqapp.domain.json.JsonQueueDisplay;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-23 23:59
 */
@Service
public class ScheduleAppointmentService {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleAppointmentService.class);

    private int untilDaysInPast;
    private int untilDaysInFuture;

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private BizStoreManager bizStoreManager;
    private StoreHourManager storeHourManager;
    private UserProfileManager userProfileManager;
    private UserAccountManager userAccountManager;

    private BizService bizService;

    @Autowired
    public ScheduleAppointmentService(
        @Value("${untilDaysInPast:60}")
        int untilDaysInPast,

        @Value("${untilDaysInFuture:2}")
        int untilDaysInFuture,

        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,
        StoreHourManager storeHourManager,
        UserProfileManager userProfileManager,
        UserAccountManager userAccountManager,

        BizService bizService
    ) {
        this.untilDaysInPast = untilDaysInPast;
        this.untilDaysInFuture = untilDaysInFuture;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.storeHourManager = storeHourManager;
        this.userProfileManager = userProfileManager;
        this.userAccountManager = userAccountManager;

        this.bizService = bizService;
    }

    @Mobile
    public JsonScheduleList bookAppointment(String guardianQid, JsonSchedule jsonSchedule) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(jsonSchedule.getCodeQR());
        if (!bizStore.isAppointmentEnable()) {
            LOG.warn("Appointment is not enabled {} for {}", jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " is not accepting appointments");
        }

        StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), DateUtil.getDayOfWeekFromDate(jsonSchedule.getScheduleDate()));
        if (storeHour.isDayClosed()) {
            LOG.warn("Closed cannot book {} {} {} {}",
                jsonSchedule.getStartTime(), storeHour.isDayClosed(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " is closed for the day");
        }

        if (storeHour.getAppointmentStartHour() > jsonSchedule.getStartTime()) {
            LOG.warn("Supplied start time is beyond range {} {} {} {}",
                jsonSchedule.getStartTime(), storeHour.getAppointmentStartHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " opens at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getStartHour()));
        }

        if (storeHour.getAppointmentEndHour() < jsonSchedule.getEndTime()) {
            LOG.warn("Supplied end time is beyond range {} {} {} {}",
                jsonSchedule.getEndTime(), storeHour.getAppointmentEndHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " closes at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getEndHour()));
        }

        int appointmentDuration = bizStore.getAppointmentDuration();
        long totalBookDuration = ChronoUnit.MINUTES.between(
            DateFormatter.getLocalTime(jsonSchedule.getStartTime()),
            DateFormatter.getLocalTime(jsonSchedule.getEndTime()));

        long loop = totalBookDuration/appointmentDuration;

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (int i = 0; i < loop; i++) {
            ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
                .setCodeQR(jsonSchedule.getCodeQR())
                .setScheduleDate(jsonSchedule.getScheduleDate())
                .setStartTime(jsonSchedule.getStartTime())
                .setEndTime(jsonSchedule.getEndTime())
                .setQueueUserId(jsonSchedule.getQueueUserId())
                .setGuardianQid(guardianQid)
                .setAppointmentStatus(AppointmentStatusEnum.U)
                .setChiefComplain(jsonSchedule.getChiefComplain());

            scheduleAppointmentManager.save(scheduleAppointment);

            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

            jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile, JsonQueueDisplay.populate(bizStore, storeHour)));
        }

        return jsonScheduleList;
    }

    @Mobile
    public void cancelAppointment(String id, String qid, String codeQR) {
        scheduleAppointmentManager.cancelAppointment(id, qid, codeQR);
    }

    public List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String scheduleDate) {
        return scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
    }

    /** Safe to use for client only. */
    @Mobile
    public JsonScheduleList findBookedAppointmentsForDayAsJson(String codeQR, String scheduleDate) {
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, null));
        }

        return jsonScheduleList;
    }

    /** Contains profile information. To be used by merchant only. */
    @Mobile
    public JsonScheduleList findScheduleForDayAsJson(String codeQR, String scheduleDate) {
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
            JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);
            jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile));
        }

        return jsonScheduleList;
    }

    @Mobile
    public JsonScheduleList numberOfAppointmentsForMonth(String codeQR, String month) {
        LOG.info("Appointments for {} {}", month, codeQR);
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        Date date = DateUtil.convertToDate(month, bizStore.getTimeZone());
        Date startOfMonth = DateUtil.startOfMonth(date, bizStore.getTimeZone());
        Date endOfMonth = DateUtil.endOfMonth(date, bizStore.getTimeZone());
        LOG.info("Find between days {} {} for {}", startOfMonth, endOfMonth, codeQR);

        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedAppointmentsForMonth(
            codeQR,
            Formatter.toDefaultDateFormatAsString(startOfMonth),
            Formatter.toDefaultDateFormatAsString(endOfMonth));

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(
                new JsonSchedule()
                    .setScheduleDate(scheduleAppointment.getScheduleDate())
                    .setTotalAppointments(scheduleAppointment.getTotalAppointments())
            );
        }

        jsonScheduleList
            .setJsonHours(bizService.findAllStoreHoursAsJson(bizStore.getId()))
            .setAppointmentDuration(bizStore.getAppointmentDuration())
            .setAppointmentEnable(bizStore.isAppointmentEnable())
            .setAppointmentOpenHowFar(bizStore.getAppointmentOpenHowFar());
        return jsonScheduleList;
    }

    @Mobile
    public JsonSchedule scheduleAction(String scheduleAppointmentId, AppointmentStatusEnum appointmentStatus, String qid, String codeQR) {
        ScheduleAppointmentEntity scheduleAppointment = scheduleAppointmentManager.updateSchedule(scheduleAppointmentId, appointmentStatus, qid, codeQR);
        if (null == scheduleAppointment) {
            LOG.warn("Could not find schedule {} {} {} {}", scheduleAppointmentId, appointmentStatus, qid, codeQR);
            throw new AppointmentBookingException("Could not find schedule");
        }
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        return JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile);
    }

    @Mobile
    public boolean doesAppointmentExists(String qid, String codeQR, String scheduleDate) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        return scheduleAppointmentManager.doesAppointmentExists(qid, codeQR, scheduleDate);
    }

    @Mobile
    public JsonScheduleList findAllPastAppointments(String qid) {
        List<String> qids = getListOfQueueUserIds(qid);

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (String queueUserId : qids) {
            List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findAllPastAppointments(queueUserId, untilDaysInPast);

            populateJsonScheduleList(jsonScheduleList, queueUserId, scheduleAppointments);
        }

        return jsonScheduleList;
    }

    @Mobile
    public JsonScheduleList findLimitedUpComingAppointments(String qid) {
        List<String> qids = getListOfQueueUserIds(qid);

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (String queueUserId : qids) {
            List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findAllUpComingAppointments(queueUserId, untilDaysInFuture);

            populateJsonScheduleList(jsonScheduleList, queueUserId, scheduleAppointments);
        }

        return jsonScheduleList;
    }

    @Mobile
    public JsonScheduleList findAllUpComingAppointments(String qid) {
        List<String> qids = getListOfQueueUserIds(qid);

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (String queueUserId : qids) {
            List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findAllUpComingAppointments(queueUserId);

            populateJsonScheduleList(jsonScheduleList, queueUserId, scheduleAppointments);
        }

        return jsonScheduleList;
    }

    private List<String> getListOfQueueUserIds(String qid) {
        UserProfileEntity userProfileOfGuardian = userProfileManager.findByQueueUserId(qid);

        List<String> qids;
        if (null != userProfileOfGuardian.getQidOfDependents()) {
            qids = userProfileOfGuardian.getQidOfDependents();
            qids.add(qid);
        } else {
            qids = new ArrayList<String>() {{add(qid);}};
        }
        return qids;
    }

    private void populateJsonScheduleList(
        JsonScheduleList jsonScheduleList,
        String queueUserId, List<ScheduleAppointmentEntity> scheduleAppointments
    ) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(queueUserId);
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(queueUserId);
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(scheduleAppointment.getCodeQR());
            StoreHourEntity storeHour = storeHourManager.findOne(
                bizStore.getId(),
                DateUtil.getDayOfWeekFromDate(scheduleAppointment.getScheduleDate()));

            jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(
                scheduleAppointment,
                jsonProfile,
                JsonQueueDisplay.populate(bizStore, storeHour)));
        }
    }

    @Mobile
    public ScheduleAppointmentEntity findAppointment(String id, String qid, String codeQR) {
        return scheduleAppointmentManager.findAppointment(id, qid, codeQR);
    }
}
