package com.noqapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.helper.QueueSupervisor;
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
    private BusinessUserService businessUserService;
    private TokenQueueService tokenQueueService;
    private AccountService accountService;

    @Autowired
    public BusinessUserStoreService(
            @Value ("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            BusinessUserStoreManager businessUserStoreManager,
            BusinessUserService businessUserService,
            TokenQueueService tokenQueueService,
            AccountService accountService
    ) {
        this.queueLimit = queueLimit;
        this.businessUserStoreManager = businessUserStoreManager;
        this.businessUserService = businessUserService;
        this.tokenQueueService = tokenQueueService;
        this.accountService = accountService;
    }

    public void save(BusinessUserStoreEntity businessUserStore) {
        businessUserStoreManager.save(businessUserStore);
    }

    @Mobile
    public boolean hasAccess(String qid, String codeQR) {
        return businessUserStoreManager.hasAccess(qid, codeQR);
    }

    @Mobile
    public List<JsonTopic> getQueues(String qid) {
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getQueues(qid, queueLimit);
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
    public List<QueueSupervisor> getAllQueueManagers(String storeId) {
        List<QueueSupervisor> queueSupervisors = new ArrayList<>();
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getAllQueueManagers(storeId);
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String qid = businessUserStore.getQueueUserId();
            UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(qid);
            BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid);
            QueueSupervisor queueSupervisor = new QueueSupervisor();
            queueSupervisor.setBusinessUserId(businessUser.getId())
                    .setStoreId(storeId)
                    .setBusinessId(businessUserStore.getBizNameId())
                    .setName(userProfile.getName())
                    .setPhone(userProfile.getPhone())
                    .setAddress(userProfile.getAddress())
                    .setEmail(userProfile.getEmail())
                    .setQueueUserId(qid)
                    .setUserLevel(userProfile.getLevel())
                    .setCreated(businessUserStore.getCreated())
                    .setActive(businessUserStore.isActive())
                    .setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus());

            queueSupervisors.add(queueSupervisor);
        }

        return queueSupervisors;
    }
}
