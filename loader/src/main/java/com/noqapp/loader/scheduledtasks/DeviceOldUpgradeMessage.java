package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.StatsCronService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Send message to those devices that would not be supported in the future.
 * hitender
 * 3/29/21 6:21 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class DeviceOldUpgradeMessage {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceOldUpgradeMessage.class);

    private String messageOldDevicesSwitch;

    private RegisteredDeviceManager registeredDeviceManager;
    private MessageCustomerService messageCustomerService;
    private StatsCronService statsCronService;

    /* Set cache parameters. */
    private static Cache<String, String> cache;

    @Autowired
    public DeviceOldUpgradeMessage(
        @Value("${DeviceOldUpgradeMessage.messageOldDevicesSwitch:ON}")
        String messageOldDevicesSwitch,

        RegisteredDeviceManager registeredDeviceManager,
        MessageCustomerService messageCustomerService,
        StatsCronService statsCronService
    ) {
        this.messageOldDevicesSwitch = messageOldDevicesSwitch;

        this.registeredDeviceManager = registeredDeviceManager;
        this.messageCustomerService = messageCustomerService;
        this.statsCronService = statsCronService;

        cache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();
    }

    @Scheduled(fixedDelayString = "${loader.DeviceOldUpgradeMessage.messageOldDevices}")
    public void messageOldDevices() {
        StatsCronEntity statsCron = new StatsCronEntity(
            DeviceOldUpgradeMessage.class.getName(),
            "messageOldDevices",
            messageOldDevicesSwitch);

        if ("OFF".equalsIgnoreCase(messageOldDevicesSwitch)) {
            return;
        }

        int sendCount = 0, skippedCount = 0;
        try {
            List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAlmostObsoleteDevices();
            for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
                String did = cache.getIfPresent(registeredDevice.getToken());
                if (StringUtils.isBlank(did)) {
                    cache.put(registeredDevice.getToken(), registeredDevice.getDeviceId());
                    messageCustomerService.createMessageToSendToSpecificUserOrDevice(
                        "Please Upgrade Your Phone",
                        "Future version of NoQueue may not be supported on your phone. Please upgrade to a higher version to prevent NoQueue service disruption.",
                        null,
                        registeredDevice,
                        MessageOriginEnum.A,
                        BusinessTypeEnum.ZZ);
                    LOG.info("Send upgrade message {} {}", registeredDevice.getDeviceId(), registeredDevice.getQueueUserId());
                    sendCount++;
                } else {
                    LOG.info("Skipped upgrade message {}", did);
                    skippedCount++;
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending obsolete device message reason={}", e.getLocalizedMessage(), e);
        } finally {
            statsCron.addStats("sendCount", sendCount);
            statsCron.addStats("skippedCount", skippedCount);
            statsCronService.save(statsCron);
            LOG.info("Device obsolete message send sendCount={} skippedCount={}", sendCount, skippedCount);
        }
    }
}
