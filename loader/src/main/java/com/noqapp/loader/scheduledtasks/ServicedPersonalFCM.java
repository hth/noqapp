package com.noqapp.loader.scheduledtasks;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.CronStatsEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonClientData;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.CronStatsService;
import com.noqapp.service.FirebaseService;

import java.util.List;

/**
 * This process sends out personal FCM when client is serviced or skipped. Message includes topic to help client
 * un-subscribe to prevent receiving any more message sent to topic.
 * User: hitender
 * Date: 3/6/17 6:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ServicedPersonalFCM {
    private static final Logger LOG = LoggerFactory.getLogger(ServicedPersonalFCM.class);

    private String sendPersonalNotification;

    private QueueManager queueManager;
    private TokenQueueManager tokenQueueManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private FirebaseService firebaseService;
    private CronStatsService cronStatsService;

    private CronStatsEntity cronStats;

    @Autowired
    public ServicedPersonalFCM(
            @Value ("${ServicedPersonalFCM.sendPersonalNotification}")
            String sendPersonalNotification,

            QueueManager queueManager,
            TokenQueueManager tokenQueueManager,
            RegisteredDeviceManager registeredDeviceManager,
            FirebaseService firebaseService,
            CronStatsService cronStatsService
    ) {
        this.sendPersonalNotification = sendPersonalNotification;

        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (fixedDelayString = "${loader.ServicedPersonalFCM.sendPersonalNotificationOnService}")
    public void sendPersonalNotificationOnService() {
        cronStats = new CronStatsEntity(
                ServicedPersonalFCM.class.getName(),
                "Serviced_Client_FCM",
                sendPersonalNotification);

        int found = 0, failure = 0, sent = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(sendPersonalNotification)) {
            LOG.debug("feature is {}", sendPersonalNotification);
        }

        try {
            List<QueueEntity> queues = queueManager.findAllClientServiced(5);
            found = queues.size();

            for (QueueEntity queue : queues) {
                String token = registeredDeviceManager.findFCMToken(queue.getRid(), queue.getDid());
                /* TODO add cache Redis. */
                if (StringUtils.isBlank(token)) {
                    LOG.info("Skipped sending message rid={} did={}", queue.getRid(), queue.getDid());
                    skipped++;
                    queueManager.increaseAttemptToSendNotificationCount(queue.getId());
                } else {
                    TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(queue.getCodeQR());
                    JsonMessage jsonMessage = composeMessage(token, tokenQueue.getTopic(), queue);
                    if (firebaseService.messageToTopic(jsonMessage)) {
                        queue.setNotifiedOnService(true);
                        queueManager.save(queue);
                        sent++;
                    } else {
                        failure++;
                        queueManager.increaseAttemptToSendNotificationCount(queue.getId());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending serviced FCM, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != found || 0 != failure || 0 != sent) {
                cronStats.addStats("found", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("skipped", skipped);
                cronStats.addStats("sentServicedClientFCM", sent);
                cronStatsService.save(cronStats);

                /* Without if condition its too noisy. */
                LOG.info("complete found={} failure={} sentServicedClientFCM={}", found, failure, sent);
            }
        }
    }

    /**
     * Send personal message upon service. Include topic to help un-subscribe. Un-subscription is not a sure shot
     * thing. This works when app is running in background(TODO confirm) or when app is in foreground(Active). If app is closed then
     * this will not work until app is opened and this message is read.
     *
     * @param token
     * @param topic send topic to initiate un-subscription by app. Not a sure shot thing. Will work
     * @param queue
     * @return
     */
    private JsonMessage composeMessage(String token, String topic, QueueEntity queue) {
        JsonMessage jsonMessage = new JsonMessage(token);
        JsonData jsonData = new JsonClientData(FirebaseMessageTypeEnum.P)
                .setCodeQR(queue.getCodeQR())
                .setQueueUserState(queue.getQueueUserState())
                .setTopic(topic);
        jsonMessage.setData(jsonData);

        switch (queue.getQueueUserState()) {
            case S:
//                jsonMessage.getNotification()
//                        .setBody("How was your service?")
//                        .setTitle(queue.getDisplayName());
                break;
            case N:
//                jsonMessage.getNotification()
//                        .setBody("You were not served?")
//                        .setTitle(queue.getDisplayName());
                break;
            default:
                LOG.warn("Un-supported status reached. Skipping rid={} did={}", queue.getRid(), queue.getDid());
                break;
        }

        return jsonMessage;
    }
}
