package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.loader.service.ComputeNextRunService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.loader.service.AfterAppointmentToTokenService;
import com.noqapp.service.DeviceService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.NotifyMobileService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.StoreHourService;
import com.noqapp.service.SubscribeTopicService;
import com.noqapp.service.TokenQueueService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class AppointmentFlexAndWalkin {
    private static final Logger LOG = LoggerFactory.getLogger(AppointmentFlexAndWalkin.class);

    private ScheduleAppointmentManager scheduleAppointmentManager;
    private BizStoreManager bizStoreManager;

    private SubscribeTopicService subscribeTopicService;
    private TokenQueueService tokenQueueService;
    private DeviceService deviceService;
    private StatsCronService statsCronService;
    private ComputeNextRunService computeNextRunService;
    private StoreHourService storeHourService;

    private String moveScheduledAppointmentToWalkin;
    private StatsCronEntity statsCron;

    @Autowired
    public AppointmentFlexAndWalkin(
        @Value("${AppointmentFlexAndWalkin.moveScheduledAppointmentToWalkin}")
        String moveScheduledAppointmentToWalkin,

        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,

        SubscribeTopicService subscribeTopicService,
        TokenQueueService tokenQueueService,
        DeviceService deviceService,
        StatsCronService statsCronService,
        ComputeNextRunService computeNextRunService,
        StoreHourService storeHourService
    ) {
        this.moveScheduledAppointmentToWalkin = moveScheduledAppointmentToWalkin;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.subscribeTopicService = subscribeTopicService;
        this.tokenQueueService = tokenQueueService;
        this.deviceService = deviceService;
        this.statsCronService = statsCronService;
        this.computeNextRunService = computeNextRunService;
        this.storeHourService = storeHourService;
    }

    @Scheduled(fixedDelayString = "${loader.AppointmentFlexAndWalkin.scheduleToWalkin}")
    public void scheduleToWalkin() {
        statsCron = new StatsCronEntity(
            AppointmentFlexAndWalkin.class.getName(),
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
             * Appointment in stores are pushed up by 30 minutes.
             */
            Date date = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));

            /*
             * Only find stores that are active and not deleted. It processes only queues.
             */
            List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueAcceptingAppointmentForTheDay(date);
            found += bizStores.size();
            LOG.info("Stores accepting walkin found={} date={}", bizStores.size(), date);
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    bizStore.setStoreHours(storeHourService.findAllStoreHours(bizStore.getId()));
                    moveFromAppointmentToWalkin(bizStore);
                    success++;

                    bizStoreManager.updateNextRunQueueAppointment(
                        bizStore.getId(),
                        Date.from(computeNextRunService.setupTokenAvailableForTomorrow(bizStore).toInstant()));
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
        List<ScheduleAppointmentEntity> scheduleAppointments = scheduleAppointmentManager.findBookedWalkinAppointmentsForDay(
            bizStore.getCodeQR(),
            DateUtil.dateToString(now));

        for (ScheduleAppointmentEntity scheduleAppointment : scheduleAppointments) {
            String registeredDeviceOfQid =  StringUtils.isNotBlank(scheduleAppointment.getGuardianQid())
                ? scheduleAppointment.getGuardianQid()
                : scheduleAppointment.getQueueUserId();

            JsonToken jsonToken = tokenQueueService.getNextToken(
                bizStore.getCodeQR(),
                deviceService.findRegisteredDeviceByQid(registeredDeviceOfQid).getDeviceId(),
                scheduleAppointment.getQueueUserId(),
                scheduleAppointment.getGuardianQid(),
                bizStore.getAverageServiceTime(),
                TokenServiceEnum.S);

            /* Do not change the state if token is not issued. Will help in rerun of the appointment. */
            if (0 != jsonToken.getToken()) {
                scheduleAppointmentManager.changeAppointmentStatusOnTokenIssued(scheduleAppointment.getId(), AppointmentStatusEnum.W);
            } else {
                scheduleAppointmentManager.changeAppointmentStatusOnTokenIssued(scheduleAppointment.getId(), AppointmentStatusEnum.R);
            }

            subscribeTopicService.notifyAfterGettingToken(bizStore, registeredDeviceOfQid, jsonToken);
        }

        if (scheduleAppointments.size() > 0) {
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());
            if (scheduleAppointments.size() != tokenQueue.getLastNumber()) {
                LOG.error("Walkin on {} scheduleAppointment={} issuedToken={} {} for \"{}\" \"{}\"",
                    DateUtil.dateToString(now),
                    scheduleAppointments.size(),
                    tokenQueue.getLastNumber(),
                    bizStore.getCodeQR(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName());
            } else {
                LOG.info("Walkin on {} scheduleAppointment={} issuedToken={} {} for \"{}\" \"{}\"",
                    DateUtil.dateToString(now),
                    scheduleAppointments.size(),
                    tokenQueue.getLastNumber(),
                    bizStore.getCodeQR(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName());
            }
        }
    }
}
