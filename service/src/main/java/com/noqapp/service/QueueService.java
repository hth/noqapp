package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.json.JsonQueueHistorical;
import com.noqapp.domain.json.JsonQueueHistoricalList;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedDependent;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.tv.JsonQueuedPersonTV;
import com.noqapp.domain.stats.HealthCareStat;
import com.noqapp.domain.stats.HealthCareStatList;
import com.noqapp.domain.stats.NewRepeatCustomers;
import com.noqapp.domain.stats.YearlyData;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/27/17 11:49 AM
 */
@Service
public class QueueService {
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private int limitedToDays;

    private UserProfileManager userProfileManager;
    private BusinessCustomerService businessCustomerService;
    private BizStoreManager bizStoreManager;
    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private TokenQueueService tokenQueueService;
    private BusinessUserStoreManager businessUserStoreManager;
    private StatsBizStoreDailyManager statsBizStoreDailyManager;

    @Autowired
    public QueueService(
        @Value("${limitedToDays:5}")
        int limitedToDays,

        UserProfileManager userProfileManager,
        BusinessCustomerService businessCustomerService,
        BizStoreManager bizStoreManager,
        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        TokenQueueService tokenQueueService,
        BusinessUserStoreManager businessUserStoreManager,
        StatsBizStoreDailyManager statsBizStoreDailyManager
    ) {
        this.limitedToDays = limitedToDays;

        this.userProfileManager = userProfileManager;
        this.businessCustomerService = businessCustomerService;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.tokenQueueService = tokenQueueService;
        this.businessUserStoreManager = businessUserStoreManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
    }

    @Mobile
    public List<QueueEntity> findAllQueuedByQid(String qid) {
        return queueManager.findAllQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findInAQueueByQid(String qid, String codeQR) {
        return queueManager.findInAQueueByQid(qid, codeQR);
    }

    @Mobile
    public List<QueueEntity> findInAQueueByQidWithAnyQueueState(String qid, String codeQR) {
        return queueManager.findInAQueueByQidWithAnyQueueState(qid, codeQR);
    }

    /** Get person with QID in a specific queue. Including when queued as guardian. */
    @Mobile
    public String getQueuedPerson(String qid, String codeQR) {
        List<QueueEntity> queues = findInAQueueByQid(qid, codeQR);
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();
        populateInJsonQueuePersonList(queuedPeople, queues);
        return new JsonQueuePersonList().setQueuedPeople(queuedPeople).asJson();
    }

    /** Get person in queue with any state. Including when queued as guardian. */
    @Mobile
    public String findThisPersonInQueue(String qid, String codeQR) {
        List<QueueEntity> queues = findInAQueueByQidWithAnyQueueState(qid, codeQR);
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();
        populateInJsonQueuePersonList(queuedPeople, queues);
        return new JsonQueuePersonList().setQueuedPeople(queuedPeople).asJson();
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByQid(String qid) {
        return queueManager.findAllNotQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByDid(String did) {
        return queueManager.findAllNotQueuedByDid(did);
    }

    @Deprecated
    public List<QueueEntity> getByQid(String qid) {
        return queueManagerJDBC.getByQid(qid);
    }

    private List<QueueEntity> getByQidSimple(String qid) {
        return queueManagerJDBC.getByQidSimple(qid);
    }

    @Mobile
    public List<QueueEntity> getByDid(String did) {
        return queueManagerJDBC.getByDid(did);
    }

    public List<QueueEntity> findAllHistoricalQueue(String qid) {
        List<QueueEntity> queues = findAllNotQueuedByQid(qid);
        queues.addAll(getByQidSimple(qid));
        return queues;
    }

    /** This is for historical queue placed today, other past queues that have moved to archive. */
    @Mobile
    public JsonQueueHistoricalList findAllHistoricalQueueAsJson(String qid) {
        UserProfileEntity userProfileOfGuardian = userProfileManager.findByQueueUserId(qid);
        List<UserProfileEntity> userProfileOfDependents = userProfileManager.findDependentProfilesByPhone(userProfileOfGuardian.getPhone());
        List<QueueEntity> queues = findAllHistoricalQueue(qid);
        for (UserProfileEntity userProfile : userProfileOfDependents) {
            queues.addAll(findAllHistoricalQueue(userProfile.getQueueUserId()));
        }

        /* Populated with data. */
        JsonQueueHistoricalList jsonQueueHistoricalList = new JsonQueueHistoricalList();
        for (QueueEntity queue : queues) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());
            JsonQueueHistorical jsonQueueHistorical = new JsonQueueHistorical(queue, bizStore);

            /* Set display image based on business type. */
            jsonQueueHistorical.setDisplayImage(CommonHelper.getBannerImage(bizStore));

            /* Set Category if any. */
            switch (queue.getBusinessType()) {
                case DO:
                    List<BusinessUserStoreEntity> businessUsers = businessUserStoreManager.findAllManagingStoreWithUserLevel(
                        bizStore.getId(),
                        UserLevelEnum.S_MANAGER);
                    if (!businessUsers.isEmpty()) {
                        BusinessUserStoreEntity businessUserStore = businessUsers.get(0);
                        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(businessUserStore.getQueueUserId());
                        jsonQueueHistorical.setDisplayImage(userProfile.getProfileImage());
                    }

                    jsonQueueHistorical.setBizCategoryName(MedicalDepartmentEnum.valueOf(bizStore.getBizCategoryId()).getDescription());
                    break;
                case BK:
                    jsonQueueHistorical.setBizCategoryName(BankDepartmentEnum.valueOf(bizStore.getBizCategoryId()).getDescription());
                    break;
                default:
                    //Do something for category
            }
            jsonQueueHistoricalList.addQueueHistorical(jsonQueueHistorical);
        }
        return jsonQueueHistoricalList;
    }

    public long deleteByCodeQR(String codeQR) {
        return queueManager.deleteByCodeQR(codeQR);
    }

    public long getPreviouslyVisitedClientCount(String codeQR) {
        return queueManager.previouslyVisitedClientCount(codeQR);
    }

    public long getNewVisitClientCount(String codeQR) {
        return queueManager.newVisitClientCount(codeQR);
    }

    public void addPhoneNumberToExistingQueue(int token, String codeQR, String did, String phone) {
        queueManager.addPhoneNumberToExistingQueue(token, codeQR, did, phone);
    }

    public QueueEntity findQueuedOne(String codeQR, String did, String qid) {
        return queueManager.findQueuedOne(codeQR, did, qid);
    }

    public QueueEntity findQueuedOneByQid(String codeQR, String qid) {
        return findQueuedOne(codeQR, null, qid);
    }

    public QueueEntity findOneByRecordReferenceId(String codeQR, String recordReferenceId) {
        return queueManager.findOneByRecordReferenceId(codeQR, recordReferenceId);
    }

    /** Finds clients who are yet to be serviced. */
    public JsonQueuePersonList findAllClientQueuedOrAborted(String codeQR) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManager.findAllClientQueuedOrAborted(codeQR);
        populateInJsonQueuePersonList(queuedPeople, queues);

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    /** Finds all clients in a queue. */
    @Mobile
    public JsonQueuePersonList findAllClient(String codeQR) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManager.findByCodeQR(codeQR);
        populateInJsonQueuePersonList(queuedPeople, queues);

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    @Mobile
    public JsonQueuePersonList findAllRegisteredClientHistorical(String codeQR) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManagerJDBC.getByCodeQRAndNotNullQID(codeQR, limitedToDays);
        populateInJsonQueuePersonList(queuedPeople, queues);

        for (JsonQueuedPerson jsonQueuedPerson : queuedPeople) {
            String qid = jsonQueuedPerson.getQueueUserId();
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            BusinessCustomerEntity businessCustomer = businessCustomerService.findOneByQid(qid, bizStore.getBizName().getId());
            jsonQueuedPerson.setCustomerName(userProfile.getName())
                .setBusinessCustomerId(businessCustomer == null ? "" : businessCustomer.getBusinessCustomerId())
                .setBusinessCustomerIdChangeCount(businessCustomer == null ? 0 : businessCustomer.getVersion())
                .setCustomerPhone(StringUtils.isNotBlank(userProfile.getGuardianPhone()) ? userProfile.getGuardianPhone() : userProfile.getPhone());
        }

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    private void populateInJsonQueuePersonList(List<JsonQueuedPerson> queuedPeople, List<QueueEntity> queues) {
        for (QueueEntity queue : queues) {
            JsonQueuedPerson jsonQueuedPerson = new JsonQueuedPerson()
                .setQueueUserId(queue.getQueueUserId())
                .setCustomerName(queue.getCustomerName())
                .setCustomerPhone(queue.getCustomerPhone())
                .setQueueUserState(queue.getQueueUserState())
                .setToken(queue.getTokenNumber())
                .setServerDeviceId(queue.getServerDeviceId())
                .setBusinessCustomerId(queue.getBusinessCustomerId())
                .setBusinessCustomerIdChangeCount(queue.getBusinessCustomerIdChangeCount())
                .setClientVisitedThisStore(queue.hasClientVisitedThisStore())
                .setClientVisitedThisBusiness(queue.hasClientVisitedThisBusiness())
                .setRecordReferenceId(queue.getRecordReferenceId())
                .setCreated(queue.getCreated());

            /* Get dependents when queue status is queued. */
            if (QueueUserStateEnum.Q == queue.getQueueUserState()) {
                if (StringUtils.isNotBlank(queue.getGuardianQid())) {
                    UserProfileEntity guardianProfile = userProfileManager.findByQueueUserId(queue.getGuardianQid());

                    for (String qid : guardianProfile.getQidOfDependents()) {
                        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
                        jsonQueuedPerson.addDependent(
                            new JsonQueuedDependent()
                                .setToken(queue.getTokenNumber())
                                .setQueueUserId(qid)
                                .setCustomerName(userProfile.getName())
                                .setGuardianPhone(queue.getCustomerPhone())
                                .setGuardianQueueUserId(queue.getQueueUserId())
                                .setQueueUserState(queue.getQueueUserState())
                                .setAge(userProfile.getAgeAsString())
                                .setGender(userProfile.getGender()));
                    }

                    /* Add Guardian at the end. */
                    jsonQueuedPerson.addDependent(
                        new JsonQueuedDependent()
                            .setToken(queue.getTokenNumber())
                            .setQueueUserId(guardianProfile.getQueueUserId())
                            .setCustomerName(guardianProfile.getName())
                            .setGuardianPhone(queue.getCustomerPhone())
                            .setGuardianQueueUserId(queue.getQueueUserId())
                            .setQueueUserState(queue.getQueueUserState())
                            .setAge(guardianProfile.getAgeAsString())
                            .setGender(guardianProfile.getGender()));
                }
            }

            queuedPeople.add(jsonQueuedPerson);
        }
    }

    private void populateInJsonQueuePersonTVList(List<JsonQueuedPersonTV> jsonQueuedPersonTVList, List<QueueEntity> queues) {
        for (QueueEntity queue : queues) {
            JsonQueuedPersonTV jsonQueuedPerson = new JsonQueuedPersonTV()
                .setQueueUserId(queue.getQueueUserId())
                .setCustomerName(CommonUtil.abbreviateName(queue.getCustomerName()))
                .setCustomerPhone(queue.getCustomerPhone())
                .setQueueUserState(queue.getQueueUserState())
                .setToken(queue.getTokenNumber());

            jsonQueuedPersonTVList.add(jsonQueuedPerson);
        }
    }

    /**
     * When merchant has served a specific token.
     *
     * @param codeQR
     * @param servedNumber
     * @param queueUserState
     * @param goTo           - counter name
     * @param sid            - server device id
     * @param tokenService   - Invoked via Web or Device
     * @return
     */
    public JsonToken updateAndGetNextInQueue(
        String codeQR,
        int servedNumber,
        QueueUserStateEnum queueUserState,
        String goTo,
        String sid,
        TokenServiceEnum tokenService
    ) {
        LOG.info("Update and getting next in queue codeQR={} servedNumber={} queueUserState={} goTo={} sid={}",
            codeQR, servedNumber, queueUserState, goTo, sid);

        QueueEntity queue = queueManager.updateAndGetNextInQueue(codeQR, servedNumber, queueUserState, goTo, sid, tokenService);
        if (null != queue) {
            LOG.info("Found queue codeQR={} servedNumber={} queueUserState={} nextToken={}",
                codeQR, servedNumber, queueUserState, queue.getTokenNumber());

            return tokenQueueService.updateServing(codeQR, QueueStatusEnum.N, queue.getTokenNumber(), goTo);
        }

        LOG.info("Reached condition of not having any more to serve");
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.D);
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            /* Better to show last number than served number. This is to maintain consistent state. */
            .setToken(tokenQueue.getCurrentlyServing())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.D);
    }

    /**
     * Merchant when pausing to serve queue.
     */
    @Mobile
    public JsonToken pauseServingQueue(
        String codeQR,
        int servedNumber,
        QueueUserStateEnum queueUserState,
        String sid,
        TokenServiceEnum tokenService
    ) {
        LOG.info("Server person is now pausing for queue codeQR={} servedNumber={} queueUserState={} sid={}",
            codeQR, servedNumber, queueUserState, sid);

        boolean status = queueManager.updateServedInQueue(codeQR, servedNumber, queueUserState, sid, tokenService);
        LOG.info("Paused status={}", status);
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setToken(tokenQueue.getLastNumber())
            .setServingNumber(servedNumber)
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.R);
    }

    /**
     * Merchant when starting or re-starting to serve token when QueueState has been either Start or Re-Start.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @return
     */
    @Mobile
    public JsonToken getNextInQueue(
        String codeQR,
        String goTo,
        String sid
    ) {
        LOG.info("Getting next in queue for codeQR={} goTo={} sid={}", codeQR, goTo, sid);

        QueueEntity queue = queueManager.getNext(codeQR, goTo, sid);
        if (null != queue) {
            LOG.info("Found queue codeQR={} token={}", codeQR, queue.getTokenNumber());

            JsonToken jsonToken = tokenQueueService.updateServing(
                codeQR,
                QueueStatusEnum.N,
                queue.getTokenNumber(),
                goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        /* When nothing is found, return DONE status for the queue. */
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        if (null != tokenQueue) {
            LOG.info("On next, found no one in queue, returning with DONE status");
            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setToken(tokenQueue.getLastNumber())
                .setServingNumber(tokenQueue.getLastNumber())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.D);
        }

        return null;
    }

    /**
     * Merchant when serving a specific token in queue. This is works for out of order request in queue.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @param token  specific token being requested for next executorService
     * @return
     */
    @Mobile
    public JsonToken getThisAsNextInQueue(
        String codeQR,
        String goTo,
        String sid,
        int token
    ) {
        LOG.info("Getting specific token next in queue for codeQR={} goTo={} sid={} token={}",
            codeQR,
            goTo,
            sid,
            token);

        QueueEntity queue = queueManager.getThisAsNext(codeQR, goTo, sid, token);
        if (null != queue) {
            LOG.info("Found queue codeQR={} token={}", codeQR, queue.getTokenNumber());

            JsonToken jsonToken = tokenQueueService.updateThisServing(
                codeQR,
                QueueStatusEnum.N,
                queue.getTokenNumber(),
                goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        return null;
    }

    public void updateServiceBeginTime(String id) {
        queueManager.updateServiceBeginTime(id);
    }

    private List<YearlyData> lastTwelveMonthVisits(String codeQR) {
        List<YearlyData> yearly = new ArrayList<>();
        List<StatsBizStoreDailyEntity> statsBizStoreDailies = statsBizStoreDailyManager.lastTwelveMonthVisits(codeQR);
        for (StatsBizStoreDailyEntity statsBizStoreDaily : statsBizStoreDailies) {
            yearly.add(new YearlyData()
                .setYearMonth(statsBizStoreDaily.getMonthOfYear())
                .setYear(statsBizStoreDaily.getYear())
                .setValue(statsBizStoreDaily.getTotalServiced()));
            LOG.debug("{} {} serviced={} codeQR={}",
                statsBizStoreDaily.getMonthOfYear(), statsBizStoreDaily.getYear(), statsBizStoreDaily.getTotalServiced(), codeQR);
        }

        return yearly;
    }

    private NewRepeatCustomers repeatAndNewCustomers(String codeQR) {
        StatsBizStoreDailyEntity statsBizStoreDaily = statsBizStoreDailyManager.repeatAndNewCustomers(codeQR);
        LOG.info("{} and new={} old={}", statsBizStoreDaily, statsBizStoreDaily.newClients(), statsBizStoreDaily.getClientsPreviouslyVisitedThisStore());
        return new NewRepeatCustomers()
            .setCustomerNew(statsBizStoreDaily.newClients())
            .setCustomerRepeat(statsBizStoreDaily.getClientsPreviouslyVisitedThisStore())
            .setMonthOfYear(statsBizStoreDaily.getMonthOfYear());
    }

    @Mobile
    public HealthCareStatList healthCareStats(String qid) {
        HealthCareStatList healthCareStatList = new HealthCareStatList();
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreManager.getQueues(qid, 0);

        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            healthCareStatList.addHealthCareStat(
                new HealthCareStat()
                    .setCodeQR(businessUserStore.getCodeQR())
                    .setRepeatCustomers(repeatAndNewCustomers(businessUserStore.getCodeQR()))
                    .setTwelveMonths(lastTwelveMonthVisits(businessUserStore.getCodeQR())));
        }

        return healthCareStatList;
    }

    @Mobile
    public boolean doesExistsByQid(String codeQR, int tokenNumber, String qid) {
        return queueManager.doesExistsByQid(codeQR, tokenNumber, qid);
    }

    @Mobile
    public QueueEntity changeUserInQueue(String codeQR, int tokenNumber, String existingQueueUserId, String changeToQueueUserId) {
        return queueManager.changeUserInQueue(codeQR, tokenNumber, existingQueueUserId, changeToQueueUserId);
    }

    @Mobile
    public JsonQueuePersonList findYetToBeServed(String codeQR) {
        List<QueueEntity> queues = queueManager.findYetToBeServed(codeQR);
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();
        populateInJsonQueuePersonList(queuedPeople, queues);
        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    @Mobile
    public List<JsonQueuedPersonTV> findYetToBeServedForTV(String codeQR) {
        List<QueueEntity> queues = queueManager.findYetToBeServed(codeQR);
        List<JsonQueuedPersonTV> jsonQueuedPersonTVList = new ArrayList<>();
        populateInJsonQueuePersonTVList(jsonQueuedPersonTVList, queues);
        return jsonQueuedPersonTVList;
    }
}
