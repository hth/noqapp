package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.Constants.TEN_METERS_IN_KILOMETER;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.DeviceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.stream.Stream;

/**
 * hitender
 * 6/20/21 7:36 PM
 */
public class LimitedAccessZone {
    private static final Logger LOG = LoggerFactory.getLogger(LimitedAccessZone.class);

    private DeviceService deviceService;
    private AccountService accountService;

    @Autowired
    public LimitedAccessZone(DeviceService deviceService, AccountService accountService) {
        this.deviceService = deviceService;
        this.accountService = accountService;
    }

    @Scheduled(cron = "${loader.DailyRegistrationStatusMail.registrationStatusMail}")
    public void listDevicesInLimitedZone() {
        try (Stream<UserAccountEntity> userAccounts = accountService.getAccountsWithLimitedAccess(AccountInactiveReasonEnum.LIM)) {
            userAccounts.iterator().forEachRemaining(userAccount -> {
                RegisteredDeviceEntity registeredDevice = deviceService.findRecentDevice(userAccount.getQueueUserId());
                try (Stream<GeoResult<RegisteredDeviceEntity>> geoResults = deviceService.findInProximity(registeredDevice.getPoint(), TEN_METERS_IN_KILOMETER)) {
                    geoResults.iterator().forEachRemaining(registeredDeviceEntityGeoResult -> {
                        try {
                            LOG.info("Proximity device of {} for {} are {} {}",
                                userAccount.getAccountInactiveReason().name(),
                                userAccount.getQueueUserId(),
                                registeredDeviceEntityGeoResult.getContent().getDeviceId(),
                                registeredDeviceEntityGeoResult.getContent().getQueueUserId());
                        } catch (Exception e) {
                            LOG.error("Failed finding proximity devices {} {} {}",
                                registeredDeviceEntityGeoResult.getContent().getId(),
                                registeredDeviceEntityGeoResult.getContent().getQueueUserId(),
                                e.getMessage(), e);
                        }
                    });
                }
            });
        }
    }
}
