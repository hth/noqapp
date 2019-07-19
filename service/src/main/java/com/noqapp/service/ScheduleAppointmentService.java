package com.noqapp.service;

import static com.noqapp.domain.BizStoreEntity.UNDER_SCORE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonProfile;
import com.noqapp.domain.json.JsonQueueDisplay;
import com.noqapp.domain.json.JsonSchedule;
import com.noqapp.domain.json.JsonScheduleList;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.exceptions.AppointmentBookingException;
import com.noqapp.service.exceptions.AppointmentCancellationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * User: hitender
 * Date: 2019-05-23 23:59
 */
@Service
public class ScheduleAppointmentService {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleAppointmentService.class);

    private int untilDaysInPast;
    private int untilDaysInFuture;
    private int appointmentCancelLimitedToHours;

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private StoreHourManager storeHourManager;
    private UserProfileManager userProfileManager;
    private UserAccountManager userAccountManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private TokenQueueManager tokenQueueManager;

    private BizService bizService;
    private FirebaseMessageService firebaseMessageService;

    private ExecutorService executorService;

    @Autowired
    public ScheduleAppointmentService(
        @Value("${untilDaysInPast:60}")
        int untilDaysInPast,

        @Value("${untilDaysInFuture:2}")
        int untilDaysInFuture,

        @Value("${appointmentCancelLimitedToHours:24}")
        int appointmentCancelLimitedToHours,

        ScheduleAppointmentManager scheduleAppointmentManager,
        StoreHourManager storeHourManager,
        UserProfileManager userProfileManager,
        UserAccountManager userAccountManager,
        RegisteredDeviceManager registeredDeviceManager,
        TokenQueueManager tokenQueueManager,

        BizService bizService,
        FirebaseMessageService firebaseMessageService
    ) {
        this.untilDaysInPast = untilDaysInPast;
        this.untilDaysInFuture = untilDaysInFuture;
        this.appointmentCancelLimitedToHours = appointmentCancelLimitedToHours;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.storeHourManager = storeHourManager;
        this.userProfileManager = userProfileManager;
        this.userAccountManager = userAccountManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.tokenQueueManager = tokenQueueManager;

        this.bizService = bizService;
        this.firebaseMessageService = firebaseMessageService;

        this.executorService = newCachedThreadPool();
    }

    @Mobile
    public JsonSchedule bookAppointment(String guardianQid, JsonSchedule jsonSchedule) {
        BizStoreEntity bizStore = bizService.findByCodeQR(jsonSchedule.getCodeQR());
        if (bizStore.getAppointmentState() == AppointmentStateEnum.O) {
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

        ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
            .setCodeQR(jsonSchedule.getCodeQR())
            .setScheduleDate(jsonSchedule.getScheduleDate())
            .setStartTime(jsonSchedule.getStartTime())
            .setEndTime(jsonSchedule.getEndTime())
            .setQueueUserId(jsonSchedule.getQueueUserId())
            .setGuardianQid(guardianQid)
            .setAppointmentStatus(AppointmentStatusEnum.U)
            .setChiefComplain(jsonSchedule.getChiefComplain());
        save(scheduleAppointment);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        sendMessageToTopic(
            jsonSchedule.getCodeQR(),
            "Appointment Received",
            "Appointment requested for " + bizStore.getDisplayName() + " by " + userProfile.getName() + ".\n\n"
                + "Date: " + jsonSchedule.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(jsonSchedule.getStartTime())
                + ". Please confirm this appointment at earliest. If not confirmed, this appointment will auto cancel after 12 hours from booking.");
        /*
         * Do not inform anyone other than the person with the
         * token who is being served. This is personal message.
         * of being served out of order/sequence.
         */
        sendMessageToSelectedTokenUser(
            jsonSchedule.getCodeQR(),
            jsonProfile.getQueueUserId(),
            "Appointment Booked",
            "Your appointment has been booked. Awaiting confirmation from " + bizStore.getDisplayName());

        return JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile, JsonQueueDisplay.populate(bizStore, storeHour));
    }

    @Mobile
    public JsonSchedule rescheduleAppointment(JsonSchedule jsonSchedule) {
        ScheduleAppointmentEntity scheduleAppointment = findAppointment(jsonSchedule.getScheduleAppointmentId(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
        scheduleAppointment
            .setChiefComplain(jsonSchedule.getChiefComplain())
            .setScheduleDate(jsonSchedule.getScheduleDate())
            .setStartTime(jsonSchedule.getStartTime())
            .setEndTime(jsonSchedule.getEndTime())
            .setRescheduleCount(scheduleAppointment.getRescheduleCount() + 1);
        save(scheduleAppointment);

        BizStoreEntity bizStore = bizService.findByCodeQR(scheduleAppointment.getCodeQR());
        sendMessageToSelectedTokenUser(
            scheduleAppointment.getCodeQR(),
            scheduleAppointment.getQueueUserId(),
            "Appointment Re-Scheduled",
            "Your appointment has been re-scheduled by " + bizStore.getDisplayName() + "\n\n"
                + "For Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime())
                + ". Please arrive 30 minutes before your appointment.");

        return populateJsonSchedule(scheduleAppointment);
    }

    @Mobile
    public JsonSchedule populateJsonSchedule(ScheduleAppointmentEntity scheduleAppointment) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        return JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile);
    }

    public void save(ScheduleAppointmentEntity scheduleAppointment) {
        scheduleAppointmentManager.save(scheduleAppointment);
    }

    @Mobile
    public boolean cancelAppointment(String id, String qid, String codeQR) {
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        ScheduleAppointmentEntity scheduleAppointment = scheduleAppointmentManager.findAppointment(id, qid, codeQR);
        int startTime = scheduleAppointment.getStartTime();
        LocalDate appointmentDate = LocalDate.parse(scheduleAppointment.getScheduleDate());
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, DateFormatter.getLocalTime(startTime));

        long durationInHours = DateUtil.getHoursBetween(LocalDateTime.now(), appointmentDateTime);
        if (durationInHours < appointmentCancelLimitedToHours && scheduleAppointment.getAppointmentStatus() == AppointmentStatusEnum.A) {
            LOG.warn("Failed to cancel appointment as within {}hrs {} {} {}", appointmentCancelLimitedToHours, id, qid, codeQR);
            throw new AppointmentCancellationException("Failed to cancel appointment as appointment is within " + appointmentCancelLimitedToHours + " hours.");
        }

        boolean status = scheduleAppointmentManager.cancelAppointment(id, qid, codeQR);
        String additionalInfo = scheduleAppointment.getAppointmentStatus() == AppointmentStatusEnum.U
            ? "Un-Confirmed appointment was cancelled."
            : "Accepted appointment was cancelled before 24hrs period.";

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        sendMessageToTopic(
            scheduleAppointment.getCodeQR(),
            "Appointment Cancelled",
            "Appointment cancelled for " + bizStore.getDisplayName() + " by " + userProfile.getName() + ".\n\n"
                + "Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime()) + ". "
                + additionalInfo);
        return status;
    }

    private List<ScheduleAppointmentEntity> findBookedAppointmentsForDay(String codeQR, String scheduleDate) {
        return scheduleAppointmentManager.findBookedAppointmentsForDay(codeQR, scheduleDate);
    }

    private List<ScheduleAppointmentEntity> findScheduleForDay(String codeQR, String scheduleDate) {
        return scheduleAppointmentManager.findScheduleForDay(codeQR, scheduleDate);
    }

    /** Safe to use for client only. */
    @Mobile
    public JsonScheduleList findBookedAppointmentsForDayAsJson(String codeQR, String scheduleDate) {
        List<ScheduleAppointmentEntity> scheduleAppointments = findBookedAppointmentsForDay(codeQR, scheduleDate);
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, null));
        }

        return jsonScheduleList;
    }

    /** Contains profile information. To be used by merchant only. */
    @Mobile
    public JsonScheduleList findScheduleForDayAsJson(String codeQR, String scheduleDate) {
        List<ScheduleAppointmentEntity> scheduleAppointments = findScheduleForDay(codeQR, scheduleDate);
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
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
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
            .setAppointmentState(bizStore.getAppointmentState())
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
        BizStoreEntity bizStore = bizService.findByCodeQR(scheduleAppointment.getCodeQR());

        switch (appointmentStatus) {
            case A:
                sendMessageToSelectedTokenUser(
                    scheduleAppointment.getCodeQR(),
                    jsonProfile.getQueueUserId(),
                    "Appointment Confirmed",
                    "Appointment has been confirmed by " + bizStore.getDisplayName() + "\n\n"
                        + "On Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime())
                        + ". Please arrive 30 minutes before your appointment.");
                break;
            case R:
                sendMessageToSelectedTokenUser(
                    scheduleAppointment.getCodeQR(),
                    jsonProfile.getQueueUserId(),
                    "Appointment Cancelled",
                    "Your appointment has been cancelled by " + bizStore.getDisplayName() + "\n\n"
                        + "For Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime())
                        + ". Please re-book appointment or call " + bizStore.getDisplayName() +".");
                break;
        }

        return JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile);
    }

    @Mobile
    public boolean doesAppointmentExists(String qid, String codeQR, String scheduleDate) {
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
            BizStoreEntity bizStore = bizService.findByCodeQR(scheduleAppointment.getCodeQR());
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

    /** Send FCM message to Topic asynchronously. */
    private void sendMessageToTopic(String codeQR, String title, String message) {
        executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, title, message));
    }

    /** Send FCM message to person with specific token number asynchronously. */
    private void sendMessageToSelectedTokenUser(String codeQR, String qid, String title, String message) {
        executorService.submit(() -> invokeThreadSendMessageToSelectedTokenUser(codeQR, qid, title, message));
    }

    /** Formulates and send messages to FCM. */
    void invokeThreadSendMessageToTopic(String codeQR, String title, String message) {
        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
        LOG.debug("Sending message codeQR={} tokenQueue={} firebaseMessageType={}", codeQR, tokenQueue, FirebaseMessageTypeEnum.M);
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Appointment received being sent to {}", tokenQueue.getCorrectTopic(QueueStatusEnum.D) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(QueueStatusEnum.D) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(MessageOriginEnum.QA, FirebaseMessageTypeEnum.P).getJsonTopicAppointmentData()
                .setMessage(message);

            /*
             * This message has to go as the merchant with the opened queue
             * will not get any update if some one joins. FCM makes sure the message is dispersed.
             */
            if (DeviceTypeEnum.I == deviceType) {
                jsonMessage.getNotification()
                    .setBody(message)
                    .setTitle(title);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setBody(message)
                    .setTitle(title);
            }

            jsonMessage.setData(jsonData);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    /** When client is booking appointment send message and mark it as personal. */
    private void invokeThreadSendMessageToSelectedTokenUser(String codeQR, String qid, String title, String message) {
        LOG.debug("Sending personal message codeQR={} qid={} message={}", codeQR, qid, message);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
            userProfile = userProfileManager.findOneByPhone(userProfile.getGuardianPhone());
        }

        RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(userProfile.getQueueUserId());

        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonTopicData(MessageOriginEnum.QA, FirebaseMessageTypeEnum.P).getJsonTopicAppointmentData()
            .setMessage(message);

        if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
            jsonMessage.getNotification()
                .setBody(message)
                .setTitle(title);
        } else {
            jsonMessage.setNotification(null);
            jsonData
                .setBody(message)
                .setTitle(title);
        }

        jsonMessage.setData(jsonData);

        LOG.debug("Personal FCM message to be sent={}", jsonMessage);
        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Personal broadcast failed message={}", jsonMessage.asJson());
        } else {
            LOG.debug("Sent Personal message={}", jsonMessage.asJson());
        }
    }
}
