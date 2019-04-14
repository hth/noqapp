package com.noqapp.domain.common;

import static com.noqapp.common.utils.DateUtil.SDF_DD_MMM_YYYY;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonTokenAndQueue;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonClientData;
import com.noqapp.domain.json.fcm.data.JsonClientOrderData;
import com.noqapp.domain.json.fcm.data.JsonClientTokenAndQueueData;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-12 11:30
 */
public class ComposeMessagesForFCM {
    private static final Logger LOG = LoggerFactory.getLogger(ComposeMessagesForFCM.class);

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
    public static JsonMessage composeMessage(RegisteredDeviceEntity registeredDevice, String topic, QueueEntity queue) {
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

    /**
     * When merchant adds client to queue. This message is sent to client.
     */
    @Mobile
    public static JsonMessage composeMessage(
        RegisteredDeviceEntity registeredDevice,
        List<JsonTokenAndQueue> jsonTokenAndQueues,
        String body,
        String title
    ) {
        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonClientTokenAndQueueData(FirebaseMessageTypeEnum.P, MessageOriginEnum.CQO)
            .setTokenAndQueues(jsonTokenAndQueues);

        if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
            jsonMessage.getNotification()
                .setBody(body)
                .setTitle(title);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setBody(body)
                .setTitle(title);
        }

        jsonMessage.setData(jsonData);
        return jsonMessage;
    }

    public static JsonMessage composeMessage(RegisteredDeviceEntity registeredDevice, TokenQueueEntity tokenQueue, PurchaseOrderEntity purchaseOrder) {
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

    public static JsonMessage composeMessageForMedicalFollowUp(
        RegisteredDeviceEntity registeredDevice,
        String queueUserId,
        String codeQR,
        String displayName,
        Date followUpDay
    ) {
        DateTime followUpScheduledFor = DateUtil.toDateTime(followUpDay);
        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
        JsonData jsonData = new JsonTopicData(MessageOriginEnum.MF, FirebaseMessageTypeEnum.P).getJsonMedicalFollowUp()
            .setQueueUserId(queueUserId)
            .setCodeQR(codeQR)
            .setPopFollowUpAlert(followUpScheduledFor.minusDays(2).toDate())
            .setFollowUpDay(followUpScheduledFor.toDate());

        if (registeredDevice.getDeviceType() == DeviceTypeEnum.I) {
            jsonMessage.getNotification()
                .setBody("Follow up has been scheduled for " + SDF_DD_MMM_YYYY.format(followUpScheduledFor.toDate()))
                .setTitle(displayName + " follow-up");
        } else {
            jsonMessage.setNotification(null);
            jsonData.setBody("Follow up has been scheduled for " + SDF_DD_MMM_YYYY.format(followUpScheduledFor.toDate()))
                .setTitle(displayName + " follow-up");
        }

        jsonMessage.setData(jsonData);
        return jsonMessage;
    }
}
