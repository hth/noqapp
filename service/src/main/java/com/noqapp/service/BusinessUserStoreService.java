package com.noqapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonTopic;
import com.noqapp.repository.BusinessUserStoreManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 12/14/16 12:19 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessUserStoreService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserStoreService.class);

    private int queueLimit;
    private BusinessUserStoreManager businessUserStoreManager;
    private TokenQueueService tokenQueueService;
    private AccountService accountService;

    @Autowired
    public BusinessUserStoreService(
            @Value ("${BusinessUserStoreService.queue.limit:10}")
            int queueLimit,

            BusinessUserStoreManager businessUserStoreManager,
            TokenQueueService tokenQueueService,
            AccountService accountService
    ) {
        this.queueLimit = queueLimit;
        this.businessUserStoreManager = businessUserStoreManager;
        this.tokenQueueService = tokenQueueService;
        this.accountService = accountService;
    }

    public void save(BusinessUserStoreEntity businessUserStore) {
        businessUserStoreManager.save(businessUserStore);
    }

    @Mobile
    public boolean hasAccess(String rid, String codeQR) {
        return businessUserStoreManager.hasAccess(rid, codeQR);
    }

    @Mobile
    public List<JsonTopic> getQueues(String rid) {
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getQueues(rid, queueLimit);
        LOG.info("Found user associated to business count={}", businessUserStores.size());

        String[] codes = new String[queueLimit];
        int i = 0;
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            codes[i] = businessUserStore.getCodeQR();
            i++;
        }

        List<TokenQueueEntity> tokenQueues = tokenQueueService.getTokenQueue(codes);
        LOG.info("tokenQueues found count={} for codes={}", tokenQueues.size(), codes);
        List<JsonTopic> jsonTopics = new ArrayList<>();
        for (TokenQueueEntity tokenQueue : tokenQueues) {
            jsonTopics.add(new JsonTopic(tokenQueue));
        }

        LOG.info("Found queues count={}", jsonTopics.size());
        return jsonTopics;
    }

    public long findNumberOfPeopleAssignedToQueue(String businessStoreId) {
        return businessUserStoreManager.findNumberOfPeopleAssignedToQueue(businessStoreId);
    }

    /**
     * Gets all the profile information of queue manager for a specific store associated.
     *
     * @param storeId
     * @return
     */
    public List<UserProfileEntity> getAllQueueManagers(String storeId) {
        List<UserProfileEntity> userProfiles = new ArrayList<>();
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getAllQueueManagers(storeId);
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String rid = businessUserStore.getReceiptUserId();
            UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(rid);
            userProfiles.add(userProfile);
        }

        return userProfiles;
    }
}
