package com.noqapp.service;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonResponse;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.noqapp.domain.BizStoreEntity.*;
import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * User: hitender
 * Date: 12/16/16 9:42 AM
 */
@Service
public class TokenQueueService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueService.class);

    private TokenQueueManager tokenQueueManager;
    private FirebaseMessageService firebaseMessageService;
    private QueueManager queueManager;
    private AccountService accountService;
    private RegisteredDeviceManager registeredDeviceManager;
    private QueueManagerJDBC queueManagerJDBC;
    private StoreHourManager storeHourManager;
    private BizStoreManager bizStoreManager;
    private ApiHealthService apiHealthService;

    private ExecutorService executorService;

    @Autowired
    public TokenQueueService(
            TokenQueueManager tokenQueueManager,
            FirebaseMessageService firebaseMessageService,
            QueueManager queueManager,
            AccountService accountService,
            RegisteredDeviceManager registeredDeviceManager,
            QueueManagerJDBC queueManagerJDBC,
            StoreHourManager storeHourManager,
            BizStoreManager bizStoreManager,
            ApiHealthService apiHealthService
    ) {
        this.tokenQueueManager = tokenQueueManager;
        this.firebaseMessageService = firebaseMessageService;
        this.queueManager = queueManager;
        this.accountService = accountService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.storeHourManager = storeHourManager;
        this.bizStoreManager = bizStoreManager;
        this.apiHealthService = apiHealthService;

        this.executorService = newCachedThreadPool();
    }

    //TODO has to createUpdate by cron job
    public void createUpdate(String codeQR, String topic, String displayName, BusinessTypeEnum businessType) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        try {
            LOG.info("Create/Update id={} {} displayName={} businessType={}", codeQR, topic, displayName, businessType);
            Assertions.assertTrue(topic.endsWith(codeQR), "Topic and CodeQR should match significantly");
            TokenQueueEntity token = findByCodeQR(codeQR);
            if (null == token) {
                token = new TokenQueueEntity(topic, displayName, businessType);
                token.setId(codeQR);
                tokenQueueManager.save(token);
            } else {
                boolean updateSuccess = tokenQueueManager.updateDisplayNameAndBusinessType(
                        codeQR,
                        topic,
                        displayName,
                        businessType);

                if (!updateSuccess) {
                    LOG.error("Failed update for codeQR={} topic={} displayName={}", codeQR, topic, displayName);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed creating TokenQueue codeQR={} topic={} displayName={}", codeQR, topic, displayName);
            methodStatusSuccess = false;
            throw new RuntimeException("Failed creating TokenQueue");
        } finally {
            apiHealthService.insert(
                    "createUpdate",
                    "createUpdate",
                    TokenQueueService.class.getName(),
                    Duration.between(start, Instant.now()),
                    methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }

    public TokenQueueEntity findByCodeQR(String codeQR) {
        return tokenQueueManager.findByCodeQR(codeQR);
    }

    public void deleteHard(TokenQueueEntity tokenQueue) {
        tokenQueueManager.deleteHard(tokenQueue);
    }

    /**
     * Gets new token or reloads existing token if previously registered.
     * This process adds the user to queue. Invokes broadcast.
     */
    @Mobile
    public JsonToken getNextToken(
            String codeQR,
            String did,
            String qid,
            long averageServiceTime,
            TokenServiceEnum tokenService
    ) {
        try {
            QueueEntity queue = queueManager.findQueuedOne(codeQR, did, qid);

            /* When not Queued or has been serviced which will not show anyway in the above query, get a new token. */
            if (null == queue) {
                /*
                 * Find storeHour early, helps prevent issuing token when queue is closed or due to some obstruction.
                 * To eliminate this, we need to let merchant know about queue closed and prevent clients from joining.
                 */
                BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
                ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
                DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
                StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);

                if (storeHour.isDayClosed() || storeHour.isPreventJoining()) {
                    LOG.warn("When queue closed or prevent joining, attempting to create new token");
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                            .setToken(0)
                            .setServingNumber(0)
                            .setDisplayName(bizStore.getDisplayName())
                            .setQueueStatus(QueueStatusEnum.C)
                            .setExpectedServiceBegin(new Date());
                }

                Assertions.assertNotNull(tokenService, "TokenService cannot be null to generate new token");
                TokenQueueEntity tokenQueue = tokenQueueManager.getNextToken(codeQR);
                LOG.info("Assigned to queue with codeQR={} with new token={}", codeQR, tokenQueue.getLastNumber());

                doActionBasedOnQueueStatus(codeQR, tokenQueue);

                try {
                    queue = new QueueEntity(codeQR, did, tokenService, qid, tokenQueue.getLastNumber(), tokenQueue.getDisplayName(), tokenQueue.getBusinessType());
                    if (0 != averageServiceTime) {
                        LocalTime now = LocalTime.now(zoneId);
                        LOG.info("Time now={}", now);
                        LocalTime start = LocalTime.parse(String.format(Locale.US, "%04d", storeHour.getStartHour()), Formatter.inputFormatter);
                        LOG.info("Time start={} format={}", start, String.format(Locale.US, "%04d", storeHour.getStartHour()));

                        Duration duration = Duration.between(now, start.atOffset(zoneId.getRules().getOffset(Instant.now())));
                        LOG.info("duration in minutes={}", duration.toMinutes());
                        long serviceInMinutes = averageServiceTime / 60_000 * (tokenQueue.getLastNumber() - tokenQueue.getCurrentlyServing());
                        LOG.info("Service in minutes={} averageServiceTime={}", serviceInMinutes, averageServiceTime);

                        if (duration.isNegative()) {
                            queue.setExpectedServiceBegin(DateUtil.convertToDateTime(
                                    LocalDateTime.now()
                                            .plusMinutes(serviceInMinutes)
                                            .plusMinutes(storeHour.getDelayedInMinutes())));
                        } else {
                            LOG.info("Now {}", LocalDateTime.now());
                            LOG.info("Plus serviceInMinutes {}", LocalDateTime.now().plusMinutes(serviceInMinutes));
                            LOG.info("Plus duration {}", LocalDateTime.now().plusMinutes(serviceInMinutes).plusMinutes(duration.toMinutes()));
                            LOG.info("Plus getDelayedInMinutes {}", LocalDateTime.now().plusMinutes(serviceInMinutes).plusMinutes(duration.toMinutes()).plusMinutes(storeHour.getDelayedInMinutes()));
                            LOG.info("convertToDateTime {}", DateUtil.convertToDateTime(
                                    LocalDateTime.now()
                                            .plusMinutes(serviceInMinutes)
                                            .plusMinutes(duration.toMinutes())
                                            .plusMinutes(storeHour.getDelayedInMinutes())));

                            queue.setExpectedServiceBegin(DateUtil.convertToDateTime(
                                    LocalDateTime.now()
                                            .plusMinutes(serviceInMinutes)
                                            .plusMinutes(duration.toMinutes())
                                            .plusMinutes(storeHour.getDelayedInMinutes())));
                        }
                    }
                    queueManager.insert(queue);
                    updateQueueWithUserDetail(codeQR, qid, queue);
                } catch (DuplicateKeyException e) {
                    LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                    return new JsonToken(codeQR, tokenQueue.getBusinessType());
                }

                return new JsonToken(codeQR, tokenQueue.getBusinessType())
                        .setToken(queue.getTokenNumber())
                        .setServingNumber(tokenQueue.getCurrentlyServing())
                        .setDisplayName(tokenQueue.getDisplayName())
                        .setQueueStatus(tokenQueue.getQueueStatus())
                        .setExpectedServiceBegin(queue.getExpectedServiceBegin());
            }

            TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} qid={} did={} queueStatus={}",
                    queue.getTokenNumber(), tokenQueue.getTopic(), qid, did, tokenQueue.getQueueStatus());

            doActionBasedOnQueueStatus(codeQR, tokenQueue);

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setExpectedServiceBegin(queue.getExpectedServiceBegin());
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    private void doActionBasedOnQueueStatus(String codeQR, TokenQueueEntity tokenQueue) {
        switch (tokenQueue.getQueueStatus()) {
            case D:
                sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue, null);
                tokenQueueManager.changeQueueStatus(codeQR, QueueStatusEnum.R);
                break;
            case S:
                sendMessageToTopic(codeQR, QueueStatusEnum.S, tokenQueue, null);
                break;
            case R:
                sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue, null);
                break;
            default:
                sendMessageToTopic(codeQR, QueueStatusEnum.N, tokenQueue, null);
                break;
        }
    }

    @Async
    public void updateQueueWithUserDetail(String codeQR, String qid, QueueEntity queue) {
        Assertions.assertNotNull(queue.getId(), "Queue should have been persisted before executing the code");
        if (StringUtils.isNotBlank(qid)) {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
            queue.setCustomerName(userProfile.getName());
            queue.setCustomerPhone(userProfile.getPhone());
            queue.setClientVisitedThisStore(queueManagerJDBC.hasClientVisitedThisStore(codeQR, qid));
            if (null != userProfile.getGuardianToQueueUserId() && !userProfile.getGuardianToQueueUserId().isEmpty()) {
                queue.setGuardianToQueueUserId(userProfile.getGuardianToQueueUserId());
            }
            queueManager.save(queue);
        }
    }

    @Mobile
    public JsonResponse abortQueue(String codeQR, String did, String qid) {
        try {
            QueueEntity queue = queueManager.findToAbort(codeQR, did, qid);
            LOG.info("abort queue={}", queue);

            if (queue == null) {
                LOG.warn("Not joined to queue did={}, ignore abort", did);
                return new JsonResponse(false);
            }

            LOG.info("Found queue id={}", queue.getId());
            queueManager.abort(queue.getId());
            return new JsonResponse(true);
        } catch (Exception e) {
            LOG.error("Abort failed {}", e.getLocalizedMessage(), e);
            return new JsonResponse(false);
        }
    }

    @Mobile
    public JsonToken updateServing(String codeQR, QueueStatusEnum queueStatus, int serving, String goTo) {
        TokenQueueEntity tokenQueue = tokenQueueManager.updateServing(codeQR, serving, queueStatus);
        sendMessageToTopic(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo);

        LOG.info("After sending message to merchant");
        QueueEntity queue = findOne(codeQR, tokenQueue.getCurrentlyServing());
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", queue.getQueueUserId(), queue.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setToken(tokenQueue.getLastNumber())
                    .setCustomerName(queue.getCustomerName())
                    .setClientVisitedThisStore(queue.hasClientVisitedThisStore());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber());
    }

    /**
     * This acquires the record of the person being served by server. No one gets informed when the record is
     * acquired other than the person who's record is acquired to be served next.
     */
    @Mobile
    public JsonToken updateThisServing(String codeQR, QueueStatusEnum queueStatus, int serving, String goTo) {
        TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
        sendMessageToTopic(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo);
        /*
         * Do not inform anyone other than the person with the
         * token who is being served. This is personal message.
         * of being served out of order/sequence.
         */
        sendMessageToSelectedTokenUser(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo, serving);

        LOG.info("After sending message to merchant and personal message to user of token");
        QueueEntity queue = findOne(codeQR, serving);
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", queue.getQueueUserId(), queue.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setServingNumber(serving)
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setToken(tokenQueue.getLastNumber())
                    .setCustomerName(queue.getCustomerName())
                    .setClientVisitedThisStore(queue.hasClientVisitedThisStore());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(serving)
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber());
    }

    /**
     * Send FCM message to Topic asynchronously.
     */
    private void sendMessageToTopic(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo) {
        executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, queueStatus, tokenQueue, goTo));
    }

    /**
     * Send FCM message to person with specific token number asynchronously.
     */
    private void sendMessageToSelectedTokenUser(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo, int tokenNumber) {
        executorService.submit(() -> invokeThreadSendMessageToSelectedTokenUser(codeQR, queueStatus, tokenQueue, goTo, tokenNumber));
    }

    /**
     * Sends any message to a specific user.
     */
    public void sendMessageToSpecificUser(String title, String body, String qid) {
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(qid);
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            String token = registeredDevice.getToken();
            JsonMessage jsonMessage = new JsonMessage(token);
            JsonData jsonData = new JsonTopicData(FirebaseMessageTypeEnum.P);

            if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                jsonMessage.getNotification()
                        .setTitle(title)
                        .setBody(body);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setTitle(title)
                        .setBody(body);
            }

            jsonMessage.setData(jsonData);
            LOG.info("Specific Message={}", jsonMessage.asJson());
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.info("Sent supervisor invite message={}", jsonMessage.asJson());
            }
        }

        LOG.info("Sent FCM supervisor invite deviceCount={} qid={}", registeredDevices.size(), qid);
    }

    /**
     * Sends any message to all users subscribed to topic. This includes Client and Merchant.
     */
    @Mobile
    public void sendMessageToAllOnSpecificTopic(String title, String body, TokenQueueEntity tokenQueue, QueueStatusEnum queueStatus) {
        LOG.debug("Sending message to all title={} body={}", title, body);

        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Topic being sent to {}", tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(FirebaseMessageTypeEnum.P)
                    //Added additional info to message for Android to not crash as it looks for CodeQR.
                    //TODO improve messaging to do some action on Client and Merchant app when status is Closed.
                    .setLastNumber(tokenQueue.getLastNumber())
                    .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                    .setCodeQR(tokenQueue.getId())
                    .setQueueStatus(queueStatus)
                    .setGoTo("");

            if (DeviceTypeEnum.I == deviceType) {
                jsonMessage.getNotification()
                        .setTitle(title)
                        .setBody(body);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setTitle(title)
                        .setBody(body);
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

    /**
     * Formulates and send messages to FCM.
     */
    private void invokeThreadSendMessageToTopic(
            String codeQR,
            QueueStatusEnum queueStatus,
            TokenQueueEntity tokenQueue,
            String goTo
    ) {
        LOG.debug("Sending message codeQR={} goTo={}", codeQR, goTo);

        int timeout = 2;
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Topic being sent to {}", tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(tokenQueue.getFirebaseMessageType())
                    .setLastNumber(tokenQueue.getLastNumber())
                    .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                    .setCodeQR(codeQR)
                    .setQueueStatus(queueStatus)
                    .setGoTo(goTo);

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * Otherwise we will have to manage more number of topic.
             */
            switch (queueStatus) {
                case S:
                case R:
                case D:
                    //TODO remove me, added as messages go out fast, before records are propagated to other replica set
                    if (0 != timeout) {
                        try {
                            TimeUnit.SECONDS.sleep(timeout);
                            timeout = 0;
                        } catch (InterruptedException e) {
                            LOG.error("Failed adding delay reason={}", e.getLocalizedMessage());
                        }
                    }

                    /*
                     * This message has to go as the merchant with the opened queue
                     * will not get any update if some one joins. FCM makes sure the message is dispersed.
                     */
                    long confirmedWaiting = queueManager.countAllQueued(codeQR);
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                                .setBody("Now has " + tokenQueue.totalWaiting() + " waiting. Confirmed waiting " + confirmedWaiting)
                                .setTitle(tokenQueue.getDisplayName() + " Queue");
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now has " + tokenQueue.totalWaiting() + " waiting. Confirmed waiting " + confirmedWaiting)
                                .setTitle(tokenQueue.getDisplayName() + " Queue");
                    }
                    break;
                default:
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                                .setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                                .setLocKey("serving")
                                .setLocArgs(new String[]{String.valueOf(tokenQueue.getCurrentlyServing())})
                                .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                                .setTitle(tokenQueue.getDisplayName());
                    }
            }

            jsonMessage.setData(jsonData);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    /**
     * When servicing token that's out of order or sequence. Send message as the selected token is being served
     * and mark it Personal.
     */
    private void invokeThreadSendMessageToSelectedTokenUser(
            String codeQR,
            QueueStatusEnum queueStatus,
            TokenQueueEntity tokenQueue,
            String goTo,
            int tokenNumber
    ) {
        LOG.debug("Sending personal message codeQR={} goTo={} tokenNumber={}", codeQR, goTo, tokenNumber);

        QueueEntity queue = findOne(codeQR, tokenNumber);
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(queue.getQueueUserId(), queue.getDid());
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            LOG.debug("Personal message of being served is sent to qid={} deviceId={} deviceType={} with tokenNumber={}",
                    registeredDevice.getQueueUserId(),
                    registeredDevice.getDeviceId(),
                    registeredDevice.getDeviceType(),
                    tokenNumber);

            JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
            JsonData jsonData = new JsonTopicData(FirebaseMessageTypeEnum.P)
                    .setLastNumber(tokenQueue.getLastNumber())
                    .setCurrentlyServing(tokenNumber)
                    .setCodeQR(codeQR)
                    .setQueueStatus(queueStatus)
                    .setGoTo(goTo);

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * As this is a personal message when server is planning to serve a spacific token.
             */
            switch (queueStatus) {
                case S:
                case R:
                case D:
                    LOG.warn("Skipped sending personal message as queue status is not 'Next' but queueStatus={}", queueStatus);
                    break;
                case P:
                case C:
                    LOG.error("Cannot reach this state codeQR={}, queueStatus={}", codeQR, queueStatus);
                    return;
                case N:
                default:
                    LOG.debug("Personal device is of type={} did={} token={}",
                            registeredDevice.getDeviceType(),
                            registeredDevice.getDeviceId(),
                            registeredDevice.getToken());

                    if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                        jsonMessage.getNotification()
                                .setBody("Now Serving " + tokenNumber)
                                .setLocKey("serving")
                                .setLocArgs(new String[]{String.valueOf(tokenNumber)})
                                .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenNumber)
                                .setTitle(tokenQueue.getDisplayName());
                    }
            }

            jsonMessage.setData(jsonData);

            LOG.debug("Personal FCM message to be sent={}", jsonMessage);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Personal broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent Personal topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    List<TokenQueueEntity> getTokenQueue(String[] ids) {
        return tokenQueueManager.getTokenQueues(ids);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return queueManager.isQueued(tokenNumber, codeQR);
    }

    public QueueEntity findQueuedByPhone(String codeQR, String phone) {
        return queueManager.findQueuedByPhone(codeQR, phone);
    }

    public QueueEntity findOne(String codeQR, int tokenNumber) {
        return queueManager.findOne(codeQR, tokenNumber);
    }

    public void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus) {
        tokenQueueManager.changeQueueStatus(codeQR, queueStatus);
    }
}
