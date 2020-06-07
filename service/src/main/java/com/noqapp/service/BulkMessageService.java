package com.noqapp.service;

import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.repository.NotificationMessageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 6/4/20 6:57 PM
 */
@Service
public class BulkMessageService {
    private static final Logger LOG = LoggerFactory.getLogger(BulkMessageService.class);

    private QueueService queueService;
    private TokenQueueService tokenQueueService;
    private NotificationMessageManager notificationMessageManager;

    @Autowired
    public BulkMessageService(
        QueueService queueService,
        TokenQueueService tokenQueueService,
        NotificationMessageManager notificationMessageManager
    ) {
        this.queueService = queueService;
        this.tokenQueueService = tokenQueueService;
        this.notificationMessageManager = notificationMessageManager;
    }

    @Mobile
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

    public long sendMessageToPastClients(String bizNameId, int days) {
        return queueService.countDistinctQIDsInBiz(bizNameId, days);
    }

}
