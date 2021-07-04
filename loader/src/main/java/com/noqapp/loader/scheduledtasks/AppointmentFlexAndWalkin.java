package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.ScheduleAppointmentEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.AppointmentStatusEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.loader.service.ComputeNextRunService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.ScheduleAppointmentManager;
import com.noqapp.service.DeviceService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.NotifyMobileService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.StoreHourService;
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

    private TokenQueueService tokenQueueService;
    private DeviceService deviceService;
    private StatsCronService statsCronService;
    private ComputeNextRunService computeNextRunService;
    private NotifyMobileService notifyMobileService;
    private StoreHourService storeHourService;
    private MessageCustomerService messageCustomerService;

    private String moveScheduledAppointmentToWalkin;
    private StatsCronEntity statsCron;

    @Autowired
    public AppointmentFlexAndWalkin(
        @Value("${AppointmentFlexAndWalkin.moveScheduledAppointmentToWalkin}")
        String moveScheduledAppointmentToWalkin,

        ScheduleAppointmentManager scheduleAppointmentManager,
        BizStoreManager bizStoreManager,

        TokenQueueService tokenQueueService,
        DeviceService deviceService,
        StatsCronService statsCronService,
        ComputeNextRunService computeNextRunService,
        NotifyMobileService notifyMobileService,
        StoreHourService storeHourService,
        MessageCustomerService messageCustomerService
    ) {
        this.moveScheduledAppointmentToWalkin = moveScheduledAppointmentToWalkin;

        this.scheduleAppointmentManager = scheduleAppointmentManager;
        this.bizStoreManager = bizStoreManager;
        this.tokenQueueService = tokenQueueService;
        this.deviceService = deviceService;
        this.statsCronService = statsCronService;
        this.computeNextRunService = computeNextRunService;
        this.notifyMobileService = notifyMobileService;
        this.storeHourService = storeHourService;
        this.messageCustomerService = messageCustomerService;
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
            JsonToken jsonToken = tokenQueueService.getNextToken(
                bizStore.getCodeQR(),
                deviceService.findRegisteredDeviceByQid(scheduleAppointment.getQueueUserId()).getDeviceId(),
                scheduleAppointment.getQueueUserId(),
                scheduleAppointment.getGuardianQid(),
                bizStore.getAverageServiceTime(),
                TokenServiceEnum.S);

            /* Do not change the state if token is not issued. Will help in rerun of the appointment. */
            if (0 != jsonToken.getToken()) {
                scheduleAppointment.setAppointmentStatus(AppointmentStatusEnum.W);
                scheduleAppointmentManager.save(scheduleAppointment);

                RegisteredDeviceEntity registeredDevice = deviceService.findRecentDevice(
                    StringUtils.isBlank(scheduleAppointment.getQueueUserId())
                        ? scheduleAppointment.getGuardianQid()
                        : scheduleAppointment.getQueueUserId());
                if (null != registeredDevice) {
                    notifyMobileService.autoSubscribeClientToTopic(
                        jsonToken.getCodeQR(),
                        registeredDevice.getToken(),
                        registeredDevice.getDeviceType());

                    notifyMobileService.notifyClient(
                        registeredDevice,
                        "Joined " + bizStore.getDisplayName() + " Queue",
                        "Your token number is " + jsonToken.getToken(),
                        bizStore.getCodeQR());
                }
            } else {
                messageCustomerService.sendMessageToSpecificUser(
                    bizStore.getDisplayName() + ": Token not issued",
                    jsonToken.getQueueJoinDenied().friendlyDescription(),
                    scheduleAppointment.getQueueUserId(),
                    MessageOriginEnum.A,
                    bizStore.getBusinessType());

                LOG.warn("Token not received for {} {} {} reason={}",
                    bizStore.getCodeQR(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName(),
                    jsonToken.getQueueStatus() != null ? jsonToken.getQueueStatus().getDescription() : jsonToken.getQueueStatus());
            }
        }

        if (scheduleAppointments.size() > 0) {
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(bizStore.getCodeQR());
            if (scheduleAppointments.size() != tokenQueue.getLastNumber()) {
                LOG.error("Walkin scheduleAppointment={} issuedToken={} {} for \"{}\" \"{}\"",
                    scheduleAppointments.size(),
                    tokenQueue.getLastNumber(),
                    bizStore.getCodeQR(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName());
            } else {
                LOG.info("Walkin scheduleAppointment={} issuedToken={} {} for \"{}\" \"{}\"",
                    scheduleAppointments.size(),
                    tokenQueue.getLastNumber(),
                    bizStore.getCodeQR(),
                    bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName());
            }
        }
    }
}
