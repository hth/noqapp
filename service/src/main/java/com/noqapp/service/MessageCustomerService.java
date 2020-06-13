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
public class MessageCustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageCustomerService.class);

    private QueueService queueService;
    private TokenQueueService tokenQueueService;
    private NotificationMessageManager notificationMessageManager;

    @Autowired
    public MessageCustomerService(
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

    public int sendMessageToPastClients(String bizNameId) {
        return queueService.countDistinctQIDsInBiz(bizNameId, 60);
    }

    public int sendMessageToPastClients(String bizNameId, int days) {
        return queueService.countDistinctQIDsInBiz(bizNameId, days == 0 ? 60 : days);
    }

    public int sendMessageToPastClients(String title, String body, String bizNameId, String qid) {
        NotificationMessageEntity notificationMessage = new NotificationMessageEntity()
            .setTitle(title)
            .setBody(body)
            .setQueueUserId(qid);
        notificationMessageManager.save(notificationMessage);

        int sendMessageCount = sendMessageToPastClients(bizNameId);
        LOG.info("Sending message by {} total send={} {} {} {}", qid, sendMessageCount, title, body, bizNameId);
        queueService.distinctQIDsInBiz(bizNameId, 60).stream().iterator()
            .forEachRemaining(senderQid -> tokenQueueService.sendMessageToSpecificUser(title, body, senderQid, MessageOriginEnum.A));

        notificationMessage.setMessageSendCount(sendMessageCount);
        notificationMessageManager.save(notificationMessage);
        return sendMessageCount;
    }
}
