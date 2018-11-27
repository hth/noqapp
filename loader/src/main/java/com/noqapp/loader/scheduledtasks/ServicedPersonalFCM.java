package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonClientData;
import com.noqapp.domain.json.fcm.data.JsonClientOrderData;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BizService;
import com.noqapp.service.FirebaseMessageService;
import com.noqapp.service.StatsCronService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
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

    private String sendPersonalNotificationSwitch;
    private int numberOfAttemptsToSendFCM;

    private QueueManager queueManager;
    private PurchaseOrderManager purchaseOrderManager;
    private TokenQueueManager tokenQueueManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private MedicalRecordManager medicalRecordManager;
    private UserProfileManager userProfileManager;
    private BizService bizService;
    private FirebaseMessageService firebaseMessageService;
    private StatsCronService statsCronService;

    private StatsCronEntity statsCron;

    @Autowired
    public ServicedPersonalFCM(
            @Value ("${ServicedPersonalFCM.sendPersonalNotification.switch}")
            String sendPersonalNotificationSwitch,

            @Value ("${ServicedPersonalFCM.numberOfAttemptsToSendFCM}")
            int numberOfAttemptsToSendFCM,

            QueueManager queueManager,
            PurchaseOrderManager purchaseOrderManager,
            TokenQueueManager tokenQueueManager,
            RegisteredDeviceManager registeredDeviceManager,
            MedicalRecordManager medicalRecordManager,
            UserProfileManager userProfileManager,
            BizService bizService,
            FirebaseMessageService firebaseMessageService,
            StatsCronService statsCronService
    ) {
        this.sendPersonalNotificationSwitch = sendPersonalNotificationSwitch;
        this.numberOfAttemptsToSendFCM = numberOfAttemptsToSendFCM;

        this.queueManager = queueManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.tokenQueueManager = tokenQueueManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.medicalRecordManager = medicalRecordManager;
        this.userProfileManager = userProfileManager;
        this.bizService = bizService;
        this.firebaseMessageService = firebaseMessageService;
        this.statsCronService = statsCronService;
    }

    @Scheduled (fixedDelayString = "${loader.ServicedPersonalFCM.sendPersonalNotificationOnService}")
    public void sendPersonalNotificationOnService() {
        statsCron = new StatsCronEntity(
                ServicedPersonalFCM.class.getName(),
                "Serviced_Client_FCM",
                sendPersonalNotificationSwitch);

        int found = 0, failure = 0, sent = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(sendPersonalNotificationSwitch)) {
            LOG.debug("feature is {}", sendPersonalNotificationSwitch);
        }

        try {
            List<QueueEntity> queues = queueManager.findAllClientServiced(numberOfAttemptsToSendFCM);
            found = queues.size();

            for (QueueEntity queue : queues) {
                RegisteredDeviceEntity registeredDevice;
                if (StringUtils.isNotBlank(queue.getGuardianQid())) {
                    registeredDevice = registeredDeviceManager.findFCMToken(queue.getGuardianQid(), queue.getDid());
                } else {
                    registeredDevice = registeredDeviceManager.findFCMToken(queue.getQueueUserId(), queue.getDid());
                }

                //TODO add cache Redis.
                if (null == registeredDevice || StringUtils.isBlank(registeredDevice.getToken())) {
                    LOG.info("Skipped sending message qid={} did={}", queue.getQueueUserId(), queue.getDid());
                    skipped++;
                    queueManager.increaseAttemptToSendNotificationCount(queue.getId());
                } else {
                    TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(queue.getCodeQR());
                    JsonMessage jsonMessage = composeMessage(registeredDevice, tokenQueue.getTopic(), queue);
                    if (firebaseMessageService.messageToTopic(jsonMessage)) {
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
            LOG.error("Failed sending serviced FCM, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != found || 0 != failure || 0 != sent || 0 != skipped) {
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("skipped", skipped);
                statsCron.addStats("sentServicedClientFCM", sent);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} sentServicedClientFCM={}", found, failure, sent);
            }
        }
    }

    @Scheduled (fixedDelayString = "${loader.ServicedPersonalFCM.sendPersonalNotificationOnService}")
    public void sendPersonalNotificationOnDelivery() {
        statsCron = new StatsCronEntity(
            ServicedPersonalFCM.class.getName(),
            "Order_Client_FCM",
            sendPersonalNotificationSwitch);

        int found = 0, failure = 0, sent = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(sendPersonalNotificationSwitch)) {
            LOG.debug("feature is {}", sendPersonalNotificationSwitch);
        }

        try {
            List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManager.findAllClientOrderDelivered(numberOfAttemptsToSendFCM);
            found = purchaseOrders.size();

            for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findFCMToken(
                    purchaseOrder.getQueueUserId(),
                    purchaseOrder.getDid());

                //TODO add cache Redis.
                if (null == registeredDevice || StringUtils.isBlank(registeredDevice.getToken())) {
                    LOG.info("Skipped sending message qid={} did={}", purchaseOrder.getQueueUserId(), purchaseOrder.getDid());
                    skipped++;
                    purchaseOrderManager.increaseAttemptToSendNotificationCount(purchaseOrder.getId());
                } else {
                    TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(purchaseOrder.getCodeQR());
                    JsonMessage jsonMessage = composeMessage(registeredDevice, tokenQueue, purchaseOrder);
                    if (firebaseMessageService.messageToTopic(jsonMessage)) {
                        purchaseOrder.setNotifiedOnService(true);
                        purchaseOrderManager.save(purchaseOrder);
                        sent++;
                    } else {
                        failure++;
                        purchaseOrderManager.increaseAttemptToSendNotificationCount(purchaseOrder.getId());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed sending on delivery FCM, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != found || 0 != failure || 0 != sent || 0 != skipped) {
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("skipped", skipped);
                statsCron.addStats("sentOrderClientFCM", sent);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} sendPersonalNotificationOnDelivery={}", found, failure, sent);
            }
        }
    }

    @Scheduled (fixedDelayString = "${loader.ServicedPersonalFCM.sendPersonalNotificationOnFollowUp}")
    public void sendPersonalNotificationOnMedicalFollowUp() {
        statsCron = new StatsCronEntity(
            ServicedPersonalFCM.class.getName(),
            "Medical_Follow_Up_Client_FCM",
            sendPersonalNotificationSwitch);

        int found = 0, failure = 0, sent = 0, skipped = 0;
        if ("OFF".equalsIgnoreCase(sendPersonalNotificationSwitch)) {
            LOG.debug("feature is {}", sendPersonalNotificationSwitch);
        }

        try {
            List<MedicalRecordEntity> medicalRecords = medicalRecordManager.findByFollowUpWithoutNotificationSent(2);
            found = medicalRecords.size();

            for (MedicalRecordEntity medicalRecord : medicalRecords) {
                String qid = medicalRecord.getQueueUserId();
                UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
                if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
                    userProfile = userProfileManager.findOneByPhone(userProfile.getGuardianPhone());
                }
                List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(userProfile.getQueueUserId());
                for(RegisteredDeviceEntity registeredDevice : registeredDevices) {
                    if (null == registeredDevice || StringUtils.isBlank(registeredDevice.getToken())) {
                        LOG.info("Skipped sending message qid={}", userProfile.getQueueUserId());
                        skipped++;
                    } else {
                        BizStoreEntity bizStore = bizService.getByStoreId(medicalRecord.getBizStoreId());
                        JsonMessage jsonMessage = composeMessageForMedicalFollowUp(
                            registeredDevice,
                            userProfile.getQueueUserId();
                            bizStore.getCodeQR(),
                            medicalRecord.getBusinessName(),
                            DateUtil.now().plusDays(Integer.parseInt(medicalRecord.getFollowUpInDays()) - 2).toDate());

                        if (firebaseMessageService.messageToTopic(jsonMessage)) {
                            sent++;
                        } else {
                            failure++;
                        }
                    }
                }

                medicalRecord.setNotifiedFollowUp(true);
                medicalRecordManager.save(medicalRecord);
            }
        } catch (Exception e) {
            LOG.error("Failed sending on delivery FCM, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != found || 0 != failure || 0 != sent || 0 != skipped) {
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("skipped", skipped);
                statsCron.addStats("sentMedicalFollowUpFCM", sent);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} sendPersonalNotificationOnMedicalFollowUp={}", found, failure, sent);
            }
        }
    }

    /**
     * Send personal message upon service. Include topic to help un-subscribe. Un-subscription is not a sure shot
     * thing. This works when app is running in background(TODO confirm) or when app is in foreground(Active). If app is closed then
     * this will not work until app is opened and this message is read.
     *
     * @param registeredDevice
     * @param topic            send topic to initiate un-subscription by app. Not a sure shot thing. Will work
     * @param queue
     * @return
     */
    private JsonMessage composeMessage(RegisteredDeviceEntity registeredDevice, String topic, QueueEntity queue) {
        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonClientData(FirebaseMessageTypeEnum.P, MessageOriginEnum.QR)
                .setCodeQR(queue.getCodeQR())
                .setQueueUserId(queue.getQueueUserId())
                .setToken(queue.getTokenNumber())
                .setQueueUserState(queue.getQueueUserState())
                .setTopic(topic);

        switch (queue.getQueueUserState()) {
            case S:
                if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
                    jsonMessage.getNotification()
                            .setBody("How was your service?")
                            .setTitle(queue.getDisplayName());
                } else {
                    jsonMessage.setNotification(null);
                    jsonData.setBody("How was your service?")
                            .setTitle(queue.getDisplayName());
                }
                break;
            case N:
                if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
                    jsonMessage.getNotification()
                            .setBody("You were not served?")
                            .setTitle(queue.getDisplayName());
                } else {
                    jsonMessage.setNotification(null);
                    jsonData.setBody("You were not served?")
                            .setTitle(queue.getDisplayName());
                }
                break;
            default:
                LOG.warn("Un-supported status reached. Skipping qid={} did={}", queue.getQueueUserId(), queue.getDid());
                break;
        }

        jsonMessage.setData(jsonData);
        return jsonMessage;
    }

    private JsonMessage composeMessage(RegisteredDeviceEntity registeredDevice, TokenQueueEntity tokenQueue, PurchaseOrderEntity purchaseOrder) {
        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonClientOrderData(FirebaseMessageTypeEnum.P, MessageOriginEnum.OR)
            .setCodeQR(purchaseOrder.getCodeQR())
            .setQueueUserId(purchaseOrder.getQueueUserId())
            .setOrderNumber(purchaseOrder.getTokenNumber())
            .setPurchaseOrderState(purchaseOrder.getPresentOrderState())
            .setTopic(tokenQueue.getTopic());

        switch (purchaseOrder.getPresentOrderState()) {
            case OD:
                if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
                    jsonMessage.getNotification()
                        .setBody("How was your order?")
                        .setTitle(tokenQueue.getDisplayName());
                } else {
                    jsonMessage.setNotification(null);
                    jsonData.setBody("How was your order?")
                        .setTitle(tokenQueue.getDisplayName());
                }
                break;
            default:
                LOG.warn("Un-supported status reached. Skipping qid={} did={}", purchaseOrder.getQueueUserId(), purchaseOrder.getDid());
                break;
        }

        jsonMessage.setData(jsonData);
        return jsonMessage;
    }

    private JsonMessage composeMessageForMedicalFollowUp(
        RegisteredDeviceEntity registeredDevice,
        String queueUserId,
        String codeQR,
        String displayName,
        Date followUpDay
    ) {
        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonTopicData(MessageOriginEnum.MF, FirebaseMessageTypeEnum.P).getJsonMedicalFollowUp()
            .setQueueUserId(queueUserId)
            .setCodeQR(codeQR)
            .setFollowUpDay(followUpDay);

        if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
            jsonMessage.getNotification()
                .setBody("Follow up has been scheduled on " + followUpDay)
                .setTitle("Follow up for: " + displayName);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setBody("Follow up has been scheduled on " + followUpDay)
                .setTitle("Follow up for: " + displayName);
        }

        jsonMessage.setData(jsonData);
        return jsonMessage;
    }
}
