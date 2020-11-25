package com.noqapp.service;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.ScheduledTaskEntity;
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
import com.noqapp.repository.ScheduledTaskManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

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
    private String doNotReplyEmail;
    private String emailAddressName;

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private StoreHourManager storeHourManager;
    private UserProfileManager userProfileManager;
    private UserAccountManager userAccountManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private TokenQueueManager tokenQueueManager;
    private ScheduledTaskManager scheduledTaskManager;

    private BizService bizService;
    private FirebaseMessageService firebaseMessageService;
    private MailService mailService;
    private StoreHourService storeHourService;

    private ExecutorService executorService;

    @Autowired
    public ScheduleAppointmentService(
        @Value("${untilDaysInPast:60}")
        int untilDaysInPast,

        @Value("${untilDaysInFuture:2}")
        int untilDaysInFuture,

        @Value("${appointmentCancelLimitedToHours:24}")
        int appointmentCancelLimitedToHours,

        @Value("${do.not.reply.email}")
        String doNotReplyEmail,

        @Value ("${email.address.name}")
        String emailAddressName,

        ScheduleAppointmentManager scheduleAppointmentManager,
        StoreHourManager storeHourManager,
        UserProfileManager userProfileManager,
        UserAccountManager userAccountManager,
        RegisteredDeviceManager registeredDeviceManager,
        TokenQueueManager tokenQueueManager,
        ScheduledTaskManager scheduledTaskManager,

        BizService bizService,
        FirebaseMessageService firebaseMessageService,
        MailService mailService,
        StoreHourService storeHourService
    ) {
        this.untilDaysInPast = untilDaysInPast;
        this.untilDaysInFuture = untilDaysInFuture;
        this.appointmentCancelLimitedToHours = appointmentCancelLimitedToHours;
        this.doNotReplyEmail = doNotReplyEmail;
        this.emailAddressName = emailAddressName;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.storeHourManager = storeHourManager;
        this.userProfileManager = userProfileManager;
        this.userAccountManager = userAccountManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.tokenQueueManager = tokenQueueManager;
        this.scheduledTaskManager = scheduledTaskManager;

        this.bizService = bizService;
        this.firebaseMessageService = firebaseMessageService;
        this.mailService = mailService;
        this.storeHourService = storeHourService;

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

        /* Back up check when everything else has failed on client side. */
        checkIfAcceptingAppointment(jsonSchedule.getScheduleDate(), bizStore);

        if (storeHour.getAppointmentStartHour() > jsonSchedule.getStartTime()) {
            LOG.warn("Supplied start time is beyond range {} {} {} {}",
                jsonSchedule.getStartTime(), storeHour.getAppointmentStartHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName()
                + " opens at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getStartHour()));
        }

        if (storeHour.getAppointmentEndHour() < jsonSchedule.getEndTime()) {
            LOG.warn("Supplied end time is beyond range {} {} {} {}",
                jsonSchedule.getEndTime(), storeHour.getAppointmentEndHour(), jsonSchedule.getQueueUserId(), jsonSchedule.getCodeQR());
            throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName()
                + " closes at " + Formatter.convertMilitaryTo12HourFormat(storeHour.getEndHour()));
        }

        AppointmentStatusEnum appointmentStatus;
        switch (bizStore.getBusinessType()) {
            case DO:
            case HS:
                appointmentStatus = AppointmentStatusEnum.U;
                break;
            default:
                appointmentStatus = AppointmentStatusEnum.A;
        }

        ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
            .setCodeQR(jsonSchedule.getCodeQR())
            .setScheduleDate(jsonSchedule.getScheduleDate())
            .setStartTime(jsonSchedule.getStartTime())
            .setEndTime(jsonSchedule.getEndTime())
            .setQueueUserId(jsonSchedule.getQueueUserId())
            .setGuardianQid(guardianQid)
            .setAppointmentStatus(appointmentStatus)
            .setChiefComplain(jsonSchedule.getChiefComplain())
            .setAppointmentState(bizStore.getAppointmentState());
        save(scheduleAppointment);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
        JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);

        if (AppointmentStatusEnum.A == appointmentStatus) {
            switch (bizStore.getAppointmentState()) {
                case S:
                    sendMessageToSelectedTokenUser(
                        scheduleAppointment.getCodeQR(),
                        jsonProfile.getQueueUserId(),
                        "Walkin Appointment Confirmed",
                        "Appointment has been confirmed by " + bizStore.getDisplayName() + "\n\n"
                            + "On Date: " + scheduleAppointment.getScheduleDate() + ". A token number will be issued on this day. "
                            + "You will be serviced based on your position in the queue. "
                            + "Please arrive 30 minutes before your token number is called.");
                    break;
                case A:
                case F:
                    sendMessageToSelectedTokenUser(
                        scheduleAppointment.getCodeQR(),
                        jsonProfile.getQueueUserId(),
                        "Appointment Confirmed",
                        "Appointment has been confirmed by " + bizStore.getDisplayName() + "\n\n"
                            + "On Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime())
                            + ". Please arrive 30 minutes before your appointment.");
                    break;
                case O:
                    LOG.error("No appointment can be issued when the state is {} {}", bizStore.getAppointmentState(), jsonSchedule.getQueueUserId());
                    break;
            }
        } else {
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
        }

        if (bizStore.getBizName().isNotClaimed()) {
            sendAppointmentMail("booked", userProfile, bizStore, scheduleAppointment);
        }

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

        switch (bizStore.getAppointmentState()) {
            case S:
                /* Allow cancelling of walkin appointments within 24 hrs. */
                break;
            case A:
            case F:
                long durationInHours = DateUtil.getHoursBetween(LocalDateTime.now(), appointmentDateTime);
                if (durationInHours < appointmentCancelLimitedToHours && scheduleAppointment.getAppointmentStatus() == AppointmentStatusEnum.A) {
                    LOG.warn("Failed to cancel appointment as within {} hrs {} {} {}", appointmentCancelLimitedToHours, id, qid, codeQR);
                    throw new AppointmentCancellationException("Failed to cancel appointment as appointment is within " + appointmentCancelLimitedToHours + " hours.");
                }
                break;
            case O:
                break;
            default:
                LOG.error("Reached un-supported condition {}", bizStore.getAppointmentState().getDescription());
                throw new UnsupportedOperationException("Reached not supported condition");
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

        if (bizStore.getBizName().isNotClaimed()) {
            sendAppointmentMail("cancelled", userProfile, bizStore, scheduleAppointment);
        }

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
        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        try {
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            checkIfAcceptingAppointment(scheduleDate, bizStore);

            List<ScheduleAppointmentEntity> scheduleAppointments = findBookedAppointmentsForDay(codeQR, scheduleDate);
            for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
                jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, null));
            }

            return jsonScheduleList;
        } catch (AppointmentBookingException e) {
            return jsonScheduleList.setAppointmentState(AppointmentStateEnum.O);
        }
    }

    /** Contains profile information. To be used by business only. */
    @Mobile
    public JsonScheduleList findScheduleForDayAsJson(String codeQR, String scheduleDate) {
        JsonScheduleList jsonScheduleList = new JsonScheduleList();

        try {
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            checkIfAcceptingAppointment(scheduleDate, bizStore);

            List<ScheduleAppointmentEntity> scheduleAppointments = findScheduleForDay(codeQR, scheduleDate);
            for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
                UserProfileEntity userProfile = userProfileManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
                UserAccountEntity userAccount = userAccountManager.findByQueueUserId(scheduleAppointment.getQueueUserId());
                JsonProfile jsonProfile = JsonProfile.newInstance(userProfile, userAccount);
                jsonScheduleList.addJsonSchedule(JsonSchedule.populateJsonSchedule(scheduleAppointment, jsonProfile));
            }

            return jsonScheduleList;
        } catch (AppointmentBookingException e) {
            return jsonScheduleList.setAppointmentState(AppointmentStateEnum.O);
        }
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
            DateUtil.dateToString(startOfMonth),
            DateUtil.dateToString(endOfMonth));

        JsonScheduleList jsonScheduleList = new JsonScheduleList();
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            jsonScheduleList.addJsonSchedule(
                new JsonSchedule()
                    .setScheduleDate(scheduleAppointment.getScheduleDate())
                    .setTotalAppointments(scheduleAppointment.getTotalAppointments())
            );
        }

        jsonScheduleList
            .setJsonHours(storeHourService.findAllStoreHoursAsJson(bizStore))
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
                switch (bizStore.getAppointmentState()) {
                    case S:
                        sendMessageToSelectedTokenUser(
                            scheduleAppointment.getCodeQR(),
                            jsonProfile.getQueueUserId(),
                            "Walkin Appointment Confirmed",
                            "Appointment has been confirmed by " + bizStore.getDisplayName() + "\n\n"
                                + "On Date: " + scheduleAppointment.getScheduleDate() + ". A token number will be issued on this day. "
                                + "You will be serviced based on your position in the queue. "
                                + "Please arrive 30 minutes before your token number is called.");
                        break;
                    case A:
                    case F:
                        sendMessageToSelectedTokenUser(
                            scheduleAppointment.getCodeQR(),
                            jsonProfile.getQueueUserId(),
                            "Appointment Confirmed",
                            "Appointment has been confirmed by " + bizStore.getDisplayName() + "\n\n"
                                + "On Date: " + scheduleAppointment.getScheduleDate() + " & Time: " + Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime())
                                + ". Please arrive 30 minutes before your appointment.");
                        break;
                    case O:
                        LOG.error("No appointment can be issued when the state is {} {}", bizStore.getAppointmentState(), qid);
                        break;
                }
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
        String queueUserId, 
        List<ScheduleAppointmentEntity> scheduleAppointments
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
             * This message has to go as the business with the opened queue
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

    /** Send email for booking, cancelling of appointment. This normally happens when business is un-claimed. */
    private void sendAppointmentMail(
        String appointmentState,
        UserProfileEntity userProfile,
        BizStoreEntity bizStore,
        ScheduleAppointmentEntity scheduleAppointment
    ) {
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("bizStore", bizStore.getDisplayName());
        rootMap.put("bizName", bizStore.getBizName().getBusinessName());
        rootMap.put("bizStorePhone", StringUtils.isNotBlank(bizStore.getPhoneFormatted())
            ? bizStore.getPhoneFormatted()
            : bizStore.getBizName().getPhoneFormatted());
        rootMap.put("bizCountryShortName", bizStore.getCountryShortName());
        rootMap.put("user", userProfile.getName());
        rootMap.put("userPhone", StringUtils.isBlank(userProfile.getGuardianPhone())
            ? userProfile.getPhoneFormatted()
            : "N/A");
        rootMap.put("userGuardianPhone", StringUtils.isNotBlank(userProfile.getGuardianPhone())
            ? userProfile.getGuardianPhoneFormatted()
            : "N/A");
        rootMap.put("appointmentState", appointmentState);
        rootMap.put("appointmentDate", scheduleAppointment.getScheduleDate());
        rootMap.put("appointmentTime", Formatter.convertMilitaryTo12HourFormat(scheduleAppointment.getStartTime()));

        LOG.info("Emailing appointment details email={} emailAddressName={}", doNotReplyEmail, emailAddressName);
        mailService.sendAnyMail(
            doNotReplyEmail,
            emailAddressName,
            "Appointment " + appointmentState + ": for " + bizStore.getDisplayName() + " at " + bizStore.getBizName().getBusinessName(),
            rootMap,
            "mail/appointment-for-unclaimed-business.ftl"
        );
    }

    /** Checks if the schedule date is between existing scheduled off. */
    private void checkIfAcceptingAppointment(String scheduleDate, BizStoreEntity bizStore) {
        if (StringUtils.isNotBlank(bizStore.getScheduledTaskId())) {
            ScheduledTaskEntity scheduledTask = scheduledTaskManager.findOneById(bizStore.getScheduledTaskId());
            Date from = DateUtil.convertToDate(scheduledTask.getFrom(), bizStore.getTimeZone());
            Date until = DateUtil.convertToDate(scheduledTask.getUntil(), bizStore.getTimeZone());
            Date expectedAppointmentDate = DateUtil.convertToDate(scheduleDate, bizStore.getTimeZone());
            if (DateUtil.isThisDayBetween(expectedAppointmentDate, from, until)) {
                LOG.warn("Scheduled closed cannot book {} {} {}", scheduleDate, from, until);
                throw new AppointmentBookingException("Booking failed as " + bizStore.getDisplayName() + " is closed on that day");
            }
        }

        if (bizStore.getBizName().isDayClosed()) {
            LOG.warn("Scheduled business is closed {}", scheduleDate);
            throw new AppointmentBookingException("Booking failed as " + bizStore.getBizName().getBusinessName() + " is closed on that day");
        }
    }

    /** Cancel all pending appointment. */
    public void findAllUpComingAppointmentsByBizName(String bizNameId) {
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizNameId);
        for (BizStoreEntity bizStore : bizStores) {
            String day = DateUtil.dateToString(DateUtil.dateAtTimeZone(bizStore.getTimeZone()));
            try (Stream<ScheduleAppointmentEntity> stream = scheduleAppointmentManager.findAllUpComingAppointmentsByBizStore(bizStore.getCodeQR(), day)) {
                stream.iterator().forEachRemaining(scheduleAppointment -> scheduleAction(
                    scheduleAppointment.getId(),
                    AppointmentStatusEnum.R,
                    scheduleAppointment.getQueueUserId(),
                    scheduleAppointment.getCodeQR()));
            }
        }
    }
}
