package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.Constants.DAYS_15;
import static com.noqapp.common.utils.Constants.TEN_METERS_IN_KILOMETER;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.DeviceService;
import com.noqapp.service.StatsCronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * hitender
 * 6/20/21 7:36 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class LimitedAccessZone {
    private static final Logger LOG = LoggerFactory.getLogger(LimitedAccessZone.class);

    private final String listDevicesInLimitedZoneSwitch;

    private DeviceService deviceService;
    private AccountService accountService;
    private StatsCronService statsCronService;

    @Autowired
    public LimitedAccessZone(
        @Value("${LimitedAccessZone.listDevicesInLimitedZoneSwitch}")
        String listDevicesInLimitedZoneSwitch,

        DeviceService deviceService,
        AccountService accountService,
        StatsCronService statsCronService
    ) {
        this.listDevicesInLimitedZoneSwitch = listDevicesInLimitedZoneSwitch;

        this.deviceService = deviceService;
        this.accountService = accountService;
        this.statsCronService = statsCronService;
    }

    @Scheduled(cron = "${loader.DailyRegistrationStatusMail.registrationStatusMail}")
    public void listDevicesInLimitedZone() {
        StatsCronEntity statsCron = new StatsCronEntity(
            ExpireRecordsProcess.class.getName(),
            "listDevicesInLimitedZone",
            listDevicesInLimitedZoneSwitch);

        if ("OFF".equalsIgnoreCase(listDevicesInLimitedZoneSwitch)) {
            return;
        }

        AtomicInteger failure = new AtomicInteger();
        AtomicLong recordsFound = new AtomicLong();
        try {
            LOG.info("Finding proximity devices {} meters", TEN_METERS_IN_KILOMETER * 1000);
            try (Stream<UserAccountEntity> userAccounts = accountService.getAccountsWithLimitedAccess(AccountInactiveReasonEnum.LIM)) {
                userAccounts.iterator().forEachRemaining(userAccount -> {
                    RegisteredDeviceEntity registeredDevice = deviceService.findRecentDevice(userAccount.getQueueUserId());
                    GeoJsonPoint from = registeredDevice.getPoint();

                    Map<String, RegisteredDeviceEntity> found = new HashMap<>();
                    try (Stream<GeoResult<RegisteredDeviceEntity>> geoResults = deviceService.findInProximity(registeredDevice.getPoint(), TEN_METERS_IN_KILOMETER, DAYS_15)) {
                        geoResults.iterator().forEachRemaining(registeredDeviceEntityGeoResult -> {
                            try {
                                found.put(registeredDeviceEntityGeoResult.getContent().getDeviceId(), registeredDeviceEntityGeoResult.getContent());
                                recordsFound.getAndIncrement();
                            } catch (Exception e) {
                                failure.getAndIncrement();
                                LOG.error("Failed finding proximity devices {} {} {}",
                                    registeredDeviceEntityGeoResult.getContent().getId(),
                                    registeredDeviceEntityGeoResult.getContent().getQueueUserId(),
                                    e.getMessage(), e);
                            }
                        });
                    }

                    StringBuilder text = new StringBuilder();
                    for (String key : found.keySet()) {
                        RegisteredDeviceEntity toRegisteredDevice = found.get(key);

                        text.append("\n").append(key).append(" - ")
                            .append(toRegisteredDevice.getDeviceId()).append(" ")
                            .append(CommonUtil.distanceInMeters(from, toRegisteredDevice.getPoint())).append(" m, ")
                            .append(DateUtil.convertDateToStringOf_DTF_DD_MMM_YYYY(toRegisteredDevice.getUpdated()));
                    }

                    LOG.info("Proximity found={} device of {} for {} are {}",
                        found.size(),
                        userAccount.getAccountInactiveReason().name(),
                        userAccount.getQueueUserId(),
                        text);
                });
            }
        } catch (Exception e) {
            LOG.error("Failed finding devices in proximity reason={}", e.getLocalizedMessage(), e);
            failure.getAndIncrement();
        } finally {
            if (0 != recordsFound.get() || 0 != failure.get()) {
                statsCron.addStats("failure", failure.get());
                statsCron.addStats("success", recordsFound.get());
                statsCronService.save(statsCron);

                /* Without if condition it is too noisy. */
                LOG.info("Complete recordsFound={} failure={}", recordsFound, failure);
            }
        }
    }
}
