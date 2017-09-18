package com.noqapp.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonResponse;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.TokenQueueManager;

import java.util.List;
import java.util.concurrent.ExecutorService;

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

    private ExecutorService service;

    @Autowired
    public TokenQueueService(
            TokenQueueManager tokenQueueManager,
            FirebaseMessageService firebaseMessageService,
            QueueManager queueManager,
            AccountService accountService,
            RegisteredDeviceManager registeredDeviceManager
    ) {
        this.tokenQueueManager = tokenQueueManager;
        this.firebaseMessageService = firebaseMessageService;
        this.queueManager = queueManager;
        this.accountService = accountService;
        this.registeredDeviceManager = registeredDeviceManager;

        this.service = newCachedThreadPool();
    }

    //TODO has to create by cron job
    public void create(String codeQR, String topic, String displayName) {
        try {
            TokenQueueEntity token = new TokenQueueEntity(topic, displayName);
            token.setId(codeQR);
            tokenQueueManager.save(token);
        } catch (Exception e) {
            LOG.error("Failed creating TokenQueue codeQR={} topic={} displayName={}", codeQR, topic, displayName);
            throw new RuntimeException("Failed creating TokenQueue");
        }
    }

    @Mobile
    public TokenQueueEntity findByCodeQR(String codeQR) {
        return tokenQueueManager.findByCodeQR(codeQR);
    }

    /**
     * Get new token. This process adds the user to queue. Invokes broadcast.
     *
     * @param codeQR
     * @param did
     * @param qid
     * @return
     */
    @Mobile
    public JsonToken getNextToken(String codeQR, String did, String qid) {
        try {
            QueueEntity queue = queueManager.findQueuedOne(codeQR, did, qid);
            LOG.info("next Token queue={}", queue);

            /* When not Queued or has been serviced which will not show anyway in the above querry, get a new token. */
            if (null == queue) {
                TokenQueueEntity tokenQueue = tokenQueueManager.getNextToken(codeQR);

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

                try {
                    queue = new QueueEntity(codeQR, did, qid, tokenQueue.getLastNumber(), tokenQueue.getDisplayName());
                    if (StringUtils.isNotBlank(qid)) {
                        UserAccountEntity userAccount = accountService.findByQueueUserId(qid);
                        queue.setCustomerName(userAccount.getDisplayName());
                    }
                    queueManager.insert(queue);
                } catch (DuplicateKeyException e) {
                    LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                    return new JsonToken(codeQR);
                }

                return new JsonToken(codeQR)
                        .setToken(queue.getTokenNumber())
                        .setServingNumber(tokenQueue.getCurrentlyServing())
                        .setDisplayName(tokenQueue.getDisplayName())
                        .setQueueStatus(tokenQueue.getQueueStatus());
            }

            TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} qid={} did={} queueStatus={}",
                    queue.getTokenNumber(), tokenQueue.getTopic(), qid, did, tokenQueue.getQueueStatus());

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

            return new JsonToken(codeQR)
                    .setToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus());
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
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
        QueueEntity queue = queueManager.findOne(codeQR, tokenQueue.getCurrentlyServing());
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", queue.getQueueUserId(), queue.getDid());

            return new JsonToken(codeQR)
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setToken(tokenQueue.getLastNumber())
                    .setCustomerName(queue.getCustomerName());
        }

        return new JsonToken(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber());
    }

    /**
     * Send FCM message to Topic asynchronously.
     *
     * @param codeQR
     * @param queueStatus
     * @param tokenQueue
     * @param goTo
     */
    private void sendMessageToTopic(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo) {
        service.submit(() -> invokeThreadSendMessageToTopic(codeQR, queueStatus, tokenQueue, goTo));
    }

    /**
     * Invite sent when business adds client as supervisor to a queue.
     *
     * @param qid
     * @param displayName
     * @param businessName
     */
    public void sendInviteToNewQueueSupervisor(String qid, String displayName, String businessName) {
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(qid);
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            String token = registeredDevice.getToken();
            JsonMessage jsonMessage = new JsonMessage(token);
            JsonData jsonData = new JsonTopicData(FirebaseMessageTypeEnum.P);

            if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                jsonMessage.getNotification()
                        .setBody(businessName + " has sent an invite")
                        .setTitle("Invitation for Queue " + displayName);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setBody(businessName + " has sent an invite")
                        .setTitle("Invitation for Queue " + displayName);
            }

            jsonMessage.setData(jsonData);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.info("Sent message={}", jsonMessage.asJson());
            }
        }

        LOG.info("Sent FCM invite count={} qid={} displayName={} businessName={}",
                registeredDevices.size(), qid, displayName, businessName);
    }

    /**
     * Formulates and send messages to FCM.
     *
     * @param codeQR
     * @param queueStatus
     * @param tokenQueue
     * @param goTo
     */
    private void invokeThreadSendMessageToTopic(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo) {
        LOG.info("sending message codeQR={} goTo={}", codeQR, goTo);

        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.info("Topic being sent to {}", tokenQueue.getCorrectTopic(queueStatus) + "_" + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus) + "_" + deviceType.name());
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
                    /*
                     * This message has to go as the merchant with the opened queue
                     * will not get any update if some one joins. FCM makes sure the message is dispersed.
                     */
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                                .setBody("Now has " + tokenQueue.totalWaiting() + " waiting")
                                .setTitle(tokenQueue.getDisplayName() + " Queue");
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now has " + tokenQueue.totalWaiting() + " waiting")
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
                LOG.info("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    List<TokenQueueEntity> getTokenQueue(String[] ids) {
        return tokenQueueManager.getTokenQueues(ids);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return queueManager.isQueued(tokenNumber, codeQR);
    }
}
