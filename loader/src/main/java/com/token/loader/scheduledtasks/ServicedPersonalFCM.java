package com.token.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.token.domain.CronStatsEntity;
import com.token.domain.QueueEntity;
import com.token.domain.json.fcm.JsonMessage;
import com.token.domain.json.fcm.data.JsonClientData;
import com.token.domain.json.fcm.data.JsonData;
import com.token.domain.types.FirebaseMessageTypeEnum;
import com.token.repository.QueueManager;
import com.token.repository.RegisteredDeviceManager;
import com.token.service.CronStatsService;
import com.token.service.FirebaseService;

import java.util.List;

/**
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
    private RegisteredDeviceManager registeredDeviceManager;
    private FirebaseService firebaseService;
    private CronStatsService cronStatsService;

    private CronStatsEntity cronStats;

    @Autowired
    public ServicedPersonalFCM(
            @Value ("${ServicedPersonalFCM.sendPersonalNotification}")
            String sendPersonalNotification,

            QueueManager queueManager,
            RegisteredDeviceManager registeredDeviceManager,
            FirebaseService firebaseService,
            CronStatsService cronStatsService
    ) {
        this.sendPersonalNotification = sendPersonalNotification;

        this.queueManager = queueManager;
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

        int found = 0, failure = 0, sent = 0;
        if ("OFF".equalsIgnoreCase(sendPersonalNotification)) {
            LOG.debug("feature is {}", sendPersonalNotification);
        }

        try {
            List<QueueEntity> queues = queueManager.findAllClientServiced();
            found = queues.size();

            for (QueueEntity queue : queues) {
                String token = registeredDeviceManager.findToken(queue.getRid(), queue.getDid());
                JsonMessage jsonMessage = composeMessage(token, queue);
                if (firebaseService.messageToTopic(jsonMessage)) {
                    queue.setNotifiedOnService(true);
                    queueManager.save(queue);
                    sent++;
                } else {
                    failure++;
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending serviced FCM, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            cronStats.addStats("found", found);
            cronStats.addStats("failure", failure);
            cronStats.addStats("sentServicedClientFCM", sent);
            cronStatsService.save(cronStats);

            /* Too noisy. */
            //LOG.debug("complete found={} failure={} sentServicedClientFCM={}", found, failure, sent);
        }
    }

    private JsonMessage composeMessage(String token, QueueEntity queue) {
        JsonMessage jsonMessage = new JsonMessage(token);
        JsonData jsonData = new JsonClientData(FirebaseMessageTypeEnum.P)
                .setCodeQR(queue.getCodeQR())
                .setQueueUserState(queue.getQueueUserState());
        jsonMessage.setData(jsonData);

        switch (queue.getQueueUserState()) {
            case S:
                jsonMessage.getNotification()
                        .setBody("How was your service?")
                        .setTitle(queue.getDisplayName());
                break;
            case N:
                jsonMessage.getNotification()
                        .setBody("You were not served?")
                        .setTitle(queue.getDisplayName());
                break;
            default:
                LOG.warn("Un-supported status reached. Skipping rid={} did={}", queue.getRid(), queue.getDid());
                break;
        }

        return jsonMessage;
    }
}
