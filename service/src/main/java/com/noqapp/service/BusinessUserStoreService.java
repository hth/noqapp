package com.noqapp.service;

import static com.noqapp.domain.types.CommonStatusEnum.FAILURE;
import static com.noqapp.domain.types.CommonStatusEnum.SUCCESS;
import static java.util.Comparator.comparing;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.annotation.Television;
import com.noqapp.domain.helper.QueueSupervisor;
import com.noqapp.domain.json.JsonDataVisibility;
import com.noqapp.domain.json.JsonHour;
import com.noqapp.domain.json.JsonTopic;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.CommonStatusEnum;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 12/14/16 12:19 PM
 */
@SuppressWarnings({
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
    private PreferredBusinessService preferredBusinessService;
    private BusinessUserService businessUserService;
    private TokenQueueService tokenQueueService;
    private AccountService accountService;
    private BizService bizService;
    private ProfessionalProfileService professionalProfileService;

    @Autowired
    public BusinessUserStoreService(
        @Value("${BusinessUserStoreService.queue.limit}")
        int queueLimit,

        BusinessUserStoreManager businessUserStoreManager,
        PreferredBusinessService preferredBusinessService,
        BusinessUserService businessUserService,
        TokenQueueService tokenQueueService,
        AccountService accountService,
        BizService bizService,
        ProfessionalProfileService professionalProfileService
    ) {
        this.queueLimit = queueLimit;
        this.businessUserStoreManager = businessUserStoreManager;
        this.preferredBusinessService = preferredBusinessService;
        this.businessUserService = businessUserService;
        this.tokenQueueService = tokenQueueService;
        this.accountService = accountService;
        this.bizService = bizService;
        this.professionalProfileService = professionalProfileService;
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

    @Mobile
    public boolean businessHasThisAsPreferredStoreId(String qid, String codeQR, String preferredBizStoreId) {
        BusinessUserStoreEntity businessUserStore = businessUserStoreManager.findOneByQidAndCodeQR(qid, codeQR);
        BizStoreEntity bizStore = bizService.findByCodeQR(businessUserStore.getCodeQR());
        BizStoreEntity preferredBizStore = bizService.getByStoreId(preferredBizStoreId);
        return preferredBusinessService.exists(bizStore.getBizName().getId(), preferredBizStore.getBizName().getId());
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
        LOG.info("Found user associated to business count={} qid={}", size, qid);

        String[] codes = new String[queueLimit <= size ? queueLimit : size];
        Map<String, JsonDataVisibility> dataVisibilityHashMap = new HashMap<>();
        int i = 0;
        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            codes[i] = businessUserStore.getCodeQR();
            BizNameEntity bizName = bizService.getByBizNameId(businessUserStore.getBizNameId());
            dataVisibilityHashMap.put(businessUserStore.getCodeQR(), new JsonDataVisibility().setDataVisibilities(bizName.getDataVisibilities()));
            i++;
        }

        List<TokenQueueEntity> tokenQueues = tokenQueueService.getTokenQueue(codes);
        LOG.info("tokenQueues found count={} for queueLimit={}", tokenQueues.size(), queueLimit);
        List<JsonTopic> jsonTopics = new ArrayList<>();
        for (TokenQueueEntity tokenQueue : tokenQueues) {
            JsonHour jsonHour = getJsonHour(tokenQueue.getId());
            jsonTopics.add(
                new JsonTopic(tokenQueue)
                    .setHour(jsonHour)
                    .setJsonDataVisibility(dataVisibilityHashMap.get(tokenQueue.getId())));
        }

        LOG.info("Sending jsonTopic count={} for qid={}", jsonTopics.size(), qid);
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
            .setDayClosed(storeHour.isDayClosed() || storeHour.isTempDayClosed())
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

    /**
     * When Store Level is changed, updated the same across the board.
     * As of now, there is no support for same QID being used between different businesses.
     * Means, no two business will have same user as Supervisor or Manager.
     */
    public long changeUserLevel(String qid, UserLevelEnum changeToUserLevel, BusinessTypeEnum businessType) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
        if (userProfile.getLevel() == changeToUserLevel) {
            LOG.warn("Changing to same level qid={} level={}", qid, changeToUserLevel);
            return -1;
        }

        if (FAILURE == checkIfProfileIsOfAProfessional(qid, changeToUserLevel, businessType, userProfile.getLevel())) {
            LOG.info("Failed changing level, exiting {} level={} businessType={}", qid, changeToUserLevel, businessType);
            return 0;
        }
        userProfile.setLevel(changeToUserLevel);
        accountService.save(userProfile);

        long change = businessUserStoreManager.updateUserLevel(qid, changeToUserLevel);
        change = change + businessUserService.updateUserLevel(qid, changeToUserLevel);
        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(qid, changeToUserLevel);
        accountService.save(userAccount);
        return change;
    }

    /**
     * Before downgrading Manager/Doctor when business type is a doctor.
     * Check if Professional profile is empty. Otherwise throw error.
     */
    private CommonStatusEnum checkIfProfileIsOfAProfessional(String qid, UserLevelEnum changeToUserLevel, BusinessTypeEnum businessType, UserLevelEnum currentUserLevel) {
        if (BusinessTypeEnum.DO == businessType) {
            if (UserLevelEnum.S_MANAGER == currentUserLevel) {
                /* Down grading level from S_MANAGER. */
                return professionalProfileService.softDeleteProfessionalProfileProfile(qid);
            } else if (UserLevelEnum.S_MANAGER == changeToUserLevel) {
                /* Upgrading level to S_MANAGER. */
                UserAccountEntity userAccountEntity = accountService.findByQueueUserId(qid);
                if (!userAccountEntity.isPhoneValidated()) {
                    LOG.warn("Cannot upgrade to level for business={} when phone is not validated {}", businessType, qid);
                    return FAILURE;
                }

                professionalProfileService.createProfessionalProfile(qid);
                return SUCCESS;
            }
        }
        return SUCCESS;
    }

    private QueueSupervisor populateQueueSupervisorFromQid(String bizStoreId, String bizNameId, String qid) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
        UserAccountEntity userAccount = accountService.findByQueueUserId(qid);
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid, bizNameId);
        return populateQueueSupervisor(bizNameId, bizStoreId, businessUser, userProfile, userAccount);
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
            UserAccountEntity userAccount = accountService.findByQueueUserId(businessUser.getQueueUserId());
            queueSupervisors.add(populateQueueSupervisor(bizNameId, bizStoreId, businessUser, userProfile, userAccount));
        }

        /* Sort by name. */
        queueSupervisors.sort(comparing(QueueSupervisor::getName));
        return queueSupervisors;
    }

    private QueueSupervisor populateQueueSupervisor(
        String bizNameId,
        String bizStoreId,
        BusinessUserEntity businessUser,
        UserProfileEntity userProfile,
        UserAccountEntity userAccount
    ) {
        return new QueueSupervisor()
            .setBusinessUserId(businessUser.getId())
            .setStoreId(bizStoreId)
            .setBusinessId(bizNameId)
            .setName(userProfile.getName())
            .setPhone(userProfile.getPhone())
            .setPhoneValidated(userAccount.isPhoneValidated())
            .setCountryShortName(userProfile.getCountryShortName())
            .setAddress(userProfile.getAddress())
            .setEmail(userProfile.getEmail())
            .setQueueUserId(userProfile.getQueueUserId())
            .setUserLevel(userProfile.getLevel())
            .setCreated(businessUser.getCreated())
            .setActive(businessUser.isActive())
            .setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus());
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

    public List<BusinessUserStoreEntity> findAllManagingStoreWithUserLevel(String bizStoreId, UserLevelEnum userLevel) {
        return businessUserStoreManager.findAllManagingStoreWithUserLevel(bizStoreId, userLevel);
    }

    @Mobile
    public boolean hasAccessWithUserLevel(String qid, String codeQR, UserLevelEnum userLevel) {
        return businessUserStoreManager.hasAccessWithUserLevel(qid, codeQR, userLevel);
    }

    @Mobile
    public BusinessUserStoreEntity findOneByQidAndCodeQR(String qid, String codeQR) {
        return businessUserStoreManager.findOneByQidAndCodeQR(qid, codeQR);
    }

    @Television
    public BusinessUserStoreEntity findUserManagingStoreWithCodeQRAndUserLevel(String codeQR) {
        return businessUserStoreManager.findUserManagingStoreWithCodeQRAndUserLevel(codeQR, UserLevelEnum.S_MANAGER);
    }
}
