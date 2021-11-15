package com.noqapp.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonQueue;
import com.noqapp.domain.json.JsonQueueHistorical;
import com.noqapp.domain.json.JsonQueueHistoricalList;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedDependent;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.JsonTokenAndQueue;
import com.noqapp.domain.json.JsonTokenAndQueueList;
import com.noqapp.domain.json.tv.JsonQueuedPersonTV;
import com.noqapp.domain.stats.HealthCareStat;
import com.noqapp.domain.stats.HealthCareStatList;
import com.noqapp.domain.stats.NewRepeatCustomers;
import com.noqapp.domain.stats.YearlyData;
import com.noqapp.domain.types.BusinessCustomerAttributeEnum;
import com.noqapp.domain.types.CustomerPriorityLevelEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.exceptions.StoreNoLongerExistsException;
import com.noqapp.service.utils.ServiceUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 8/27/17 11:49 AM
 */
@Service
public class QueueService {
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private UserProfileManager userProfileManager;
    private BusinessCustomerService businessCustomerService;
    private BizStoreManager bizStoreManager;
    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private TokenQueueService tokenQueueService;
    private BusinessUserStoreManager businessUserStoreManager;
    private StatsBizStoreDailyManager statsBizStoreDailyManager;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private PurchaseOrderProductService purchaseOrderProductService;
    private StoreHourService storeHourService;
    private CouponService couponService;

    @Autowired
    public QueueService(
        UserProfileManager userProfileManager,
        BizStoreManager bizStoreManager,
        QueueManager queueManager,
        QueueManagerJDBC queueManagerJDBC,
        BusinessUserStoreManager businessUserStoreManager,
        StatsBizStoreDailyManager statsBizStoreDailyManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,

        BusinessCustomerService businessCustomerService,
        TokenQueueService tokenQueueService,
        PurchaseOrderProductService purchaseOrderProductService,
        StoreHourService storeHourService,
        CouponService couponService
    ) {
        this.userProfileManager = userProfileManager;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.businessUserStoreManager = businessUserStoreManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;

        this.businessCustomerService = businessCustomerService;
        this.tokenQueueService = tokenQueueService;
        this.purchaseOrderProductService = purchaseOrderProductService;
        this.storeHourService = storeHourService;
        this.couponService = couponService;
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

    /** All queue placed today, other historical past queues that have moved to archive. */
    public List<QueueEntity> findAllHistoricalQueue(String qid) {
        List<QueueEntity> queues = findAllNotQueuedByQid(qid);
        queues.addAll(getByQidSimple(qid));
        return queues;
    }

    /** This is for queue placed today, other historical past queues that have moved to archive. */
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
            try {
                BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());

                /* Find any orders if available. */
                JsonPurchaseOrder jsonPurchaseOrder = null;
                if (StringUtils.isNotBlank(queue.getTransactionId())) {
                    PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(queue.getTransactionId());
                    if (null == purchaseOrder) {
                        purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(queue.getQueueUserId(), queue.getTransactionId());
                        if (null == purchaseOrder) {
                            //TODO check when purchaseOrder is null
                            LOG.error("Failed finding purchaseOrder={} displayName={} qid={} token={} date={}",
                                queue.getTransactionId(),
                                queue.getDisplayName(),
                                queue.getQueueUserId(),
                                queue.getTokenNumber(),
                                queue.getCreated());

                            jsonPurchaseOrder = new JsonPurchaseOrder();
                        } else {
                            jsonPurchaseOrder = purchaseOrderProductService.populateHistoricalJsonPurchaseOrder(purchaseOrder);
                        }
                    } else {
                        jsonPurchaseOrder = purchaseOrderProductService.populateJsonPurchaseOrder(purchaseOrder);
                    }
                }

                JsonQueueHistorical jsonQueueHistorical = new JsonQueueHistorical(queue, bizStore, jsonPurchaseOrder);

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
            } catch (Exception e) {
                //TODO This error should not be happening. Cause needs to be investigated. For now its on Sandbox but followup on Live.
                LOG.error("Failed populating from queue with transactionId={} id={} {}",
                    queue.getTransactionId(),
                    queue.getId(),
                    e.getLocalizedMessage(),
                    e);
            }
        }
        LOG.info("Queue history size {}", jsonQueueHistoricalList.getQueueHistoricals().size());
        return jsonQueueHistoricalList;
    }

    long deleteByCodeQR(String codeQR) {
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

    @Mobile
    public QueueEntity findOneWithoutState(String qid, String codeQR) {
        return queueManager.findOneWithoutState(qid, codeQR);
    }

    public QueueEntity findQueuedOne(String codeQR, String did, String qid) {
        return queueManager.findQueuedOne(codeQR, did, qid);
    }

    @Mobile
    public QueueEntity findQueuedOneByQid(String codeQR, String qid) {
        return findQueuedOne(codeQR, null, qid);
    }

    public QueueEntity findOneByRecordReferenceId(String codeQR, String recordReferenceId) {
        return queueManager.findOneByRecordReferenceId(codeQR, recordReferenceId);
    }

    public QueueEntity findOneHistoricalByRecordReferenceId(String codeQR, String recordReferenceId) {
        return queueManagerJDBC.findOneHistoricalByRecordReferenceId(codeQR, recordReferenceId);
    }

    /** Finds clients who are yet to be serviced. */
    public JsonQueuePersonList findAllClientQueuedOrAborted(String codeQR) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManager.findAllClientQueuedOrAborted(codeQR);
        populateInJsonQueuePersonList(queuedPeople, queues);

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    /** Finds historical clients. */
    public JsonQueuePersonList getByCodeQR(String codeQR, int limitedToDays) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManagerJDBC.getByCodeQR(codeQR, limitedToDays);
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
    public JsonQueuePersonList findAllRegisteredClientHistorical(String codeQR, Date start, Date until) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManagerJDBC.getByCodeQRDateRangeAndNotNullQID(codeQR, start, until);
        populateInJsonQueuePersonList(queuedPeople, queues);
        populateHistoricallyQueuePeople(codeQR, queuedPeople);
        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    private void populateHistoricallyQueuePeople(String codeQR, List<JsonQueuedPerson> queuedPeople) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        for (JsonQueuedPerson jsonQueuedPerson : queuedPeople) {
            String qid = jsonQueuedPerson.getQueueUserId();
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
            BusinessCustomerEntity businessCustomer = businessCustomerService.findOneByQid(qid, bizStore.getBizName().getId());
            jsonQueuedPerson.setCustomerName(userProfile.getName())
                .setBusinessCustomerId(businessCustomer == null ? "" : businessCustomer.getBusinessCustomerId())
                .setBusinessCustomerIdChangeCount(businessCustomer == null ? 0 : businessCustomer.getVersion())
                .setCustomerPhone(StringUtils.isNotBlank(userProfile.getGuardianPhone()) ? userProfile.getGuardianPhone() : userProfile.getPhone());
        }
    }

    private void populateInJsonQueuePersonList(List<JsonQueuedPerson> queuedPeople, List<QueueEntity> queues) {
        for (QueueEntity queue : queues) {
            try {
                queuedPeople.add(getJsonQueuedPerson(queue));
            } catch (Exception e) {
                LOG.error("Failed populating from queue with transactionId={} id={} {}", queue.getTransactionId(), queue.getId(), e.getLocalizedMessage());
            }
        }
    }

    @Mobile
    public JsonQueuedPerson getJsonQueuedPerson(QueueEntity queue) {
        try {
            JsonQueuedPerson jsonQueuedPerson = new JsonQueuedPerson()
                .setQueueUserId(queue.getQueueUserId())
                .setCustomerName(queue.getCustomerName())
                .setCustomerPhone(queue.getCustomerPhone())
                .setQueueUserState(queue.getQueueUserState())
                .setToken(queue.getTokenNumber())
                .setDisplayToken(queue.getDisplayToken())
                .setServerDeviceId(queue.getServerDeviceId())
                .setBusinessCustomerId(queue.getBusinessCustomerId())
                .setBusinessCustomerIdChangeCount(queue.getBusinessCustomerIdChangeCount())
                .setClientVisitedThisStore(queue.hasClientVisitedThisStore())
                .setClientVisitedThisStoreDate(queue.getClientVisitedThisStoreDate())
                .setClientVisitedThisBusiness(queue.hasClientVisitedThisBusiness())
                .setRecordReferenceId(queue.getRecordReferenceId())
                .setCreated(queue.getCreated())
                .setTransactionId(queue.getTransactionId())
                .setTimeSlotMessage(queue.getTimeSlotMessage())
                .setRecordReferenceId(queue.getRecordReferenceId())
                .setCodeQR(queue.getCodeQR())
                .setDisplayName(queue.getDisplayName())
                .setCustomerPriorityLevel(queue.getCustomerPriorityLevel())
                .setBusinessCustomerAttributes(queue.getBusinessCustomerAttributes());

            if (StringUtils.isNotBlank(queue.getTransactionId())) {
                JsonPurchaseOrder jsonPurchaseOrder;
                PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(queue.getTransactionId());
                if (null == purchaseOrder) {
                    purchaseOrder = purchaseOrderManagerJDBC.findOrderByTransactionId(queue.getTransactionId());
                    jsonPurchaseOrder = purchaseOrderProductService.populateHistoricalJsonPurchaseOrder(purchaseOrder);
                } else {
                    jsonPurchaseOrder = purchaseOrderProductService.populateJsonPurchaseOrder(purchaseOrder);
                }
                jsonQueuedPerson.setJsonPurchaseOrder(jsonPurchaseOrder);
            }

            /* Get dependents when queue status is queued. */
            if (QueueUserStateEnum.Q == queue.getQueueUserState()) {
                if (StringUtils.isNotBlank(queue.getGuardianQid())) {
                    UserProfileEntity guardianProfile = userProfileManager.findByQueueUserId(queue.getGuardianQid());

                    for (String qid : guardianProfile.getQidOfDependents()) {
                        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
                        jsonQueuedPerson.addDependent(
                            new JsonQueuedDependent()
                                .setToken(queue.getTokenNumber())
                                .setDisplayToken(queue.getDisplayToken())
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
                            .setDisplayToken(queue.getDisplayToken())
                            .setQueueUserId(guardianProfile.getQueueUserId())
                            .setCustomerName(guardianProfile.getName())
                            .setGuardianPhone(queue.getCustomerPhone())
                            .setGuardianQueueUserId(queue.getQueueUserId())
                            .setQueueUserState(queue.getQueueUserState())
                            .setAge(guardianProfile.getAgeAsString())
                            .setGender(guardianProfile.getGender()));
                }
            }
            return jsonQueuedPerson;
        } catch (Exception e) {
            //TODO This error should not be happening. Cause needs to be investigated. For now its on Sandbox but followup on Live.
            LOG.error("Failed populating from queue with transactionId={} id={} {}", queue.getTransactionId(), queue.getId(), e.getLocalizedMessage(), e);
            throw e;
        }
    }

    private void populateInJsonQueuePersonTVList(List<JsonQueuedPersonTV> jsonQueuedPersonTVList, List<QueueEntity> queues) {
        for (QueueEntity queue : queues) {
            JsonQueuedPersonTV jsonQueuedPerson = new JsonQueuedPersonTV()
                .setQueueUserId(queue.getQueueUserId())
                .setCustomerName(CommonUtil.abbreviateName(queue.getCustomerName()))
                .setCustomerPhone(queue.getCustomerPhone())
                .setQueueUserState(queue.getQueueUserState())
                .setToken(queue.getTokenNumber())
                .setDisplayToken(queue.getDisplayToken());

            jsonQueuedPersonTVList.add(jsonQueuedPerson);
        }
    }

    /**
     * When business has served a specific token.
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
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.D);
    }

    /**
     * Business when pausing to serve queue.
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
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setServingNumber(servedNumber)
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(QueueStatusEnum.R);
    }

    /**
     * Business when starting or re-starting to serve token when QueueState has been either Start or Re-Start.
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
                .setDisplayToken(tokenQueue.generateDisplayToken())
                .setServingNumber(tokenQueue.getLastNumber())
                .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.D);
        }

        return null;
    }

    /**
     * Business when serving a specific token in queue. This is works for out of order request in queue.
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
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(businessUserStore.getCodeQR());
            healthCareStatList.addHealthCareStat(
                new HealthCareStat()
                    .setCodeQR(businessUserStore.getCodeQR())
                    .setProductPrice(bizStore.getProductPrice())
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
    public List<JsonQueuedPersonTV> findYetToBeServedForTV(String codeQR) {
        List<QueueEntity> queues = queueManager.findYetToBeServed(codeQR);
        List<JsonQueuedPersonTV> jsonQueuedPersonTVList = new ArrayList<>();
        populateInJsonQueuePersonTVList(jsonQueuedPersonTVList, queues);
        return jsonQueuedPersonTVList;
    }

    @Mobile
    public QueueEntity findByTransactionId(String codeQR, String transactionId, String qid) {
        return queueManager.findByTransactionId(codeQR, transactionId, qid);
    }

    @Mobile
    public void updateCustomerPriorityAndCustomerAttributes(
        String qid,
        String codeQR,
        int tokenNumber,
        CustomerPriorityLevelEnum customerPriorityLevel,
        BusinessCustomerAttributeEnum businessCustomerAttribute
    ) {
        queueManager.updateCustomerPriorityAndCustomerAttributes(qid, codeQR, tokenNumber, customerPriorityLevel, businessCustomerAttribute);
    }

    public int countDistinctQIDsInBiz(String bizNameId, int limitedToDays) {
        return queueManagerJDBC.countDistinctQIDsInBiz(bizNameId, limitedToDays);
    }

    public List<String> distinctQIDsInBiz(String bizNameId, int limitedToDays) {
        return queueManagerJDBC.distinctQIDsInBiz(bizNameId, limitedToDays);
    }

    public JsonTokenAndQueueList findAllJoinedQueues(String qid, String did) {
        Validate.isValidQid(qid);
        List<QueueEntity> queues = findAllQueuedByQid(qid);
        LOG.info("Currently joined queue size={} qid={} did={}", queues.size(), qid, did);
        return populateJsonTokenAndQueue(queues);
    }

    public JsonTokenAndQueueList populateJsonTokenAndQueue(List<QueueEntity> queues) {
        List<JsonTokenAndQueue> jsonTokenAndQueues = new ArrayList<>();
        for (QueueEntity queue : queues) {
            validateJoinedQueue(queue);

            /*
             * Join Queue will join if user is not joined, hence fetch only queues with status is Queued.
             * Since we are fetching only queues that are joined, we can send
             * averageServiceTime as zero, and
             * tokenService as null, and
             * guardianQid as null too.
             */
            //JsonToken jsonToken = tokenQueueMobileService.joinQueue(queue.getCodeQR(), did, qid, queue.getGuardianQid(), 0, null);
            JsonQueue jsonQueue = findTokenState(queue.getCodeQR());

            /* Override the creation date of TokenAndQueue. This date helps in sorting of client side to show active queue. */
            jsonQueue.setCreated(queue.getCreated());

            JsonPurchaseOrder jsonPurchaseOrder = null;
            if (StringUtils.isNotBlank(queue.getTransactionId())) {
                PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(queue.getTransactionId());
                jsonPurchaseOrder = purchaseOrderProductService.populateJsonPurchaseOrder(purchaseOrder);
                couponService.addCouponInformationIfAny(jsonPurchaseOrder);
            }

            JsonTokenAndQueue jsonTokenAndQueue = new JsonTokenAndQueue(
                queue.getTokenNumber(),
                queue.getDisplayToken(),
                queue.getQueueUserId(),
                tokenQueueService.findByCodeQR(queue.getCodeQR()).getQueueStatus(),
                jsonQueue,
                jsonPurchaseOrder);
            jsonTokenAndQueues.add(jsonTokenAndQueue);
        }

        JsonTokenAndQueueList jsonTokenAndQueueList = new JsonTokenAndQueueList();
        jsonTokenAndQueueList.setTokenAndQueues(jsonTokenAndQueues);
        LOG.info("Current tokenAndQueueSize={}", jsonTokenAndQueueList.getTokenAndQueues().size());
        return jsonTokenAndQueueList;
    }

    public void validateJoinedQueue(QueueEntity queue) {
        switch (queue.getQueueUserState()) {
            case A:
            case S:
            case N:
                LOG.error("Failed as only Q status is supported");
                throw new UnsupportedOperationException("Reached not supported condition");
        }
    }

    public JsonQueue findTokenState(String codeQR) {
        try {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
            if (bizStore.isDeleted()) {
                LOG.info("Store has been deleted id={} displayName=\"{}\"", bizStore.getId(), bizStore.getDisplayName());
                throw new StoreNoLongerExistsException("Store no longer exists");
            }

            StoreHourEntity storeHour = storeHourService.getStoreHours(codeQR, bizStore);
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
            LOG.info("TokenState bizStore=\"{}\" businessType={} averageServiceTime={} tokenQueue={}",
                bizStore.getBizName().getBusinessName(),
                bizStore.getBusinessType().name(),
                bizStore.getAverageServiceTime(),
                tokenQueue.getCurrentlyServing());

            return getJsonQueue(bizStore, storeHour, tokenQueue);
        } catch (StoreNoLongerExistsException e) {
            throw e;
        } catch (Exception e) {
            //TODO remove this catch
            LOG.error("Failed getting state codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Store Service Image and Store Interior Image are as is. Its not being appended with code QR like
     * for BizStoreElastic.
     *
     * @param bizStore
     * @param storeHour
     * @param tokenQueue
     * @return
     */
    public JsonQueue getJsonQueue(BizStoreEntity bizStore, StoreHourEntity storeHour, TokenQueueEntity tokenQueue) {
        JsonQueue jsonQueue = new JsonQueue(bizStore.getId(), bizStore.getCodeQR())
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setDisplayName(bizStore.getDisplayName())
            .setBusinessType(bizStore.getBusinessType())
            .setStoreAddress(bizStore.getAddress())
            .setArea(bizStore.getArea())
            .setTown(bizStore.getTown())
            .setCountryShortName(bizStore.getCountryShortName())
            .setStorePhone(bizStore.getPhoneFormatted())
            .setRating(bizStore.getRating())
            .setReviewCount(bizStore.getReviewCount())
            .setAverageServiceTime(bizStore.getAverageServiceTime())
            .setLimitServiceByDays(bizStore.getBizName().getLimitServiceByDays())
            .setPriorityAccess(bizStore.getBizName().getPriorityAccess())
            .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
            .setStartHour(storeHour.getStartHour())
            .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
            .setEndHour(storeHour.getEndHour())
            .setLunchTimeStart(storeHour.getLunchTimeStart())
            .setLunchTimeEnd(storeHour.getLunchTimeEnd())
            .setDelayedInMinutes(storeHour.getDelayedInMinutes())
            .setPreventJoining(storeHour.isPreventJoining())
            .setDayClosed(bizStore.getBizName().isDayClosed() || storeHour.isDayClosed() || storeHour.isTempDayClosed())
            .setTopic(bizStore.getTopic())
            .setGeoHash(bizStore.getGeoPoint().getGeohash())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayServingNumber(tokenQueue.generateDisplayServingNow())
            .setDisplayToken(tokenQueue.generateDisplayToken())
            .setLastNumber(tokenQueue.getLastNumber())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setCreated(tokenQueue.getCreated())
            .setRemoteJoinAvailable(bizStore.isRemoteJoin())
            .setAllowLoggedInUser(bizStore.isAllowLoggedInUser())
            .setAvailableTokenCount(bizStore.getAvailableTokenCount())
            .setAvailableTokenAfterCancellation(bizStore.getAvailableTokenAfterCancellation())
            .setEnabledPayment(bizStore.isEnabledPayment())
            .setProductPrice(bizStore.getProductPrice())
            .setCancellationPrice(bizStore.getCancellationPrice())
            .setBizCategoryId(bizStore.getBizCategoryId())
            .setFamousFor(bizStore.getFamousFor())
            .setDiscount(bizStore.getDiscount())
            .setMinimumDeliveryOrder(bizStore.getMinimumDeliveryOrder())
            .setDeliveryRange(bizStore.getDeliveryRange())
            .setStoreServiceImages(bizStore.getStoreServiceImages())
            .setStoreInteriorImages(bizStore.getStoreInteriorImages())
            .setAmenities(bizStore.getAmenities())
            .setFacilities(bizStore.getFacilities())
            .setAcceptedPayments(bizStore.getAcceptedPayments())
            .setAcceptedDeliveries(bizStore.getAcceptedDeliveries());

        String timeSlotMessage;
        switch (bizStore.getBusinessType()) {
            case CD:
            case CDQ:
                jsonQueue.setStoreAddress(FileUtil.DASH);
                jsonQueue.setStorePhone(FileUtil.DASH);

                if (bizStore.getAvailableTokenCount() > 0) {
                    timeSlotMessage = ServiceUtils.expectedService(
                        bizStore,
                        storeHour,
                        tokenQueue.getLastNumber() - bizStore.getAvailableTokenAfterCancellation());
                } else {
                    timeSlotMessage = ServiceUtils.calculateEstimatedWaitTime(
                        bizStore.getAverageServiceTime(),
                        tokenQueue.getLastNumber() - tokenQueue.getCurrentlyServing(),
                        tokenQueue.getQueueStatus(),
                        storeHour.getStartHour(),
                        bizStore.getTimeZone());
                }
                break;
            default:
                timeSlotMessage = ServiceUtils.calculateEstimatedWaitTime(
                    bizStore.getAverageServiceTime(),
                    tokenQueue.getLastNumber() - tokenQueue.getCurrentlyServing(),
                    tokenQueue.getQueueStatus(),
                    storeHour.getStartHour(),
                    bizStore.getTimeZone());
        }
        jsonQueue.setTimeSlotMessage(timeSlotMessage == null ? "Not Available" : timeSlotMessage);
        LOG.info("Sending timeSlotMessage={}", jsonQueue.getTimeSlotMessage());
        return jsonQueue;
    }
}
