package com.noqapp.loader.scheduledtasks;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.JsonLatestAppVersion;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.loader.ITest;
import com.noqapp.repository.ScheduleAppointmentManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * hitender
 * 10/5/20 6:58 PM
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentFlexAndWalkinITest extends ITest {

    private AppointmentFlexAndWalkin appointmentFlexAndWalkin;

    @BeforeEach
    void setUp() {
        appointmentFlexAndWalkin = new AppointmentFlexAndWalkin(
            "ON",
            scheduleAppointmentManager,
            bizStoreManager,
            tokenQueueService,
            deviceService,
            bizService,
            statsCronService,
            computeNextRunService
        );
    }

    @Test
    void scheduleToWalkin() {
        BizNameEntity bizName = bizService.findByPhone("9118000000041");
        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores) {
            bizStore.setAppointmentState(AppointmentStateEnum.S);
            Date date = Date.from(Instant.now().minus(Duration.ofDays(1)));
            bizStore.setQueueHistory(date);
            bizStore.setQueueAppointment(date);
            bizStoreManager.save(bizStore);
        }

        BizStoreEntity bizStore = bizService.findOneBizStore(bizName.getId());
        UserProfileEntity userProfile = userProfileManager.findOneByPhone("9118000000001");
        registeredDeviceManager.save(
            RegisteredDeviceEntity.newInstance(
                userProfile.getQueueUserId(),
                UUID.randomUUID().toString(),
                DeviceTypeEnum.A,
                AppFlavorEnum.NQCL,
                fcmToken,
                appVersion,
                null,
                null));

        ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
            .setCodeQR(bizStore.getCodeQR())
            .setScheduleDate(DateUtil.dateToString(new Date()))
            .setStartTime(100)
            .setEndTime(200)
            .setQueueUserId(userProfile.getQueueUserId())
            .setGuardianQid(null)
            .setAppointmentStatus(AppointmentStatusEnum.A)
            .setChiefComplain(null);

        scheduleAppointmentManager.save(scheduleAppointment);
        appointmentFlexAndWalkin.scheduleToWalkin();

        ScheduleAppointmentEntity updateAppointment = scheduleAppointmentManager.findAppointment(scheduleAppointment.getId(), scheduleAppointment.getQueueUserId(), scheduleAppointment.getCodeQR());
        assertEquals(updateAppointment.getAppointmentStatus(), AppointmentStatusEnum.W);
    }
}
