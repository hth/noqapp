package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.UserPreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mostly subscribe user to a topic and updates users preferences with recent queue or purchase.
 * hitender
 * 7/9/21 2:28 PM
 */
@Service
public class SubscribeTopicService {
    private static final Logger LOG = LoggerFactory.getLogger(SubscribeTopicService.class);

    private UserPreferenceManager userPreferenceManager;
    private BizStoreManager bizStoreManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private FirebaseService firebaseService;

    @Autowired
    public SubscribeTopicService(
        UserPreferenceManager userPreferenceManager,
        BizStoreManager bizStoreManager,
        RegisteredDeviceManager registeredDeviceManager,
        FirebaseService firebaseService
    ) {
        this.userPreferenceManager = userPreferenceManager;
        this.bizStoreManager = bizStoreManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.firebaseService = firebaseService;
    }

    @Async
    public void addSubscribedTopic(String qid, BizStoreEntity bizStore) {
        try {
            UserPreferenceEntity userPreference = userPreferenceManager.findByQueueUserId(qid);

            /* Always add to suggested. */
            switch (bizStore.getBusinessType()) {
                case DO:
                case CD:
                case CDQ:
                case BK:
                case HS:
                case PW:
                case LB:
                    Set<String> codeQRs = new HashSet<>();
                    List<BizStoreEntity> bizStores = bizStoreManager.getAllBizStores(bizStore.getBizName().getId());
                    for (BizStoreEntity bizStoreFound : bizStores) {
                        codeQRs.add(bizStoreFound.getCodeQR());
                    }

                    codeQRs.retainAll(userPreference.getFavoriteSuggested());
                    if (codeQRs.isEmpty()) {
                        userPreference.addFavoriteSuggested(bizStore.getCodeQR());
                    } else {
                        if (codeQRs.iterator().hasNext()) {
                            String codeQR = codeQRs.iterator().next();
                            userPreference.getFavoriteSuggested().remove(codeQRs.iterator().next());
                            userPreference.addFavoriteSuggested(codeQR);
                        }
                    }
                    break;
                default:
                    userPreference.getFavoriteSuggested().remove(bizStore.getCodeQR());
                    userPreference.addFavoriteSuggested(bizStore.getCodeQR());
            }

            /* When user signs up with new device or token, subscribe to these topics by default. */
            if (!userPreference.getSubscriptionTopics().contains(bizStore.getBusinessType().getName())) {
                userPreference.addSubscriptionTopic(bizStore.getBusinessType().getName());

                RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(qid);
                firebaseService.subscribeToTopic(bizStore.getBusinessType(), registeredDevice);
            }

            userPreferenceManager.save(userPreference);
            LOG.info("Updated preference with {} subscription={} recommended={}", qid, bizStore.getBusinessType().getName(), bizStore.getBizName().getBusinessName());
        } catch (Exception e) {
            LOG.error("Failed subscribing or adding to recommended {} {}", qid, e.getLocalizedMessage(), e);
        }
    }
}
