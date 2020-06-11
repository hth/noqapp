package com.noqapp.service;

import com.noqapp.domain.NotificationMessageEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.MessageOriginEnum;
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
        return queueService.countDistinctQIDsInBiz(bizNameId, days == 0 ? 45 : days);
    }

    public void sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        List<String> qids = queueService.distinctQIDsInBiz(bizNameId, 45);
        LOG.info("Sending message by {} total send={} {} {} {}", qid, qids.size(), title, body, bizNameId);
        for (String senderQid : qids) {
            tokenQueueService.sendMessageToSpecificUser(title, body, senderQid, MessageOriginEnum.A);
        }
    }
}
