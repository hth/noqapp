package com.noqapp.loader.service;

import static com.noqapp.common.utils.DateUtil.DTF_ISO;
import static com.noqapp.common.utils.DateUtil.DTF_YYYY_MM_DD;
import static com.noqapp.common.utils.DateUtil.MINUTES_IN_MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.RandomString;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.jms.FlexAppointment;
import com.noqapp.domain.json.JsonSchedule;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.loader.ITest;
import com.noqapp.service.exceptions.TokenAvailableLimitReachedException;
import com.noqapp.service.utils.ServiceUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * hitender
 * 7/8/21 6:58 PM
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JMSConsumerServiceITest extends ITest {
    private static final Logger LOG = LoggerFactory.getLogger(JMSConsumerServiceITest.class);

    private JMSConsumerService jmsConsumerService;

    private final List<String> mails = new LinkedList<>();
    private final List<String> flexAppointmentUsers = new LinkedList<>();

    private BizStoreEntity bizStore;
    private StoreHourEntity storeHour;

    private final int registeredUser = 210;
    private final int flexAppointmentUser = 50;

    private String timeZone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ZonedDateTime zonedDateTime = DateUtil.getZonedDateTimeAtUTC();
        if (zonedDateTime.getHour() < 12) {
            timeZone = "Asia/Calcutta";
        } else {
            timeZone = "Pacific/Honolulu";
        }

        jmsConsumerService = new JMSConsumerService(
            mailService,
            messageCustomerService,
            flexAppointmentToTokenService,
            fileOperationOnS3);

        registerStore();
        registerUsers();
        preAuthorizeUser();
    }

    /** Registers store. */
    private void registerStore() {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        bizStore = bizService.findOneBizStore(bizName.getId());
        List<StoreHourEntity> storeHours = storeHourService.findAllStoreHours(bizStore.getId());
        setBizStoreHours(bizStore, storeHours);
    }

    private void setBizStoreHours(BizStoreEntity bizStore, List<StoreHourEntity> storeHours) {
        bizStore.setAverageServiceTime(114000).setAvailableTokenCount(200);
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.F);
        bizService.saveStore(bizStore, "Changed AST with Flex Appointment");

        for (StoreHourEntity storeHour : storeHours) {
            storeHour.setStartHour(930)
                .setEndHour(1600)
                .setLunchTimeStart(1300)
                .setLunchTimeEnd(1400)
                .setTokenAvailableFrom(100)
                .setTokenNotAvailableFrom(1530)
                .setAppointmentStartHour(930)
                .setAppointmentEndHour(1600)
                .setDayClosed(false)
                .setTempDayClosed(false)
                .setPreventJoining(false);
            storeHourManager.save(storeHour);
            this.storeHour = storeHour;
        }

        bizService.updateStoreTokenAndServiceTime(
            bizStore.getCodeQR(),
            ServiceUtils.computeAverageServiceTime(storeHour, bizStore.getAvailableTokenCount()),
            bizStore.getAvailableTokenCount());
    }

    /** Create new users. */
    private void registerUsers() {
        for (int i = 0; i < registeredUser; i++) {
            String phone = "+91" + StringUtils.leftPad(String.valueOf(i), 10, '0');
            String name = RandomString.newInstance(6).nextString().toLowerCase();
            UserAccountEntity userAccount = accountService.createNewAccount(
                phone,
                name,
                name,
                name + "@r.com",
                "2000-12-12",
                GenderEnum.M,
                "IN",
                timeZone,
                "password",
                "",
                true,
                false
            );
            mails.add(name + "@r.com");

            RegisteredDeviceEntity registeredDevice = RegisteredDeviceEntity.newInstance(
                userAccount.getQueueUserId(),
                UUID.randomUUID().toString(),
                DeviceTypeEnum.A,
                AppFlavorEnum.NQCL,
                UUID.randomUUID().toString(),
                appVersion,
                "en",
                null,
                new double[]{71.022498, 18.0244723},
                null);
            registeredDeviceManager.save(registeredDevice);
        }

        for (int i = registeredUser; i < registeredUser + flexAppointmentUser; i++) {
            String phone = "+91" + StringUtils.leftPad(String.valueOf(i), 10, '0');
            String name = RandomString.newInstance(6).nextString().toLowerCase();
            UserAccountEntity userAccount = accountService.createNewAccount(
                phone,
                name,
                name,
                name + "@flex.com",
                "2000-12-12",
                GenderEnum.M,
                "IN",
                timeZone,
                "password",
                "",
                true,
                false
            );
            flexAppointmentUsers.add(name + "@flex.com");

            RegisteredDeviceEntity registeredDevice = RegisteredDeviceEntity.newInstance(
                userAccount.getQueueUserId(),
                UUID.randomUUID().toString(),
                DeviceTypeEnum.A,
                AppFlavorEnum.NQCL,
                UUID.randomUUID().toString(),
                appVersion,
                "en",
                null,
                new double[]{71.022498, 18.0244723},
                null);
            registeredDeviceManager.save(registeredDevice);
        }
    }

    /** Authorized user for Stores. */
    private void preAuthorizeUser() {
        for (String mail : mails) {
            preApproveUser(mail);
        }

        for (String mail : flexAppointmentUsers) {
            preApproveUser(mail);
        }
    }

    private void preApproveUser(String mail) {
        UserAccountEntity userAccount = userAccountManager.findByUserId(mail);

        String businessCustomerId = businessCustomerService.addAuthorizedUserForDoingBusiness(
            "G" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'),
            bizStore.getBizName().getId(),
            userAccount.getQueueUserId());
        Assertions.assertNotNull(businessCustomerId, "Should not be null");
        businessCustomerService.addBusinessCustomerAttribute(businessCustomerId, BusinessCustomerAttributeEnum.GR);

        businessCustomerId = businessCustomerService.addAuthorizedUserForDoingBusiness(
            "L" + StringUtils.leftPad(String.valueOf(userAccount.getQueueUserId()), 18, '0'),
            bizStore.getBizName().getId(),
            userAccount.getQueueUserId());
        Assertions.assertNotNull(businessCustomerId, "Should not be null");
        businessCustomerService.addBusinessCustomerAttribute(businessCustomerId, BusinessCustomerAttributeEnum.LQ);
    }

    /* Ran with success on 15 Aug 2021. Disabled as it consumes lot of time. */
    @Disabled
    void sendFlexAppointment() {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        BizStoreEntity bizStore = bizService.findOneBizStore(bizName.getId());

        createFlexAppointment();
        List<ScheduleAppointmentEntity> scheduleAppointmentsBefore = scheduleAppointmentService.findBookedAppointmentsForDay(bizStore.getCodeQR(), DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD));
        assertEquals(scheduleAppointmentsBefore.size(), 50, "Booked appointments before tokens are issued");

        Map<String, String> display = new LinkedHashMap<>();
        for (String mail : mails) {
            try {
                JsonToken jsonToken = joinQueue(display, mail, bizStore);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(jsonToken.getExpectedServiceBegin(), DTF_ISO).withZoneSameInstant(ZoneId.of(timeZone));
                jmsConsumerService.sendFlexAppointment(FlexAppointment.newInstance(bizStore.getCodeQR(), zonedDateTime.format(DTF_YYYY_MM_DD), CommonUtil.getTimeIn24HourFormat(zonedDateTime)));
            } catch (TokenAvailableLimitReachedException e) {
                LOG.error("Failed as {} {}", mail, e.getLocalizedMessage());
            }
        }
        printTokens(display, "After Getting");

        Map<String, String> ordered = new LinkedHashMap<>();
        List<QueueEntity> queues = queueManager.findAllByCodeQR(bizStore.getCodeQR());
        queues.stream().sorted(Comparator.comparingInt(QueueEntity::getTokenNumber)).forEach(queue -> {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queue.getQueueUserId());
            LOG.info("All in queue {} {}", queue.getTokenNumber(), queue.getTimeSlotMessage() + " : " + userProfile.getEmail());
            ordered.put(String.valueOf(queue.getTokenNumber()), queue.getTimeSlotMessage() + " : " + userProfile.getEmail());
        });
        printTokens(ordered, "Ordered Queue");

        List<ScheduleAppointmentEntity> scheduleAppointmentsAfter = scheduleAppointmentService.findBookedAppointmentsForDay(bizStore.getCodeQR(), DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD));
        assertEquals(scheduleAppointmentsAfter.size(), 0, "Booked appointments after tokens are issued");
        LOG.info("Appointments {} {}", scheduleAppointmentsBefore.size(), scheduleAppointmentsAfter.size());

        long averageServiceTime = ServiceUtils.computeAverageServiceTime(storeHour, bizStore.getAvailableTokenCount());
        System.out.println("averageServiceTime=" + new BigDecimal(averageServiceTime).divide(new BigDecimal(MINUTES_IN_MILLISECONDS), MathContext.DECIMAL64) + " minutes per user");
        assertEquals(200, queues.size(), "Number of token issued must be equal " + bizStore.getDisplayName());
    }

    private void printTokens(Map<String, String> display, String append) {
        for (String key : display.keySet()) {
            LOG.info("{} Token={} {}", append, key, display.get(key));
        }
    }

    private void createFlexAppointment() {
        for (String mail : flexAppointmentUsers) {
            UserAccountEntity userAccount = userAccountManager.findByUserId(mail);

            JsonSchedule jsonSchedule = new JsonSchedule()
                .setCodeQR(bizStore.getCodeQR())
                .setScheduleDate(DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD))
                .setStartTime(1000)
                .setEndTime(1030)
                .setQueueUserId(userAccount.getQueueUserId())
                .setAppointmentState(bizStore.getAppointmentState());

            JsonSchedule bookedAppointment = scheduleAppointmentService.bookAppointment(userAccount.getQueueUserId(), jsonSchedule);
            LOG.info("Appointment booked {}", bookedAppointment);

            jsonSchedule = new JsonSchedule()
                .setCodeQR(bizStore.getCodeQR())
                .setScheduleDate(DateUtil.getZonedDateTimeAtUTC().plusDays(1).format(DTF_YYYY_MM_DD))
                .setStartTime(1000)
                .setEndTime(1030)
                .setQueueUserId(userAccount.getQueueUserId())
                .setAppointmentState(bizStore.getAppointmentState());

            bookedAppointment = scheduleAppointmentService.bookAppointment(userAccount.getQueueUserId(), jsonSchedule);
            LOG.info("Appointment booked {}", bookedAppointment);
        }
    }

    private JsonToken joinQueue(Map<String, String> display, String mail, BizStoreEntity bizStore) {
        UserAccountEntity userAccount = userAccountManager.findByUserId(mail);

        joinAbortService.checkCustomerApprovedForTheQueue(userAccount.getQueueUserId(), bizStore);
        JsonToken jsonToken = joinAbortService.joinQueue(
            UUID.randomUUID().toString(),
            userAccount.getQueueUserId(),
            null,
            bizStore,
            TokenServiceEnum.C);

        //{"error":{"reason":"CSD Liquor for Ex-Servicemen has not started. Please correct time on your device.","systemErrorCode":"4071","systemError":"DEVICE_TIMEZONE_OFF"}}
        //{"error":{"reason":"CSD Liquor for Ex-Servicemen token limit for the day has reached.","systemErrorCode":"4309","systemError":"QUEUE_TOKEN_LIMIT"}}
        if (0 != jsonToken.getToken()) {
            display.put(String.valueOf(jsonToken.getToken()), jsonToken.getTimeSlotMessage() + " : " + mail);
            LOG.info("Joined queue {} : {}", jsonToken.getToken(), jsonToken);
        }

        return jsonToken;
    }
}
