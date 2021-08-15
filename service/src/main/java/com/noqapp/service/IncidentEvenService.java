package com.noqapp.service;

import static com.noqapp.domain.types.IncidentEventEnum.SOSP;

import com.noqapp.domain.IncidentEventEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.common.ComposeMessagesForFCM;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.repository.IncidentEventManager;
import com.noqapp.repository.RegisteredDeviceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * hitender
 * 5/21/21 8:45 AM
 */
@Service
public class IncidentEvenService {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentEvenService.class);

    private IncidentEventManager incidentEventManager;
    private RegisteredDeviceManager registeredDeviceManager;

    private UserProfilePreferenceService userProfilePreferenceService;
    private FirebaseMessageService firebaseMessageService;
    private FirebaseService firebaseService;

    /** When in same thread use Executor and not @Async. */
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public IncidentEvenService(
        IncidentEventManager incidentEventManager,
        RegisteredDeviceManager registeredDeviceManager,
        UserProfilePreferenceService userProfilePreferenceService,
        FirebaseMessageService firebaseMessageService,
        FirebaseService firebaseService
    ) {
        this.incidentEventManager = incidentEventManager;
        this.registeredDeviceManager = registeredDeviceManager;

        this.userProfilePreferenceService = userProfilePreferenceService;
        this.firebaseMessageService = firebaseMessageService;
        this.firebaseService = firebaseService;

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Async
    public void save(IncidentEventEntity incidentEvent) {
        int expectedView;
        if (SOSP == incidentEvent.getIncidentEvent()) {
            expectedView = alertSOSReceiver(incidentEvent);
        } else {
            expectedView = informAboutIncidentAndEvent(incidentEvent);
        }
        incidentEvent.setExpectedView(expectedView);
        incidentEventManager.save(incidentEvent);
    }

    private int alertSOSReceiver(IncidentEventEntity incidentEvent) {
        List<UserProfileEntity> userProfiles = userProfilePreferenceService.findSOSUsers(incidentEvent.getQid());
        for (UserProfileEntity userProfile : userProfiles) {
            RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(userProfile.getQueueUserId());

            JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessageForSOSIncidentEvent(incidentEvent, registeredDevice);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast SOS failed id={} qid={} topic={} message={}", incidentEvent.getId(), userProfile.getQueueUserId(), incidentEvent.getTopicWellFormatted(), jsonMessage.asJson());
            } else {
                LOG.debug("Sent SOS topic={} qid={} message={}", incidentEvent.getTopicWellFormatted(), userProfile.getQueueUserId(), jsonMessage.asJson());
            }
        }
        return userProfiles.size();
    }

    private int informAboutIncidentAndEvent(IncidentEventEntity incidentEvent) {
        List<String> tokens_A = new ArrayList<>();
        List<String> tokens_I = new ArrayList<>();
        try (Stream<GeoResult<RegisteredDeviceEntity>> geoResults = registeredDeviceManager.findDevicesWithinVicinity(
            incidentEvent.getCoordinate(),
            incidentEvent.getIncidentEvent().getDistanceToPropagateInformation())
        ) {
            geoResults.iterator().forEachRemaining(registeredDeviceEntityGeoResult -> {
                try {
                    switch (registeredDeviceEntityGeoResult.getContent().getDeviceType()) {
                        case A:
                            tokens_A.add(registeredDeviceEntityGeoResult.getContent().getToken());
                            break;
                        case I:
                            tokens_I.add(registeredDeviceEntityGeoResult.getContent().getToken());
                            break;
                        case W:
                            //Do nothing
                            break;
                    }
                } catch (Exception e) {
                    LOG.error("Failed adding token {} {} {}",
                        registeredDeviceEntityGeoResult.getContent().getId(),
                        registeredDeviceEntityGeoResult.getContent().getQueueUserId(),
                        e.getMessage());
                }
            });
        }

        subscribeAndSendMessage(incidentEvent, tokens_A, tokens_I);
        scheduledExecutorService.schedule(() -> unsubscribeTokensFromTopic(incidentEvent, tokens_A, tokens_I), 10, TimeUnit.SECONDS);
        return tokens_A.size() + tokens_I.size();
    }

    private void subscribeAndSendMessage(IncidentEventEntity incidentEvent, List<String> tokens_A, List<String> tokens_I) {
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            String topic = incidentEvent.getTopicWellFormatted();
            switch (deviceType) {
                case A:
                    if (firebaseService.subscribeTokenToTopic(tokens_A, topic)) {
                        JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessageForIncidentEvent(incidentEvent, deviceType);
                        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);

                        if (!fcmMessageBroadcast) {
                            LOG.warn("Broadcast failed id={} topic={} message={}", incidentEvent.getId(), incidentEvent.getTopicWellFormatted(), jsonMessage.asJson());
                        } else {
                            LOG.debug("Sent topic={} message={}", incidentEvent.getTopicWellFormatted(), jsonMessage.asJson());
                        }
                    }
                    break;
                case I:
                    if (firebaseService.subscribeTokenToTopic(tokens_I, topic)) {
                        JsonMessage jsonMessage = ComposeMessagesForFCM.composeMessageForIncidentEvent(incidentEvent, deviceType);
                        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);

                        if (!fcmMessageBroadcast) {
                            LOG.warn("Broadcast failed id={} topic={} message={}", incidentEvent.getId(), incidentEvent.getTopicWellFormatted(), jsonMessage.asJson());
                        } else {
                            LOG.debug("Sent topic={} message={}", incidentEvent.getTopicWellFormatted(), jsonMessage.asJson());
                        }
                    }
                    break;
                case W:
                    //Do nothing
                    break;
            }
        }
    }

    private void unsubscribeTokensFromTopic(IncidentEventEntity incidentEvent, List<String> tokens_A, List<String> tokens_I) {
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            String topic = incidentEvent.getTopicWellFormatted();
            switch (deviceType) {
                case A:
                    firebaseService.unsubscribeTokensFromTopic(tokens_A, topic);
                    break;
                case I:
                    firebaseService.unsubscribeTokensFromTopic(tokens_I, topic);
                    break;
                case W:
                    //Do nothing
                    break;
            }
        }
    }
}
