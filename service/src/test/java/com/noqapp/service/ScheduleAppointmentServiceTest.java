package com.noqapp.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduledTaskEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.json.JsonSchedule;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.ScheduleTaskEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserPreferenceManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.exceptions.AppointmentBookingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;

class ScheduleAppointmentServiceTest {

    private int untilDaysInPast;
    private int untilDaysInFuture;
    private int appointmentCancelLimitedToHours;

    @Mock private ScheduleAppointmentManager scheduleAppointmentManager;
    @Mock private StoreHourManager storeHourManager;
    @Mock private UserProfileManager userProfileManager;
    @Mock private UserAccountManager userAccountManager;
    @Mock private UserPreferenceManager userPreferenceManager;
    @Mock private RegisteredDeviceManager registeredDeviceManager;
    @Mock private TokenQueueManager tokenQueueManager;
    @Mock private ScheduledTaskManager scheduledTaskManager;

    @Mock private BizService bizService;
    @Mock private FirebaseMessageService firebaseMessageService;
    @Mock private MailService mailService;
    @Mock private StoreHourService storeHourService;

    private ScheduleAppointmentService scheduleAppointmentService;
    private JsonSchedule jsonSchedule;
    private BizStoreEntity bizStoreEntity;
    private StoreHourEntity storeHour;
    private ScheduledTaskEntity scheduledTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduleAppointmentService = new ScheduleAppointmentService(
            60,
            2,
            24,
            "no-reply@noqapp.com",
            "NoQueue",
            scheduleAppointmentManager,
            storeHourManager,
            userProfileManager,
            userAccountManager,
            userPreferenceManager,
            registeredDeviceManager,
            tokenQueueManager,
            scheduledTaskManager,
            bizService,
            firebaseMessageService,
            mailService,
            storeHourService
        );

        jsonSchedule = new JsonSchedule()
            .setCodeQR("");

        bizStoreEntity = new BizStoreEntity();
        bizStoreEntity.setId("A");
        bizStoreEntity
            .setCodeQR("codeQR").setTimeZone("Asia/Calcutta")
            .setScheduledTaskId("scheduleTaskId")
            .setDisplayName("Store Service Name");

        storeHour = new StoreHourEntity("A", 1);
    }

    @Test
    void bookAppointment_Fail_Appointment_Not_Accepting() {
        jsonSchedule.setScheduleDate(DateUtil.dateToString(DateUtil.asDate(LocalDate.now())));

        bizStoreEntity.setAppointmentState(AppointmentStateEnum.O);
        when(bizService.findByCodeQR(anyString())).thenReturn(bizStoreEntity);

        Exception exception = assertThrows(AppointmentBookingException.class, () -> scheduleAppointmentService.bookAppointment("1", jsonSchedule));
        assertEquals("Booking failed as " + bizStoreEntity.getDisplayName() + " is not accepting appointments", exception.getMessage());
    }

    @Test
    void bookAppointment_Fail_Store_Closed() {
        jsonSchedule.setScheduleDate(DateUtil.dateToString(DateUtil.asDate(LocalDate.now())));

        bizStoreEntity.setAppointmentState(AppointmentStateEnum.S);
        when(bizService.findByCodeQR(anyString())).thenReturn(bizStoreEntity);

        storeHour.setDayClosed(true);
        doReturn(storeHour).when(storeHourManager).findOne(anyString(), any(DayOfWeek.class));

        Exception exception = assertThrows(AppointmentBookingException.class, () -> scheduleAppointmentService.bookAppointment("1", jsonSchedule));
        assertEquals("Booking failed as " + bizStoreEntity.getDisplayName() + " is closed for the day", exception.getMessage());
    }

    @Test
    void bookAppointment_Fail_When_Scheduled_Off() {
        jsonSchedule.setScheduleDate(DateUtil.dateToString(DateUtil.asDate(LocalDate.now())));

        bizStoreEntity.setAppointmentState(AppointmentStateEnum.S);
        when(bizService.findByCodeQR(anyString())).thenReturn(bizStoreEntity);

        storeHour.setDayClosed(false);
        doReturn(storeHour).when(storeHourManager).findOne(anyString(), any(DayOfWeek.class));

        scheduledTask = new ScheduledTaskEntity()
            .setScheduleTask(ScheduleTaskEnum.CLOSE)
            .setFrom(DateUtil.dateToString(DateUtil.asDate(LocalDate.now().minusDays(1))))
            .setUntil(DateUtil.dateToString(DateUtil.asDate(LocalDate.now().plusDays(1))));
        when(scheduledTaskManager.findOneById(anyString())).thenReturn(scheduledTask);

        Exception exception = assertThrows(AppointmentBookingException.class, () -> scheduleAppointmentService.bookAppointment("1", jsonSchedule));
        assertEquals("Booking failed as " + bizStoreEntity.getDisplayName() + " is closed on that day", exception.getMessage());
    }
}
