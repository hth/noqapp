package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.QueueEntity;
import com.token.domain.TokenQueueEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.json.JsonTokenQueue;
import com.token.repository.QueueManager;
import com.token.repository.TokenQueueManager;

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
    public void create(String codeQR, String topic) {
        TokenQueueEntity token = new TokenQueueEntity(topic);
        token.setId(codeQR);
        tokenQueueManager.save(token);
    }

    @Mobile
    public TokenQueueEntity findByCodeQR(String codeQR) {
        return tokenQueueManager.findByCodeQR(codeQR);
    }

    @Mobile
    public JsonTokenQueue getNextToken(String codeQR, String did, String rid, String deviceToken) {
        TokenQueueEntity tokenQueue = tokenQueueManager.getNextToken(codeQR);
        boolean topicRegistration = firebaseService.subscribeTopic(tokenQueue.getTopic(), did, deviceToken);

        if (topicRegistration) {
            QueueEntity queue = new QueueEntity(codeQR, did, rid, tokenQueue.getLastNumber());
            queueManager.save(queue);
        } else {
            LOG.error("Failed subscription did={} codeQR={}", did, codeQR);
        }

        return new JsonTokenQueue(codeQR)
                .setToken(tokenQueue.getLastNumber())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setTopicRegistration(topicRegistration);
    }
}
