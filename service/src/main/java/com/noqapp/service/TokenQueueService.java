package com.noqapp.service;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonResponse;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.TokenQueueManager;

import java.util.List;

/**
 * User: hitender
 * Date: 12/16/16 9:42 AM
 */
@Service
public class TokenQueueService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueService.class);

    private TokenQueueManager tokenQueueManager;
    private FirebaseService firebaseService;
    private QueueManager queueManager;
    private AccountService accountService;

    @Autowired
    public TokenQueueService(
            TokenQueueManager tokenQueueManager,
            FirebaseService firebaseService,
            QueueManager queueManager,
            AccountService accountService
    ) {
        this.tokenQueueManager = tokenQueueManager;
        this.firebaseService = firebaseService;
        this.queueManager = queueManager;
        this.accountService = accountService;
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
     * @param rid
     * @return
     */
    @Mobile
    public JsonToken getNextToken(String codeQR, String did, String rid) {
        try {
            QueueEntity queue = queueManager.findQueuedOne(codeQR, did, rid);
            LOG.info("next Token queue={}", queue);

            /* When not Queued or has been serviced which will not show anyway in the above query, get a new token. */
            if (null == queue) {
                TokenQueueEntity tokenQueue = tokenQueueManager.getNextToken(codeQR);

                switch (tokenQueue.getQueueStatus()) {
                    case D:
                        sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue);
                        tokenQueueManager.changeQueueStatus(codeQR, QueueStatusEnum.R);
                        break;
                    case S:
                        sendMessageToTopic(codeQR, QueueStatusEnum.S, tokenQueue);
                        break;
                    case R:
                        sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue);
                        break;
                    default:
                        sendMessageToTopic(codeQR, QueueStatusEnum.N, tokenQueue);
                        break;
                }

                try {
                    queue = new QueueEntity(codeQR, did, rid, tokenQueue.getLastNumber(), tokenQueue.getDisplayName());
                    if (StringUtils.isNotBlank(rid)) {
                        UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);
                        queue.setCustomerName(userAccount.getDisplayName());
                    }
                    queueManager.insert(queue);
                } catch (DuplicateKeyException e) {
                    LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                    return new JsonToken(codeQR);
                }

                return new JsonToken(codeQR)
                        .setToken(queue.getTokenNumber())
                        .setServingNumber(tokenQueue.computeCurrentlyServing())
                        .setDisplayName(tokenQueue.getDisplayName())
                        .setQueueStatus(tokenQueue.getQueueStatus());
            }

            TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} rid={} did={}", queue.getTokenNumber(), tokenQueue.getTopic(), rid, did);
            switch (tokenQueue.getQueueStatus()) {
                case D:
                    sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue);
                    tokenQueueManager.changeQueueStatus(codeQR, QueueStatusEnum.R);
                    break;
                case S:
                    sendMessageToTopic(codeQR, QueueStatusEnum.S, tokenQueue);
                    break;
                case R:
                    sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue);
                    break;
                default:
                    sendMessageToTopic(codeQR, QueueStatusEnum.N, tokenQueue);
                    break;
            }

            return new JsonToken(codeQR)
                    .setToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.computeCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus());
        } catch(Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    @Mobile
    public JsonResponse abortQueue(String codeQR, String did, String rid) {
        try {
            QueueEntity queue = queueManager.findToAbort(codeQR, did, rid);
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
    public JsonToken updateServing(String codeQR, QueueStatusEnum queueStatus, int serving) {
        TokenQueueEntity tokenQueue = tokenQueueManager.updateServing(codeQR, serving, queueStatus);
        sendMessageToTopic(codeQR, tokenQueue.getQueueStatus(), tokenQueue);

        LOG.info("After sending message to merchant");
        QueueEntity queue = queueManager.findOne(codeQR, serving);
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue user={} did={}", queue.getRid(), queue.getDid());

            return new JsonToken(codeQR)
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setServingNumber(tokenQueue.computeCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setToken(tokenQueue.getLastNumber())
                    .setCustomerName(queue.getCustomerName());
        }

        return new JsonToken(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.computeCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber());
    }

    /**
     * Send message to Topic.
     *
     * @param codeQR
     * @param tokenQueue
     */
    private void sendMessageToTopic(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue) {
        JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus));
        JsonData jsonData = new JsonTopicData(tokenQueue.getFirebaseMessageType())
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                .setCodeQR(codeQR)
                .setQueueStatus(queueStatus);

        /*
        Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
        Otherwise we will have to manage more number of topic.
        */
        switch(queueStatus) {
            case S:
            case R:
            case D:
                /**
                 * This message has to go as the merchant with the opened queue
                 * will not get any update if some one joins. FCM makes sure the message is dispersed.  
                 */
                jsonData.setBody("Now has " + tokenQueue.totalWaiting() + " waiting")
                        .setTitle(tokenQueue.getDisplayName() + " Queue");
                break;
            default:
                jsonData.setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                        .setTitle(tokenQueue.getDisplayName());
        }

        jsonMessage.setData(jsonData);
        boolean fcmMessageBroadcast = firebaseService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
        } else {
            LOG.info("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
        }
    }

    List<TokenQueueEntity> getTokenQueue(String[] ids) {
        return tokenQueueManager.getTokenQueues(ids);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return queueManager.isQueued(tokenNumber, codeQR);
    }
}
