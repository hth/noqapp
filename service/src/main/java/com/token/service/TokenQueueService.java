package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.token.domain.QueueEntity;
import com.token.domain.TokenQueueEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.json.JsonToken;
import com.token.domain.json.fcm.JsonMessage;
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
     * Get new token. This process adds the user to queue. Invoke broadcast.
     *
     * @param codeQR
     * @param did
     * @param rid
     * @param deviceToken
     * @return
     */
    @Mobile
    public JsonToken getNextToken(String codeQR, String did, String rid, String deviceToken) {
        QueueEntity queue = queueManager.findOne(codeQR, did, rid);
        if (queue == null) {
            TokenQueueEntity tokenQueue = tokenQueueManager.getNextToken(codeQR);
            sendMessageToTopic(codeQR, tokenQueue);

            try {
                queue = new QueueEntity(codeQR, did, rid, tokenQueue.getLastNumber());
                queueManager.save(queue);
            } catch (DuplicateKeyException e) {
                LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                return new JsonToken(codeQR);
            }

            return new JsonToken(codeQR)
                    .setTotalToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setActive(queue.isActive());
        } else {
            TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} rid={} did={}", queue.getTokenNumber(), tokenQueue.getTopic(), rid, did);
            sendMessageToTopic(codeQR, tokenQueue);

            return new JsonToken(codeQR)
                    .setTotalToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setActive(queue.isActive());
        }
    }

    @Mobile
    public JsonToken updateServing(String codeQR, int serving) {
        TokenQueueEntity tokenQueue = tokenQueueManager.updateServing(codeQR, serving);
        sendMessageToTopic(codeQR, tokenQueue);

        return new JsonToken(codeQR)
                .setActive(tokenQueue.isActive())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setTotalToken(tokenQueue.getLastNumber());
    }

    /**
     * Send message to Topic.
     * @param codeQR
     * @param tokenQueue
     */
    private void sendMessageToTopic(String codeQR, TokenQueueEntity tokenQueue) {
        JsonMessage jsonMessage = new JsonMessage(tokenQueue.getTopicWellFormatted());
        jsonMessage.getTopicData()
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                .setCodeQR(codeQR);

        jsonMessage.getNotification()
                .setBody("Hello Body")
                .setTitle("Hello");

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
}
