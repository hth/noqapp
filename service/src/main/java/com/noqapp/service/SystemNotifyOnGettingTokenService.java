package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.repository.RegisteredDeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 7/11/21 10:57 AM
 */
@Service
public class SystemNotifyOnGettingTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(SystemNotifyOnGettingTokenService.class);

    private RegisteredDeviceManager registeredDeviceManager;

    private NotifyMobileService notifyMobileService;
    private MessageCustomerService messageCustomerService;

    @Autowired
    public SystemNotifyOnGettingTokenService(
        RegisteredDeviceManager registeredDeviceManager,
        NotifyMobileService notifyMobileService,
        MessageCustomerService messageCustomerService
    ) {
        this.registeredDeviceManager = registeredDeviceManager;
        this.notifyMobileService = notifyMobileService;
        this.messageCustomerService = messageCustomerService;
    }

    @Async
    public void notifyAfterGettingToken(BizStoreEntity bizStore, String registeredDeviceOfQid, JsonToken jsonToken) {
        if (0 != jsonToken.getToken()) {
            RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(registeredDeviceOfQid);
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
                registeredDeviceOfQid,
                MessageOriginEnum.A,
                bizStore.getBusinessType());

            LOG.warn("Token not received for {} {} {} reason={}",
                bizStore.getCodeQR(),
                bizStore.getDisplayName(),
                bizStore.getBizName().getBusinessName(),
                jsonToken.getQueueStatus() != null ? jsonToken.getQueueStatus().getDescription() : jsonToken.getQueueStatus());
        }
    }
}
