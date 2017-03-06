package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.token.domain.QueueEntity;
import com.token.domain.TokenQueueEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.json.JsonResponse;
import com.token.domain.json.JsonToken;
import com.token.domain.json.fcm.JsonMessage;
import com.token.domain.types.QueueStatusEnum;
import com.token.domain.types.QueueUserStateEnum;
import com.token.repository.QueueManager;
import com.token.repository.TokenQueueManager;

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

    @Autowired
    public TokenQueueService(
            TokenQueueManager tokenQueueManager,
            FirebaseService firebaseService,
            QueueManager queueManager
    ) {
        this.tokenQueueManager = tokenQueueManager;
        this.firebaseService = firebaseService;
        this.queueManager = queueManager;
    }

    //TODO has to create by cron job
    public void create(String codeQR, String topic, String displayName) {
        TokenQueueEntity token = new TokenQueueEntity(topic, displayName);
        token.setId(codeQR);
        tokenQueueManager.save(token);
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
        QueueEntity queue = queueManager.findOne(codeQR, did, rid);

        /* Either not registered or registered but has been serviced so get new token. */
        if (null == queue || QueueUserStateEnum.Q != queue.getQueueUserState()) {
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
                queue = new QueueEntity(codeQR, did, rid, tokenQueue.getLastNumber());
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
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(tokenQueue.getQueueStatus());
    }

    @Mobile
    public JsonResponse abortQueue(String codeQR, String did, String rid) {
        try {
            QueueEntity queue = queueManager.findToAbort(codeQR, did, rid);
            if (queue == null) {
                LOG.warn("Not joined to queue did={}, ignore abort", did);
                return new JsonResponse(true);
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

        return new JsonToken(codeQR)
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.getCurrentlyServing())
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
        jsonMessage.getTopicData()
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
                jsonMessage.getNotification()
                        .setBody("Now has " + tokenQueue.totalWaiting() + " waiting")
                        .setTitle(tokenQueue.getDisplayName() + " Queue");
                break;
            default:
                jsonMessage.getNotification()
                        .setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                        .setLocKey("serving")
                        .setLocArgs(new String[]{String.valueOf(tokenQueue.getCurrentlyServing())})
                        .setTitle(tokenQueue.getDisplayName());
        }

        boolean fcmMessageBroadcast = firebaseService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
        } else {
            LOG.info("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
        }
    }

    public List<TokenQueueEntity> getTokenQueue(String[] ids) {
        return tokenQueueManager.getTokenQueues(ids);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return queueManager.isQueued(tokenNumber, codeQR);
    }
}
