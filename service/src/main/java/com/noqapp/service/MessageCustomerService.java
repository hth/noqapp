package com.noqapp.service;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.NotificationMessageManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private UserProfileManager userProfileManager;

    private int limitedToDays;

    /* Supported max limit by Firebase. */
    private final static int maxTokenLimit = 1_000;

    @Autowired
    public MessageCustomerService(
        @Value("${MessageCustomerService.limitedToDays}")
        int limitedToDays,

        QueueService queueService,
        TokenQueueService tokenQueueService,
        NotificationMessageManager notificationMessageManager,
        RegisteredDeviceManager registeredDeviceManager,
        FirebaseService firebaseService,
        BizService bizService,
        UserProfileManager userProfileManager
    ) {
        this.limitedToDays = limitedToDays;

        this.queueService = queueService;
        this.tokenQueueService = tokenQueueService;
        this.notificationMessageManager = notificationMessageManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
        this.bizService = bizService;
        this.userProfileManager = userProfileManager;
    }

    @Mobile
    @Async
    public void sendMessageToSubscribers(String title, String body, List<String> codeQRs, String qid) {
        try {
            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setQueueUserId(qid);
            notificationMessageManager.save(notificationMessage);

            int messageSendCount = 0;
            for (String codeQR : codeQRs) {
                BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
                TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
                messageSendCount =+ tokenQueue.getLastNumber();
                sendMessageToSubscriber(
                    title,
                    CommonUtil.appendBusinessNameToNotificationMessage(body, bizStore.getBizName().getBusinessName()),
                    tokenQueue);
            }

            notificationMessage.setMessageSendCount(messageSendCount);
            notificationMessageManager.save(notificationMessage);
        } catch (Exception e) {
            LOG.error("Failed sending message qid={} title=\"{}\" body=\"{}\"", qid, title, body);
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

    private void sendMessageToSubscriber(String title, String body, TokenQueueEntity tokenQueue) {
        tokenQueueService.sendAlertMessageToAllOnSpecificTopic(title, body, tokenQueue, QueueStatusEnum.C);
    }

    public int sendMessageToPastClients(String bizNameId) {
        return queueService.countDistinctQIDsInBiz(bizNameId, limitedToDays);
    }

    public int sendMessageToPastClients(String bizNameId, int days) {
        return queueService.countDistinctQIDsInBiz(bizNameId, days == 0 ? limitedToDays : days);
    }

    public void sendMessageToAll(String title, String body, String qid, String subscribedTopic) {
        sendMessageToAll(title, body, null, qid, subscribedTopic);
    }

    public void sendMessageToAll(String title, String body, String imageURL, String qid, String subscribedTopic) {
        try {
            Assert.hasText(subscribedTopic, "Subscribed topic cannot be empty");

            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setQueueUserId(qid);
            notificationMessageManager.save(notificationMessage);

            if (StringUtils.isBlank(imageURL)) {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(subscribedTopic, deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(title, body, topic, MessageOriginEnum.A, deviceType);
                }
            } else {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(subscribedTopic, deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(title, body, imageURL, topic, MessageOriginEnum.A, deviceType);
                }
            }

            notificationMessageManager.save(notificationMessage);
        } catch (Exception e) {
            LOG.error("Failed sending message to all {} {} {} reason={}", title, body, qid, e.getMessage(), e);
        }
    }

    public int sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        try {
            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setQueueUserId(qid);
            notificationMessageManager.save(notificationMessage);

            int sendMessageCount = sendMessageToPastClients(bizNameId);
            LOG.info("Sending message by {} total send={} \"{}\" \"{}\" {}", qid, sendMessageCount, title, body, bizNameId);

            List<String> tokens_A = new ArrayList<>();
            List<String> tokens_I = new ArrayList<>();
            BizNameEntity bizName = bizService.getByBizNameId(bizNameId);
            queueService.distinctQIDsInBiz(bizNameId, limitedToDays).stream().iterator()
                .forEachRemaining(senderQid -> {
                    RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(senderQid);
                    try {
                        switch (registeredDevice.getDeviceType()) {
                            case A:
                                tokens_A.add(registeredDevice.getToken());
                                break;
                            case I:
                                tokens_I.add(registeredDevice.getToken());
                                break;
                            case W:
                                //Do nothing
                                break;
                        }
                    } catch (Exception e) {
                        LOG.error("Failed adding token {} {} {} {}", senderQid, bizName.getBusinessName(), bizNameId, e.getMessage());
                    }
                });

            for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                String topic = CommonUtil.buildTopic(bizName.getCountryShortName() + UNDER_SCORE + bizNameId, deviceType.name());
                switch (deviceType) {
                    case A:
                        if (subscribeToTopic(tokens_A, topic)) {
                            tokenQueueService.sendBulkMessageToBusinessUser(title, body, topic, MessageOriginEnum.A, deviceType);
                        }
                        break;
                    case I:
                        if (subscribeToTopic(tokens_I, topic)) {
                            tokenQueueService.sendBulkMessageToBusinessUser(title, body, topic, MessageOriginEnum.A, deviceType);
                        }
                        break;
                    case W:
                        //Do nothing
                        break;
                }
            }

            notificationMessage.setMessageSendCount(sendMessageCount);
            notificationMessageManager.save(notificationMessage);
            return sendMessageCount;
        } catch (Exception e) {
            LOG.error("Failed sending message {} {} reason={}", bizNameId, qid, e.getMessage(), e);
            return 0;
        }
    }

    public boolean subscribeToTopic(List<String> tokens, String topic) {
        int size = tokens.size();
        if (size == 0) {
            return false;
        }

        if (size > maxTokenLimit) {
            Collection<List<String>> collectionOfTokens = CommonUtil.partitionBasedOnSize(tokens, maxTokenLimit);
            for (List<String> collectionOfToken : collectionOfTokens) {
                firebaseService.subscribeToTopic(collectionOfToken, topic);
            }
        } else {
            firebaseService.subscribeToTopic(tokens, topic);
        }

        return true;
    }
}
