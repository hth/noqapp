package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.service.DeviceService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.TokenQueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-08-22 11:03
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class MoveScheduledAppointmentToWalkin {
    private static final Logger LOG = LoggerFactory.getLogger(MoveScheduledAppointmentToWalkin.class);

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private BizStoreManager bizStoreManager;
    private TokenQueueService tokenQueueService;
    private DeviceService deviceService;
    private StatsCronService statsCronService;
    private ArchiveAndReset archiveAndReset;

    private String moveScheduledAppointmentToWalkin;
    private StatsCronEntity statsCron;

    @Autowired
    public MoveScheduledAppointmentToWalkin(
        @Value("${MoveScheduledAppointmentToWalkin.moveScheduledAppointmentToWalkin}")
        String moveScheduledAppointmentToWalkin,

        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,
        TokenQueueService tokenQueueService,
        DeviceService deviceService,
        StatsCronService statsCronService,
        ArchiveAndReset archiveAndReset
    ) {
        this.moveScheduledAppointmentToWalkin = moveScheduledAppointmentToWalkin;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.tokenQueueService = tokenQueueService;
        this.deviceService = deviceService;
        this.statsCronService = statsCronService;
        this.archiveAndReset = archiveAndReset;
    }

    @Scheduled(fixedDelayString = "${loader.MoveScheduledAppointmentToWalkin.scheduleToWalkin}")
    public void scheduleToWalkin() {
        statsCron = new StatsCronEntity(
                MoveScheduledAppointmentToWalkin.class.getName(),
                "scheduleToWalkin",
                moveScheduledAppointmentToWalkin);

        int found = 0, failure = 0, success = 0;
        if ("OFF".equalsIgnoreCase(moveScheduledAppointmentToWalkin)) {
            LOG.debug("feature is {}", moveScheduledAppointmentToWalkin);
        }

        try {
            /*
             * Date is based on UTC time of the System.
             * Hence its important to run on UTC time.
             *
             * Appointment in stores are pushed by up by 15 minutes.
             */
            Date date = Date.from(Instant.now().plus(15, ChronoUnit.MINUTES));

            /*
             * Only find stores that are active and not deleted. It processes only queues.
             */
            List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueAcceptingAppointmentForTheDay(date);
            found += bizStores.size();
            LOG.info("Stores accepting walkins found={} date={}", bizStores.size(), date);
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    moveFromAppointmentToWalkin(bizStore);
                    success++;

                    /* Set date and time for next run. */
                    DayOfWeek dayOfWeek = archiveAndReset.computeDayOfWeekHistoryIsSupposeToRun(bizStore);
                    bizStore.setQueueAppointment(Date.from(archiveAndReset.setupTokenAvailableForTomorrow(bizStore, dayOfWeek).toInstant()));
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail on joining queue bizStore={} codeQR={} reason={}",
                            bizStore.getId(),
                            bizStore.getCodeQR(),
                            e.getLocalizedMessage(),
                            e);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to execute store accepting walkin");
        } finally {
            if (0 != found || 0 != failure || 0 != success) {
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("success", success);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} success={}", found, failure, success);
            }
        }
    }

    private void moveFromAppointmentToWalkin(BizStoreEntity bizStore) {
        Date now = DateUtil.dateAtTimeZone(bizStore.getTimeZone());
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedWalkinAppointmentsForDay(bizStore.getCodeQR(), DateUtil.dateToString(now));
        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            tokenQueueService.getNextToken(
                    bizStore.getCodeQR(),
                    deviceService.findRegisteredDeviceByQid(scheduleAppointment.getQueueUserId()).getDeviceId(),
                    scheduleAppointment.getQueueUserId(),
                    scheduleAppointment.getGuardianQid(),
                    bizStore.getAverageServiceTime(),
                    TokenServiceEnum.C
            );

            scheduleAppointment.setAppointmentStatus(AppointmentStatusEnum.W);
            scheduleAppointmentManager.save(scheduleAppointment);
        }
    }

}
