package com.noqapp.service;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.neo4j.NotificationN4j;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.NotificationMessageManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.repository.neo4j.NotificationN4jManager;
import com.noqapp.service.exceptions.DuplicateMessageException;
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
import java.util.List;

/**
 * hitender
 * 6/4/20 6:57 PM
 */
@Service
public class MessageCustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageCustomerService.class);

    private NotificationMessageManager notificationMessageManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private BizStoreManager bizStoreManager;
    private BizNameManager bizNameManager;
    private QueueManagerJDBC queueManagerJDBC;
    private TokenQueueManager tokenQueueManager;
    private UserProfileManager userProfileManager;

    private FirebaseService firebaseService;
    private FirebaseMessageService firebaseMessageService;
    private LanguageTranslationService languageTranslationService;

    private GraphDetailOfPerson graphDetailOfPerson;
    private NotificationN4jManager notificationN4jManager;

    private int limitMessageToCustomerVisitedInDays;

    @Autowired
    public MessageCustomerService(
        @Value("${MessageCustomerService.limitMessageToCustomerVisitedInDays}")
        int limitMessageToCustomerVisitedInDays,

        NotificationMessageManager notificationMessageManager,
        RegisteredDeviceManager registeredDeviceManager,
        BizStoreManager bizStoreManager,
        BizNameManager bizNameManager,
        QueueManagerJDBC queueManagerJDBC,
        TokenQueueManager tokenQueueManager,
        UserProfileManager userProfileManager,

        FirebaseService firebaseService,
        FirebaseMessageService firebaseMessageService,
        LanguageTranslationService languageTranslationService,

        GraphDetailOfPerson graphDetailOfPerson,
        NotificationN4jManager notificationN4jManager
    ) {
        this.limitMessageToCustomerVisitedInDays = limitMessageToCustomerVisitedInDays;

        this.notificationMessageManager = notificationMessageManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.bizStoreManager = bizStoreManager;
        this.bizNameManager = bizNameManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.tokenQueueManager = tokenQueueManager;
        this.userProfileManager = userProfileManager;

        this.firebaseService = firebaseService;
        this.firebaseMessageService = firebaseMessageService;
        this.languageTranslationService = languageTranslationService;

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
                BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
                TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
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
        sendAlertMessageToAllOnSpecificTopic(id, title, body, tokenQueue, QueueStatusEnum.C);
    }

    public int sendMessageToPastClients(String bizNameId) {
        return queueManagerJDBC.countDistinctQIDsInBiz(bizNameId, limitMessageToCustomerVisitedInDays);
    }

    /** Can be used for dynamic setting number of days. */
    @SuppressWarnings("unused")
    public int sendMessageToPastClients(String bizNameId, int days) {
        return queueManagerJDBC.countDistinctQIDsInBiz(bizNameId, days == 0 ? limitMessageToCustomerVisitedInDays : days);
    }

    public void sendMessageToAll(String title, String body, String qid, String subscribedTopic) {
        sendMessageToAll(title, body, null, qid, subscribedTopic, BusinessTypeEnum.ZZ);
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
                    sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            } else {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(subscribedTopic, deviceType.name());
                    sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, imageURL, topic, MessageOriginEnum.A, deviceType, businessType);
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
                    sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            } else {
                for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
                    String topic = CommonUtil.buildTopic(businessType.getName(), deviceType.name());
                    sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, imageURL, topic, MessageOriginEnum.A, deviceType, businessType);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending message to all {} {} {} reason={}", title, body, qid, e.getMessage(), e);
        }
    }

    public int sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        try {
            BizNameEntity bizName = bizNameManager.getById(bizNameId);
            String topicForLogging = CommonUtil.buildTopic(bizName.getCountryShortName() + UNDER_SCORE + bizNameId, DeviceTypeEnum.onlyForLogging());
            if (notificationMessageManager.findPreviouslySentMessages(title, body, topicForLogging, qid)) {
                LOG.info("Sending duplicate message ignored {} {} {} {}", qid, bizNameId, title, body);
                throw new DuplicateMessageException("Previously Sent Message");
            }

            NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
                .setTitle(title)
                .setBody(body)
                .setTopic(topicForLogging)
                .setQueueUserId(qid);
            save(notificationMessage);

            int sendMessageCount = sendMessageToPastClients(bizNameId);
            LOG.info("Sending message by {} total send={} \"{}\" \"{}\" {}", qid, sendMessageCount, title, body, bizNameId);

            List<String> tokens_A = new ArrayList<>();
            List<String> tokens_I = new ArrayList<>();
            queueManagerJDBC.distinctQIDsInBiz(bizNameId, limitMessageToCustomerVisitedInDays).stream().iterator()
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
                        if (firebaseService.subscribeTokenToTopic(tokens_A, topic)) {
                            sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, bizName.getBusinessType());
                        }
                        break;
                    case I:
                        if (firebaseService.subscribeTokenToTopic(tokens_I, topic)) {
                            sendBulkMessageToBusinessUser(notificationMessage.getId(), title, body, topic, MessageOriginEnum.A, deviceType, bizName.getBusinessType());
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
        } catch (DuplicateMessageException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Failed sending message {} {} reason={}", bizNameId, qid, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * This is suppose to be a backup (currently this is only what works) to unsubscribe aborted queue when store closes after people have
     * joined the queue, as aborted queue could rise from closing the store. Mobile should support this unsubscription as message is sent
     * to mobile.
     */
    public void unsubscribeWhenUserInQueueHaveStatusAborted(List<QueueEntity> queues, BizStoreEntity bizStore) {
        if (queues.isEmpty()) {
            return;
        }

        List<String> tokens_A = new ArrayList<>();
        List<String> tokens_I = new ArrayList<>();
        queues.forEach(queue -> {
            switch (queue.getQueueUserState()) {
                case A:
                    RegisteredDeviceEntity registeredDevice = registeredDeviceManager.find(queue.getQueueUserId(), queue.getDid());
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
                        LOG.error("Failed unsubscribe token {} {} {} {} {}", queue.getDisplayName(), queue.getBizNameId(), queue.getCodeQR(), queue.getQueueUserId(), e.getMessage());
                    }
                    break;
                default:
                    //Ignore other conditions
            }
        });

        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(bizStore.getCodeQR());
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            String topic = tokenQueue.getCorrectTopic(QueueStatusEnum.C) + UNDER_SCORE + deviceType.name();
            switch (deviceType) {
                case A:
                    firebaseService.unsubscribeTokensFromTopic(tokens_A, topic);
                    break;
                case I:
                    firebaseService.unsubscribeTokensFromTopic(tokens_I, topic);
                    break;
                case W:
                    //Do nothing
                    break;
            }
        }
    }

    @Mobile
    @Async
    public void increaseViewClientCount(String id, String qid) {
        graphDetailOfPerson.graphPersonWithNotification(id, qid);
        notificationMessageManager.increaseViewClientCount(id);
    }

    @Mobile
    @Async
    public void increaseViewUnregisteredCount(String id) {
        notificationMessageManager.increaseViewUnregisteredCount(id);
    }

    @Mobile
    @Async
    public void increaseViewBusinessCount(String id, String qid) {
        graphDetailOfPerson.graphPersonWithNotification(id, qid);
        notificationMessageManager.increaseViewBusinessCount(id);
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String qid, MessageOriginEnum messageOrigin, BusinessTypeEnum businessType) {
        LOG.debug("Sending message to specific user title=\"{}\" body=\"{}\" qid={} messageOrigin={}", title, body, qid, messageOrigin);
        RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(qid);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, null, registeredDevice, messageOrigin, businessType);
        } else {
            LOG.warn("Skipped as no registered device found for qid={}", qid);
        }
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String imageURL, String qid, MessageOriginEnum messageOrigin, BusinessTypeEnum businessType) {
        LOG.debug("Sending message to specific user title=\"{}\" body=\"{}\" qid={} messageOrigin={}", title, body, qid, messageOrigin);
        RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(qid);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, imageURL, registeredDevice, messageOrigin, businessType);
        } else {
            LOG.warn("Skipped as no registered device found for qid={}", qid);
        }
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String imageURL, RegisteredDeviceEntity registeredDevice, MessageOriginEnum messageOrigin, BusinessTypeEnum businessType) {
        LOG.debug("Sending message to specific user title=\"{}\" body=\"{}\" messageOrigin={}", title, body, messageOrigin);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, imageURL, registeredDevice, messageOrigin, businessType);
        }
    }

    public void createMessageToSendToSpecificUserOrDevice(String title, String body, String imageURL, RegisteredDeviceEntity registeredDevice, MessageOriginEnum messageOrigin, BusinessTypeEnum businessType) {
        String token = registeredDevice.getToken();
        JsonMessage jsonMessage = new JsonMessage(token);
        JsonData jsonData = new JsonTopicData(messageOrigin, FirebaseMessageTypeEnum.P).getJsonAlertData().setBusinessType(businessType);
        jsonData.setImageURL(imageURL);

        if (StringUtils.isNotBlank(registeredDevice.getQueueUserId())) {
            UserProfileEntity userProfile = userProfileManager.populateName(registeredDevice.getQueueUserId());
            body = userProfile.getName() + ", " + body;
            LOG.info("Message personalized {} \"{}\"", registeredDevice.getQueueUserId(), body);
        }

        if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
            jsonMessage.getNotification()
                .setTitle(title)
                .setBody(body);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setTitle(title)
                .setBody(body)
                .setTranslatedBody(languageTranslationService.translateText(registeredDevice.getDeviceLanguage(), body));
        }

        jsonMessage.setData(jsonData);
        LOG.info("Specific Message={}", jsonMessage.asJson());
        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Failed personal message=\"{}\"", jsonMessage.asJson());
        } else {
            LOG.info("Sent personal message=\"{}\"", jsonMessage.asJson());
        }
    }

    public void sendBulkMessageToBusinessUser(String id, String title, String body, String topic, MessageOriginEnum messageOrigin, DeviceTypeEnum deviceType, BusinessTypeEnum businessType) {
        JsonMessage jsonMessage = new JsonMessage(topic);
        JsonData jsonData = new JsonTopicData(messageOrigin, FirebaseMessageTypeEnum.P).getJsonAlertData().setBusinessType(businessType);
        jsonData.setId(id);

        if (DeviceTypeEnum.I == deviceType) {
            jsonMessage.getNotification()
                .setTitle(title)
                .setBody(body);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setTitle(title)
                .setBody(body)
                .setTranslatedBody(languageTranslationService.translateText(body));
        }

        jsonMessage.setData(jsonData);
        LOG.info("Specific Message=\"{}\"", jsonMessage.asJson());
        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Failed bulk message=\"{}\"", jsonMessage.asJson());
        } else {
            LOG.info("Sent bulk message=\"{}\"", jsonMessage.asJson());
        }
    }

    public void sendBulkMessageToBusinessUser(String id, String title, String body, String imageURL, String topic, MessageOriginEnum messageOrigin, DeviceTypeEnum deviceType, BusinessTypeEnum businessType) {
        JsonMessage jsonMessage = new JsonMessage(topic);
        JsonData jsonData = new JsonTopicData(messageOrigin, FirebaseMessageTypeEnum.P).getJsonAlertData().setBusinessType(businessType);
        jsonData
            .setId(id)
            .setImageURL(imageURL);

        if (DeviceTypeEnum.I == deviceType) {
            jsonMessage.getNotification()
                .setTitle(title)
                .setBody(body);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setTitle(title)
                .setBody(body)
                .setTranslatedBody(languageTranslationService.translateText(body));
        }

        jsonMessage.setData(jsonData);
        LOG.info("Specific Message={}", jsonMessage.asJson());
        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Failed bulk message={}", jsonMessage.asJson());
        } else {
            LOG.info("Sent bulk message={}", jsonMessage.asJson());
        }
    }

    /** Sends any message to all users subscribed to topic. This includes Client and Merchant. */
    @Mobile
    public void sendAlertMessageToAllOnSpecificTopic(String id, String title, String body, TokenQueueEntity tokenQueue, QueueStatusEnum queueStatus) {
        LOG.debug("Sending message to all title={} body={}", title, body);
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            String topic = tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name();
            LOG.debug("Topic being sent to {}", topic);
            JsonMessage jsonMessage = new JsonMessage(topic);
            JsonData jsonData = new JsonTopicData(MessageOriginEnum.A, FirebaseMessageTypeEnum.P).getJsonAlertData().setBusinessType(tokenQueue.getBusinessType())
                //Added additional info to message for Android to not crash as it looks for CodeQR.
                //TODO improve messaging to do some action on Client and Business app when status is Closed.
                .setCodeQR(tokenQueue.getId());
            jsonData
                .setId(id);

            if (DeviceTypeEnum.I == deviceType) {
                jsonMessage.getNotification()
                    .setTitle(title)
                    .setBody(body);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setTitle(title)
                    .setBody(body)
                    .setTranslatedBody(languageTranslationService.translateText(body));
            }

            jsonMessage.setData(jsonData);
            LOG.info("Broadcast Message={}", jsonMessage.asJson());
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.info("Sent message to all subscriber of topic message={}", jsonMessage.asJson());
            }
        }
    }
}
