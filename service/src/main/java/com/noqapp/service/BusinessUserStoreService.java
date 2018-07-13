package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.json.JsonHour;
import com.noqapp.domain.json.JsonTopic;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BusinessUserStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static java.util.Comparator.comparing;

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
    private BizService bizService;

    @Autowired
    public BusinessUserStoreService(
            @Value ("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            BusinessUserStoreManager businessUserStoreManager,
            BusinessUserService businessUserService,
            TokenQueueService tokenQueueService,
            AccountService accountService,
            BizService bizService
    ) {
        this.queueLimit = queueLimit;
        this.businessUserStoreManager = businessUserStoreManager;
        this.businessUserService = businessUserService;
        this.tokenQueueService = tokenQueueService;
        this.accountService = accountService;
        this.bizService = bizService;
    }

    public void save(BusinessUserStoreEntity businessUserStore) {
        businessUserStoreManager.save(businessUserStore);
    }

    public void activateAccount(String qid, String bizNameId) {
        businessUserStoreManager.activateAccount(qid, bizNameId);
    }

    public void removeFromBusiness(String qid, String bizNameId) {
        businessUserStoreManager.removeFromBusiness(qid, bizNameId);
    }

    public void removeFromStore(String qid, String bizStoreId) {
        businessUserStoreManager.removeFromStore(qid, bizStoreId);
    }

    @Mobile
    public boolean hasAccess(String qid, String codeQR) {
        return businessUserStoreManager.hasAccess(qid, codeQR);
    }

    public boolean hasAccessUsingStoreId(String qid, String bizStoreId) {
        return businessUserStoreManager.hasAccessUsingStoreId(qid, bizStoreId);
    }

    /**
     * Used for queue supervisor role, store manager has little different view.
     *
     * @param qid
     * @return
     */
    @Mobile
    public List<JsonTopic> getQueues(String qid) {
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getQueues(qid, queueLimit);
        int size = businessUserStores.size();
        LOG.info("Found user associated to business count={}", size);

        String[] codes = new String[queueLimit <= size ? queueLimit : size];
        int i = 0;
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            codes[i] = businessUserStore.getCodeQR();
            i++;
        }

        List<TokenQueueEntity> tokenQueues = tokenQueueService.getTokenQueue(codes);
        LOG.info("tokenQueues found count={} for queueLimit={}", tokenQueues.size(), queueLimit);
        List<JsonTopic> jsonTopics = new ArrayList<>();
        for (TokenQueueEntity tokenQueue : tokenQueues) {
            JsonHour jsonHour = getJsonHour(tokenQueue.getId());
            jsonTopics.add(new JsonTopic(tokenQueue).setHour(jsonHour));
        }

        LOG.info("Sending jsonTopic count={}", jsonTopics.size());
        return jsonTopics;
    }

    @Mobile
    public JsonHour getJsonHour(String codeQR) {
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        DayOfWeek dayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
        StoreHourEntity storeHour = bizService.findStoreHour(bizStore.getId(), dayOfWeek);
        return new JsonHour()
                .setDayOfWeek(storeHour.getDayOfWeek())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setStartHour(storeHour.getStartHour())
                .setEndHour(storeHour.getEndHour())
                .setPreventJoining(storeHour.isPreventJoining())
                .setDayClosed(storeHour.isDayClosed())
                .setDelayedInMinutes(storeHour.getDelayedInMinutes());
    }

    /**
     * Gets all the queues associated with qid.
     *
     * @param qid
     * @return
     */
    public List<BusinessUserStoreEntity> findAllStoreQueueAssociated(String qid) {
        return businessUserStoreManager.getQueues(qid, 0);
    }

    public long findNumberOfPeopleAssignedToQueue(String bizStoreId) {
        return businessUserStoreManager.findNumberOfPeopleAssignedToQueue(bizStoreId);
    }

    public long findNumberOfPeoplePendingApprovalToQueue(String bizStoreId) {
        return businessUserStoreManager.findNumberOfPeoplePendingApprovalToQueue(bizStoreId);
    }

    /**
     * Gets all the profile information of queue manager for a specific store associated.
     *
     * @param bizStoreId
     * @return
     */
    public List<QueueSupervisor> getAllManagingStore(String bizStoreId) {
        List<QueueSupervisor> queueSupervisors = new ArrayList<>();
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getAllManagingStore(bizStoreId);
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String qid = businessUserStore.getQueueUserId();
            QueueSupervisor queueSupervisor = populateQueueSupervisorFromQid(bizStoreId, businessUserStore.getBizNameId(), qid);
            queueSupervisor
                    .setCreated(businessUserStore.getCreated())
                    .setActive(businessUserStore.isActive());

            queueSupervisors.add(queueSupervisor);
        }

        /* Sort by name. */
        queueSupervisors.sort(comparing(QueueSupervisor::getName));
        return queueSupervisors;
    }

    public List<QueueSupervisor> getAuthorizedUsersForBusiness(String bizNameId) {
        List<QueueSupervisor> queueSupervisors = new ArrayList<>();
        List<BusinessUserEntity> businessUsers = businessUserService.getAllForBusiness(bizNameId);

        for (BusinessUserEntity businessUser : businessUsers) {
            QueueSupervisor queueSupervisor = populateQueueSupervisorFromQid(null, bizNameId, businessUser.getQueueUserId());
            queueSupervisor
                    .setCreated(businessUser.getCreated())
                    .setActive(businessUser.isActive());

            queueSupervisors.add(queueSupervisor);
        }

        /* Sort by name. */
        queueSupervisors.sort(comparing(QueueSupervisor::getName));
        return queueSupervisors;
    }

    private QueueSupervisor populateQueueSupervisorFromQid(String bizStoreId, String bizNameId, String qid) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid, bizNameId);
        QueueSupervisor queueSupervisor = new QueueSupervisor();
        queueSupervisor.setBusinessUserId(businessUser.getId())
                .setStoreId(bizStoreId)
                .setBusinessId(bizNameId)
                .setName(userProfile.getName())
                .setPhone(userProfile.getPhone())
                .setCountryShortName(userProfile.getCountryShortName())
                .setAddress(userProfile.getAddress())
                .setEmail(userProfile.getEmail())
                .setQueueUserId(qid)
                .setUserLevel(userProfile.getLevel())
                .setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus());
        return queueSupervisor;
    }

    /**
     * Populate list with specific bizStoreId. These users will be available to be added as queue supervisor.
     *
     * @param bizNameId
     * @param bizStoreId Add bizStoreId to the list of QueueSupervisor
     * @return
     */
    public List<QueueSupervisor> getAllNonAdminForBusiness(String bizNameId, String bizStoreId) {
        List<BusinessUserEntity> businessUsers = businessUserService.getAllNonAdminForBusiness(bizNameId);
        List<QueueSupervisor> queueSupervisors = new ArrayList<>();

        for (BusinessUserEntity businessUser : businessUsers) {
            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(businessUser.getQueueUserId());
            QueueSupervisor queueSupervisor = new QueueSupervisor();
            queueSupervisor.setBusinessUserId(businessUser.getId())
                    .setStoreId(bizStoreId)
                    .setBusinessId(bizNameId)
                    .setName(userProfile.getName())
                    .setPhone(userProfile.getPhone())
                    .setCountryShortName(userProfile.getCountryShortName())
                    .setAddress(userProfile.getAddress())
                    .setEmail(userProfile.getEmail())
                    .setQueueUserId(userProfile.getQueueUserId())
                    .setUserLevel(userProfile.getLevel())
                    .setCreated(businessUser.getCreated())
                    .setActive(businessUser.isActive())
                    .setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus());

            queueSupervisors.add(queueSupervisor);
        }
        
        /* Sort by name. */
        queueSupervisors.sort(comparing(QueueSupervisor::getName));
        return queueSupervisors;
    }

    /**
     * Adding existing Q_SUPERVISOR or S_MANAGER to manage queue.
     *
     * @param qid
     * @param bizStore
     * @param businessUserRegistrationStatus
     */
    public void addToBusinessUserStore(
            String qid,
            BizStoreEntity bizStore,
            BusinessUserRegistrationStatusEnum businessUserRegistrationStatus,
            UserLevelEnum userLevel) {
        BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
                qid,
                bizStore.getId(),
                bizStore.getBizName().getId(),
                bizStore.getCodeQR(),
                userLevel);

        /*
         * Marked as inactive until user signs and agrees to be a queue supervisor.
         * Will be active upon approval.
         */
        if (BusinessUserRegistrationStatusEnum.V == businessUserRegistrationStatus) {
            /* If business user has been validated already then no need to mark it in-active. */
            businessUserStore.active();
        } else {
            businessUserStore.inActive();
        }
        save(businessUserStore);
    }

    List<BusinessUserStoreEntity> findAllManagingStoreWithUserLevel(String qid, UserLevelEnum userLevel) {
        return businessUserStoreManager.findAllManagingStoreWithUserLevel(qid, userLevel);
    }

    @Mobile
    public boolean hasAccessWithUserLevel(String qid, String codeQR, UserLevelEnum userLevel) {
        return businessUserStoreManager.hasAccessWithUserLevel(qid, codeQR, userLevel);
    }

    @Mobile
    public BusinessUserStoreEntity findOneByQidAndCodeQR(String qid, String codeQR) {
        return businessUserStoreManager.findOneByQidAndCodeQR(qid, codeQR);
    }
}
