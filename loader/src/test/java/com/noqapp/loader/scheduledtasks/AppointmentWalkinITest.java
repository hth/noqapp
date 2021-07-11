package com.noqapp.loader.scheduledtasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.AppFlavorEnum;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.loader.ITest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * hitender
 * 10/5/20 6:58 PM
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppointmentWalkinITest extends ITest {

    private AppointmentWalkin appointmentWalkin;

    @BeforeEach
    void setUp() {
        appointmentWalkin = new AppointmentWalkin(
            "ON",
            scheduleAppointmentManager,
            bizStoreManager,
            registeredDeviceManager,

            notifyMobileService,
            messageCustomerService,
            tokenQueueService,
            deviceService,
            statsCronService,
            computeNextRunService,
            storeHourService
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
        UserProfileEntity userProfile1 = userProfileManager.findOneByPhone("9118000000001");
        ScheduleAppointmentEntity scheduleAppointment1 = scheduleAppointment(bizStore, userProfile1);

        UserProfileEntity userProfile2 = userProfileManager.findOneByPhone("9118000000002");
        ScheduleAppointmentEntity scheduleAppointment2 = scheduleAppointment(bizStore, userProfile2);
        scheduleAppointmentManager.cancelAppointment(scheduleAppointment2.getId(), scheduleAppointment2.getQueueUserId(), scheduleAppointment2.getCodeQR());

        appointmentWalkin.scheduleToWalkin();

        ScheduleAppointmentEntity updateAppointment1 = scheduleAppointmentManager.findAppointment(scheduleAppointment1.getId(), scheduleAppointment1.getQueueUserId(), scheduleAppointment1.getCodeQR());
        assertEquals(updateAppointment1.getAppointmentStatus(), AppointmentStatusEnum.W);
        assertEquals(bizStore.getAppointmentState(), updateAppointment1.getAppointmentState());

        ScheduleAppointmentEntity updateAppointment2 = scheduleAppointmentManager.findAppointment(scheduleAppointment2.getId(), scheduleAppointment2.getQueueUserId(), scheduleAppointment2.getCodeQR());
        assertEquals(updateAppointment2.getAppointmentStatus(), AppointmentStatusEnum.C);
    }

    private ScheduleAppointmentEntity scheduleAppointment(BizStoreEntity bizStore, UserProfileEntity userProfile1) {
        registeredDeviceManager.save(
            RegisteredDeviceEntity.newInstance(
                userProfile1.getQueueUserId(),
                UUID.randomUUID().toString(),
                DeviceTypeEnum.A,
                AppFlavorEnum.NQCL,
                fcmToken,
                appVersion,
                null,
                new double[] {0, 0},
                null));

        ScheduleAppointmentEntity scheduleAppointment = new ScheduleAppointmentEntity()
            .setCodeQR(bizStore.getCodeQR())
            .setScheduleDate(DateUtil.dateToString(new Date()))
            .setStartTime(100)
            .setEndTime(200)
            .setQueueUserId(userProfile1.getQueueUserId())
            .setGuardianQid(null)
            .setAppointmentStatus(AppointmentStatusEnum.A)
            .setChiefComplain(null)
            .setAppointmentState(bizStore.getAppointmentState());

        scheduleAppointmentManager.save(scheduleAppointment);
        return scheduleAppointment;
    }
}
