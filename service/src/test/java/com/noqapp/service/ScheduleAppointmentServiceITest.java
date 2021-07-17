package com.noqapp.service;

import static com.noqapp.common.utils.DateUtil.DTF_YYYY_MM_DD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonSchedule;
import com.noqapp.domain.json.JsonScheduleFlex;
import com.noqapp.domain.json.JsonScheduleList;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.service.exceptions.AppointmentBookingException;
import com.noqapp.service.utils.ServiceUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 7/14/21 1:12 PM
 */
@DisplayName("Account Service API")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("api")
class ScheduleAppointmentServiceITest extends ITest {

    private BizStoreEntity bizStore;
    private StoreHourEntity storeHour;

    private String timeZone;
    private String dateAsString;

    @BeforeEach
    void setUp() {
        ZonedDateTime zonedDateTime = DateUtil.getZonedDateTimeAtUTC();
        if (zonedDateTime.getHour() < 12) {
            timeZone = "Asia/Calcutta";
        } else {
            timeZone = "Pacific/Honolulu";
        }

        registerStore();
        dateAsString = DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD);

        Authentication authentication = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /** Registers store. */
    private void registerStore() {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        bizStore = bizService.findOneBizStore(bizName.getId());
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.O);
        List<StoreHourEntity> storeHours = storeHourService.findAllStoreHours(bizStore.getId());
        setBizStoreHours(bizStore, storeHours);
    }

    private void setBizStoreHours(BizStoreEntity bizStore, List<StoreHourEntity> storeHours) {
        bizStore.setAverageServiceTime(114000).setAvailableTokenCount(200);
        bizStore.setTimeZone(timeZone);
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

    @Test
    void bookAppointment() {
        AppointmentStateEnum appointmentState = bizStore.getAppointmentState();
        assertEquals(AppointmentStateEnum.O, appointmentState);

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone("9118000000001");
        JsonSchedule jsonSchedule = new JsonSchedule()
            .setCodeQR(bizStore.getCodeQR())
            .setScheduleDate(dateAsString)
            .setStartTime(1000)
            .setEndTime(1030)
            .setQueueUserId(userProfile.getQueueUserId());
        Exception exception = assertThrows(AppointmentBookingException.class, () -> scheduleAppointmentService.bookAppointment(userProfile.getQueueUserId(), jsonSchedule));
        assertEquals("Booking failed as " + bizStore.getDisplayName() + " is not accepting appointments", exception.getMessage());

        bizStore = bizService.findByCodeQR(bizStore.getCodeQR());
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.A);
        bizService.saveStore(bizStore, "Changed appointment type");

        JsonSchedule jsonScheduleAfterAppointment = scheduleAppointmentService.bookAppointment(userProfile.getQueueUserId(), jsonSchedule);
        assertEquals(AppointmentStatusEnum.A, jsonScheduleAfterAppointment.getAppointmentStatus());
    }

    @Test
    void findBookedAppointmentsForDayAsJson_TraditionalAppointments() {
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone("9118000000001");
        JsonSchedule jsonSchedule = new JsonSchedule()
            .setCodeQR(bizStore.getCodeQR())
            .setScheduleDate(dateAsString)
            .setStartTime(1000)
            .setEndTime(1030)
            .setQueueUserId(userProfile.getQueueUserId());
        bizStore = bizService.findByCodeQR(bizStore.getCodeQR());
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.A);
        bizService.saveStore(bizStore, "Changed appointment type");

        JsonSchedule jsonScheduleAfterAppointment = scheduleAppointmentService.bookAppointment(userProfile.getQueueUserId(), jsonSchedule);
        assertEquals(AppointmentStatusEnum.A, jsonScheduleAfterAppointment.getAppointmentStatus());

        JsonScheduleList jsonScheduleList = scheduleAppointmentService.findBookedAppointmentsForDayAsJson(
            bizStore.getCodeQR(),
            dateAsString);
        assertEquals(jsonScheduleList.getJsonSchedules().size(), 1);
        assertTrue(scheduleAppointmentService.doesAppointmentExists(userProfile.getQueueUserId(), bizStore.getCodeQR(), dateAsString));
    }

    @Test
    void findBookedAppointmentsForDayAsJson_FlexAppointments() {
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone("9118000000001");
        JsonSchedule jsonSchedule = new JsonSchedule()
            .setCodeQR(bizStore.getCodeQR())
            .setScheduleDate(dateAsString)
            .setStartTime(1000)
            .setEndTime(1030)
            .setQueueUserId(userProfile.getQueueUserId());
        bizStore = bizService.findByCodeQR(bizStore.getCodeQR());
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.F);
        bizService.saveStore(bizStore, "Changed appointment type");

        JsonSchedule jsonScheduleAfterAppointment = scheduleAppointmentService.bookAppointment(userProfile.getQueueUserId(), jsonSchedule);
        assertEquals(AppointmentStatusEnum.A, jsonScheduleAfterAppointment.getAppointmentStatus());

        JsonScheduleList jsonScheduleList = scheduleAppointmentService.findBookedAppointmentsForDayAsJson(
            bizStore.getCodeQR(),
            dateAsString);
        assertEquals(jsonScheduleList.getJsonSchedules().size(), 2);
        assertTrue(jsonScheduleList.getJsonScheduleFlexes().size() > 0);
        assertTrue(scheduleAppointmentService.doesAppointmentExists(userProfile.getQueueUserId(), bizStore.getCodeQR(), dateAsString));
    }

    @Test
    void computeFlexAppointment() {
        bizStore = bizService.findByCodeQR(bizStore.getCodeQR());
        bizStore
            .setTimeZone(timeZone)
            .setAppointmentState(AppointmentStateEnum.F);
        bizService.saveStore(bizStore, "Changed appointment type");

        String modifiedDate = DateUtil.getZonedDateTimeAtUTC().format(DTF_YYYY_MM_DD);
        Set<JsonScheduleFlex> jsonScheduleFlexes = scheduleAppointmentService.computeFlexAppointment(modifiedDate, bizStore);
        assertTrue(jsonScheduleFlexes.size() > 0);
    }
}
