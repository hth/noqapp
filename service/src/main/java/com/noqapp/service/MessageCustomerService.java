package com.noqapp.service;

import static com.noqapp.domain.BizStoreEntity.UNDER_SCORE;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.NotificationMessageManager;
import com.noqapp.repository.RegisteredDeviceManager;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * hitender
 * 6/4/20 6:57 PM
 */
@Service
public class MessageCustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageCustomerService.class);

    private QueueService queueService;
    private TokenQueueService tokenQueueService;
    private NotificationMessageManager notificationMessageManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private FirebaseService firebaseService;
    private BizService bizService;

    @Autowired
    public MessageCustomerService(
        QueueService queueService,
        TokenQueueService tokenQueueService,
        NotificationMessageManager notificationMessageManager,
        RegisteredDeviceManager registeredDeviceManager,
        FirebaseService firebaseService,
        BizService bizService
    ) {
        this.queueService = queueService;
        this.tokenQueueService = tokenQueueService;
        this.notificationMessageManager = notificationMessageManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
        this.bizService = bizService;
    }

    @Mobile
    @Async
    public void sendMessageToSubscribers(String title, String body, List<String> codeQRs, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        for (String codeQR : codeQRs) {
            sendMessageToSubscriber(title, body, codeQR);
        }
    }

    public void sendMessageToSubscriber(String title, String body, String codeQR, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        tokenQueueService.sendAlertMessageToAllOnSpecificTopic(title, body, tokenQueue, QueueStatusEnum.C);
    }

    private void sendMessageToSubscriber(String title, String body, String codeQR) {
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        tokenQueueService.sendAlertMessageToAllOnSpecificTopic(title, body, tokenQueue, QueueStatusEnum.C);
    }

    public int sendMessageToPastClients(String bizNameId) {
        return queueService.countDistinctQIDsInBiz(bizNameId, 60);
    }

    public int sendMessageToPastClients(String bizNameId, int days) {
        return queueService.countDistinctQIDsInBiz(bizNameId, days == 0 ? 60 : days);
    }

    @Deprecated
    public int sendMessageToPastClients_Old(String title, String body, String bizNameId, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        int sendMessageCount = sendMessageToPastClients(bizNameId);
        LOG.info("Sending message by {} total send={} {} {} {}", qid, sendMessageCount, title, body, bizNameId);
        queueService.distinctQIDsInBiz(bizNameId, 60).stream().iterator()
            .forEachRemaining(senderQid -> tokenQueueService.sendMessageToSpecificUser(title, body, senderQid, MessageOriginEnum.A));

        notificationMessage.setMessageSendCount(sendMessageCount);
        notificationMessageManager.save(notificationMessage);
        return sendMessageCount;
    }

    public int sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        int sendMessageCount = sendMessageToPastClients(bizNameId);
        LOG.info("Sending message by {} total send={} {} {} {}", qid, sendMessageCount, title, body, bizNameId);

        List<String> tokens_A = new ArrayList<>();
        List<String> tokens_I = new ArrayList<>();
        queueService.distinctQIDsInBiz(bizNameId, 60).stream().iterator()
            .forEachRemaining(senderQid -> {
                RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(senderQid);
                switch (registeredDevice.getDeviceType()) {
                    case I:
                        tokens_I.add(registeredDevice.getToken());
                        break;
                    case A:
                        tokens_A.add(registeredDevice.getToken());
                        break;
                }
            });

        BizNameEntity bizName = bizService.getByBizNameId(bizNameId);
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            String topic = "/topics/" + bizName.getCountryShortName() + UNDER_SCORE + bizNameId + UNDER_SCORE + deviceType.name();
            switch (deviceType) {
                case A:
                    Collection<List<String>> collectionOfTokens = CommonUtil.partitionBasedOnSize(tokens_A, 1000);
                    for (List<String> collectionOfToken : collectionOfTokens) {
                        firebaseService.subscribeToTopic(collectionOfToken, topic);
                    }
                    break;
                case I:
                    collectionOfTokens = CommonUtil.partitionBasedOnSize(tokens_I, 1000);
                    for (List<String> collectionOfToken : collectionOfTokens) {
                        firebaseService.subscribeToTopic(collectionOfToken, topic);
                    }
                    break;
            }
            tokenQueueService.sendBulkMessageToBusinessUser(title, body, topic, MessageOriginEnum.A, deviceType);
        }

        notificationMessage.setMessageSendCount(sendMessageCount);
        notificationMessageManager.save(notificationMessage);
        return sendMessageCount;
    }
}
