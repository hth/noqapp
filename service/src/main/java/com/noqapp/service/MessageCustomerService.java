package com.noqapp.service;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.neo4j.NotificationN4j;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.NotificationMessageManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.neo4j.NotificationN4jManager;
import com.noqapp.service.graph.GraphDetailOfPerson;

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

    private GraphDetailOfPerson graphDetailOfPerson;
    private NotificationN4jManager notificationN4jManager;

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

        GraphDetailOfPerson graphDetailOfPerson,
        NotificationN4jManager notificationN4jManager
    ) {
        this.limitedToDays = limitedToDays;

        this.queueService = queueService;
        this.tokenQueueService = tokenQueueService;
        this.notificationMessageManager = notificationMessageManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
        this.bizService = bizService;

        this.graphDetailOfPerson = graphDetailOfPerson;
        this.notificationN4jManager = notificationN4jManager;
    }

    private void save(NotificationMessageEntity notificationMessage) {
        notificationMessageManager.save(notificationMessage);
        NotificationN4j notificationN4j = notificationN4jManager.findById(notificationMessage.getId());
        if (null == notificationN4j) {
            notificationN4j = new NotificationN4j().setId(notificationMessage.getId());
            notificationN4jManager.save(notificationN4j);
        }
    }

    /** Send message to all customers in the queue. */
    @Mobile
    @Async
    public void sendMessageToSubscribers(String title, String body, List<String> codeQRs, String qid) {
        try {
            for (String codeQR : codeQRs) {
                BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
                TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
                NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                    .setTitle(title)
                    .setBody(body)
                    .setTopic(tokenQueue.getCorrectTopic(QueueStatusEnum.C) + UNDER_SCORE + DeviceTypeEnum.onlyForLogging())
                    .setQueueUserId(qid)
                    .setMessageSendCount(tokenQueue.getLastNumber());
                save(notificationMessage);

                sendMessageToSubscriber(
                    notificationMessage.getId(),
                    title,
                    CommonUtil.appendBusinessNameToNotificationMessage(body, bizStore.getBizName().getBusinessName()),
                    tokenQueue);
            }
        } catch (Exception e) {
            LOG.error("Failed sending message qid={} title=\"{}\" body=\"{}\"", qid, title, body);
        }
    }

    private void sendMessageToSubscriber(String id, String title, String body, TokenQueueEntity tokenQueue) {
        tokenQueueService.sendAlertMessageToAllOnSpecificTopic(id, title, body, tokenQueue, QueueStatusEnum.C);
    }

    public int sendMessageToPastClients(String bizNameId) {
        return queueService.countDistinctQIDsInBiz(bizNameId, limitedToDays);
    }

    public int sendMessageToPastClients(String bizNameId, int days) {
        return queueService.countDistinctQIDsInBiz(bizNameId, days == 0 ? limitedToDays : days);
    }

    public void sendMessageToAll(String title, String body, String qid, String subscribedTopic) {
        //TODO change this from PA to ZZ after 1.2.700
        sendMessageToAll(title, body, null, qid, subscribedTopic, BusinessTypeEnum.PA);
    }

    public void sendMessageToAll(String title, String body, String imageURL, String qid, String subscribedTopic, BusinessTypeEnum businessType) {
        try {
            Assert.hasText(subscribedTopic, "Subscribed topic cannot be empty");

            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setImageURL(imageURL)
                .setTopic(CommonUtil.buildTopic(subscribedTopic, DeviceTypeEnum.onlyForLogging()))
                .setQueueUserId(qid);
            save(notificationMessage);

            if (StringUtils.isBlank(imageURL)) {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(subscribedTopic, deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            } else {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(subscribedTopic, deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, imageURL, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending message to all {} {} {} reason={}", title, body, qid, e.getMessage(), e);
        }
    }

    public void sendMessageToAll(String title, String body, String imageURL, String qid, BusinessTypeEnum businessType) {
        try {
            Assert.hasText(businessType.getName(), "Subscribed topic cannot be empty");

            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setImageURL(imageURL)
                .setTopic(CommonUtil.buildTopic(businessType.getName(), DeviceTypeEnum.onlyForLogging()))
                .setQueueUserId(qid);
            save(notificationMessage);

            if (StringUtils.isBlank(imageURL)) {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(businessType.getName(), deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            } else {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(businessType.getName(), deviceType.name());
                    tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, imageURL, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending message to all {} {} {} reason={}", title, body, qid, e.getMessage(), e);
        }
    }

    public int sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        try {
            BizNameEntity bizName = bizService.getByBizNameId(bizNameId);
            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setTopic(CommonUtil.buildTopic(bizName.getCountryShortName() + UNDER_SCORE + bizNameId, DeviceTypeEnum.onlyForLogging()))
                .setQueueUserId(qid);
            save(notificationMessage);

            int sendMessageCount = sendMessageToPastClients(bizNameId);
            LOG.info("Sending message by {} total send={} \"{}\" \"{}\" {}", qid, sendMessageCount, title, body, bizNameId);

            List<String> tokens_A = new ArrayList<>();
            List<String> tokens_I = new ArrayList<>();
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
                            tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, bizName.getBusinessType());
                        }
                        break;
                    case I:
                        if (subscribeToTopic(tokens_I, topic)) {
                            tokenQueueService.sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, bizName.getBusinessType());
                        }
                        break;
                    case W:
                        //Do nothing
                        break;
                }
            }

            notificationMessage.setMessageSendCount(sendMessageCount);
            save(notificationMessage);
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

    @Mobile
    public boolean increaseViewClientCount(String id, String qid) {
        try {
            graphDetailOfPerson.graphPersonWithNotification(id, qid);
        } catch (Exception e) {
            LOG.error("Failed graphing person {} {} {}", qid, id, e.getLocalizedMessage());
        }
        return notificationMessageManager.increaseViewClientCount(id);
    }

    @Mobile
    public boolean increaseViewUnregisteredCount(String id) {
        return notificationMessageManager.increaseViewUnregisteredCount(id);
    }

    @Mobile
    public boolean increaseViewBusinessCount(String id, String qid) {
        try {
            graphDetailOfPerson.graphPersonWithNotification(id, qid);
        } catch (Exception e) {
            LOG.error("Failed graphing person {} {} {}", qid, id, e.getLocalizedMessage());
        }
        return notificationMessageManager.increaseViewBusinessCount(id);
    }
}
