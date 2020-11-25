package com.noqapp.service;

import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.common.ComposeMessagesForFCM;
import com.noqapp.domain.json.JsonTokenAndQueueList;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 11/24/20 11:46 AM
 */
@Service
public class NotifyMobileService {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyMobileService.class);

    private PurchaseOrderService purchaseOrderService;
    private PurchaseOrderProductService purchaseOrderProductService;
    private FirebaseMessageService firebaseMessageService;
    private FirebaseService firebaseService;
    private TokenQueueService tokenQueueService;
    private QueueService queueService;

    @Autowired
    public NotifyMobileService(
        PurchaseOrderService purchaseOrderService,
        PurchaseOrderProductService purchaseOrderProductService,
        FirebaseMessageService firebaseMessageService,
        FirebaseService firebaseService,
        TokenQueueService tokenQueueService,
        QueueService queueService
    ) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderProductService = purchaseOrderProductService;
        this.firebaseMessageService = firebaseMessageService;
        this.firebaseService = firebaseService;
        this.tokenQueueService = tokenQueueService;
        this.queueService = queueService;
    }

    /** Sends personal message with all the current queue and orders. */
    @Async
    public void notifyClient(RegisteredDeviceEntity registeredDevice, String title, String body, String codeQR) {
        if (null != registeredDevice) {
            JsonTokenAndQueueList jsonTokenAndQueues = queueService.findAllJoinedQueues(registeredDevice.getQueueUserId(), registeredDevice.getDeviceId());
            jsonTokenAndQueues.getTokenAndQueues().addAll(purchaseOrderService.findAllOpenOrderAsJson(registeredDevice.getQueueUserId()));

            JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessage(
                registeredDevice,
                jsonTokenAndQueues.getTokenAndQueues(),
                body,
                title,
                codeQR,
                MessageOriginEnum.CQO);
            firebaseMessageService.messageToTopic(jsonMessage);
        }
    }

    /** Subscribes client to a topic when merchant adds client to queue. */
    @Async
    public void autoSubscribeClientToTopic(String codeQR, String token, DeviceTypeEnum deviceType) {
        if (StringUtils.isNotBlank(token)) {
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
            List<String> registeredTokens = new ArrayList<>() {{
                add(token);
            }};
            firebaseService.subscribeToTopic(registeredTokens, tokenQueue.getTopic() + "_" + deviceType.getName());
        }
    }
}
