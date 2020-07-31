package com.noqapp.service;

import static com.noqapp.common.utils.Constants.PREVENT_JOINING_BEFORE_CLOSING;
import static com.noqapp.domain.BizStoreEntity.UNDER_SCORE;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.GetTimeAgoUtils;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessCustomerEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.json.fcm.JsonMessage;
import com.noqapp.domain.json.fcm.data.JsonData;
import com.noqapp.domain.json.fcm.data.JsonTopicData;
import com.noqapp.domain.json.fcm.data.speech.JsonTextToSpeech;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeviceTypeEnum;
import com.noqapp.domain.types.FirebaseMessageTypeEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.StoreHourManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.exceptions.ExpectedServiceBeyondStoreClosingHour;
import com.noqapp.service.utils.ServiceUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 12/16/16 9:42 AM
 */
@Service
public class TokenQueueService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenQueueService.class);

    private TokenQueueManager tokenQueueManager;
    private FirebaseMessageService firebaseMessageService;
    private QueueManager queueManager;
    private AccountService accountService;
    private RegisteredDeviceManager registeredDeviceManager;
    private QueueManagerJDBC queueManagerJDBC;
    private StoreHourManager storeHourManager;
    private BizStoreManager bizStoreManager;
    private BusinessCustomerService businessCustomerService;
    private TextToSpeechService textToSpeechService;
    private ApiHealthService apiHealthService;

    private ExecutorService executorService;

    @Autowired
    public TokenQueueService(
        TokenQueueManager tokenQueueManager,
        FirebaseMessageService firebaseMessageService,
        QueueManager queueManager,
        AccountService accountService,
        RegisteredDeviceManager registeredDeviceManager,
        QueueManagerJDBC queueManagerJDBC,
        StoreHourManager storeHourManager,
        BizStoreManager bizStoreManager,
        BusinessCustomerService businessCustomerService,
        TextToSpeechService textToSpeechService,
        ApiHealthService apiHealthService
    ) {
        this.tokenQueueManager = tokenQueueManager;
        this.firebaseMessageService = firebaseMessageService;
        this.queueManager = queueManager;
        this.accountService = accountService;
        this.registeredDeviceManager = registeredDeviceManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.storeHourManager = storeHourManager;
        this.bizStoreManager = bizStoreManager;
        this.businessCustomerService = businessCustomerService;
        this.textToSpeechService = textToSpeechService;
        this.apiHealthService = apiHealthService;

        this.executorService = newCachedThreadPool();
    }

    //TODO has to createUpdate by cron job
    public void createUpdate(BizStoreEntity bizStore) {
        String codeQR = bizStore.getCodeQR();
        String topic = bizStore.getTopic();
        String displayName = bizStore.getDisplayName();
        BusinessTypeEnum businessType = bizStore.getBusinessType();
        String bizCategoryId = bizStore.getBizCategoryId();
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        try {
            LOG.info("Create/Update id={} {} displayName={} businessType={}", codeQR, topic, displayName, businessType);
            Assertions.assertTrue(topic.endsWith(codeQR), "Topic and CodeQR should match significantly");
            TokenQueueEntity token = findByCodeQR(codeQR);
            if (null == token) {
                token = new TokenQueueEntity(topic, displayName, businessType, bizCategoryId);
                token.setId(codeQR);
                tokenQueueManager.save(token);
            } else {
                boolean updateSuccess = tokenQueueManager.updateDisplayNameAndBusinessType(
                    codeQR,
                    topic,
                    displayName,
                    businessType,
                    bizCategoryId);

                if (!updateSuccess) {
                    LOG.error("Failed update for codeQR={} topic={} displayName={}", codeQR, topic, displayName);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed creating TokenQueue codeQR={} topic={} displayName={}", codeQR, topic, displayName);
            methodStatusSuccess = false;
            throw new RuntimeException("Failed creating TokenQueue");
        } finally {
            apiHealthService.insert(
                    "createUpdate",
                    "createUpdate",
                    TokenQueueService.class.getName(),
                    Duration.between(start, Instant.now()),
                    methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }

    public TokenQueueEntity findByCodeQR(String codeQR) {
        return tokenQueueManager.findByCodeQR(codeQR);
    }

    public TokenQueueEntity getNextToken(String codeQR, int availableTokenCount) {
        return tokenQueueManager.getNextToken(codeQR, availableTokenCount);
    }

    public void deleteHard(TokenQueueEntity tokenQueue) {
        tokenQueueManager.deleteHard(tokenQueue);
    }

    /**
     * Gets new token or reloads existing token if previously registered.
     * This process adds the user to queue. Invokes broadcast.
     */
    @Mobile
    public JsonToken getNextToken(
        String codeQR,
        String did,
        String qid,
        String guardianQid,
        long averageServiceTime,
        TokenServiceEnum tokenService
    ) {
        return getNextToken(codeQR, did, qid, guardianQid, averageServiceTime, tokenService, null);
    }

    /**
     * Gets new token or reloads existing token if previously registered.
     * This process adds the user to queue. Invokes broadcast.
     */
    @Mobile
    public JsonToken getNextToken(
        String codeQR,
        String did,
        String qid,
        String guardianQid,
        long averageServiceTime,
        TokenServiceEnum tokenService,
        UserLevelEnum userLevel
    ) {
        try {
            QueueEntity queue = queueManager.findQueuedOne(codeQR, did, qid);

            /* When not Queued or has been serviced which will not show anyway in the above query, get a new token. */
            if (null == queue) {
                /*
                 * Find storeHour early, helps prevent issuing token when queue is closed or due to some obstruction.
                 * To eliminate this, we need to let business know about queue closed and prevent clients from joining.
                 */
                BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
                /* This condition exists only for non paid system. */
                if (0 < bizStore.getBizName().getLimitServiceByDays()
                    && StringUtils.isNotBlank(qid)
                    && queueManagerJDBC.hasServicedInPastXDays(codeQR, qid, bizStore.getBizName().getLimitServiceByDays())) {
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.X)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
                DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
                StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);

                if (!bizStore.isActive() || storeHour.isDayClosed() || storeHour.isTempDayClosed() || storeHour.isPreventJoining()) {
                    LOG.warn("When queue closed or prevent joining, attempting to create new token");
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                            .setToken(0)
                            .setServingNumber(0)
                            .setDisplayName(bizStore.getDisplayName())
                            .setQueueStatus(QueueStatusEnum.C)
                            .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                int requesterTime = DateFormatter.getTimeIn24HourFormat(LocalTime.now(zoneId));
                int tokenFrom = storeHour.getTokenAvailableFrom();
                if (requesterTime < tokenFrom) {
                    //Might need to add condition || requesterTime > storeHour.getEndHour() to prevent users from taking token after hours.
                    //This should be prevented on mobile front.
                    LOG.warn("Requester time qid={} tokenFrom={} requesterTime={} codeQR={}", qid, tokenFrom, requesterTime, codeQR);
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.B)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                } else if (requesterTime > storeHour.getEndHour()) {
                    LOG.error("Requester attempted token after close time qid={} tokenFrom={} requesterTime={} codeQR={}",
                        qid, tokenFrom, requesterTime, codeQR);
                }

                /* This code finds if there is an existing token issued to the  user. */
                switch (bizStore.getBusinessType()) {
                    case CD:
                    case CDQ:
                        if (StringUtils.isNotBlank(qid)) {
                            queue = queueManager.findOneWithoutState(qid, codeQR);
                            if (null != queue) {
                                switch (queue.getQueueUserState()) {
                                    case A:
                                        queue.setQueueUserState(QueueUserStateEnum.Q);
                                        queue.active();
                                        queueManager.save(queue);
                                        TokenQueueEntity tokenQueue = tokenQueueManager.findByCodeQR(codeQR);
                                        doActionBasedOnQueueStatus(codeQR, tokenQueue);
                                        return getJsonToken(codeQR, queue, tokenQueue);
                                    case N:
                                    case S:
                                    default:
                                        /* Person already served or skipped. */
                                        return new JsonToken(codeQR, bizStore.getBusinessType())
                                            .setToken(0)
                                            .setServingNumber(0)
                                            .setDisplayName(bizStore.getDisplayName())
                                            .setQueueStatus(QueueStatusEnum.T)
                                            .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC())
                                            .setTimeSlotMessage(queue.getTimeSlotMessage());
                                }
                            }
                        }
                    default:
                        //Do nothing
                }

                Assertions.assertNotNull(tokenService, "TokenService cannot be null to generate new token");
                TokenQueueEntity tokenQueue;
                if (userLevel == UserLevelEnum.S_MANAGER) {
                    tokenQueue = getNextToken(codeQR, 0);
                } else {
                    tokenQueue = getNextToken(codeQR, bizStore.getAvailableTokenCount());
                }
                if (tokenQueue == null && bizStore.getAvailableTokenCount() > 0) {
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.L)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }
                LOG.info("Assigned to queue with codeQR={} with new token={}", codeQR, tokenQueue.getLastNumber());

                doActionBasedOnQueueStatus(codeQR, tokenQueue);
                try {
                    queue = new QueueEntity(codeQR, did, tokenService, qid, tokenQueue.getLastNumber(), tokenQueue.getDisplayName(), tokenQueue.getBusinessType());
                    if (StringUtils.isNotBlank(guardianQid)) {
                        /* Set this field when client is really a guardian and has at least one dependent in profile. */
                        queue.setGuardianQid(guardianQid);
                    }

                    /* For limited token. */
                    if (bizStore.getAvailableTokenCount() > 0) {
                        Date expectedServiceBegin = computeExpectedServiceBeginTime(averageServiceTime, zoneId, storeHour, tokenQueue);
                        queue.setExpectedServiceBegin(expectedServiceBegin)
                            .setBizNameId(bizStore.getBizName().getId())
                            .setTimeSlotMessage(ServiceUtils.timeSlot(expectedServiceBegin, bizStore.getTimeZone(), storeHour));
                    } else {
                        queue.setBizNameId(bizStore.getBizName().getId());
                    }
                    queueManager.insert(queue);
                    updateQueueWithUserDetail(codeQR, qid, queue);
                } catch (DuplicateKeyException e) {
                    LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                    return new JsonToken(codeQR, tokenQueue.getBusinessType());
                } catch (ExpectedServiceBeyondStoreClosingHour e) {
                    LOG.warn("Error serving to queue did={} qid={} codeQR={} reason={}", did, qid, codeQR, e.getLocalizedMessage());
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.A)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                return getJsonToken(codeQR, queue, tokenQueue);
            }

            TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} qid={} did={} queueStatus={}",
                queue.getTokenNumber(), tokenQueue.getTopic(), qid, did, tokenQueue.getQueueStatus());

            doActionBasedOnQueueStatus(codeQR, tokenQueue);
            return getJsonToken(codeQR, queue, tokenQueue);
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    private JsonToken getJsonToken(String codeQR, QueueEntity queue, TokenQueueEntity tokenQueue) {
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setToken(queue.getTokenNumber())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setExpectedServiceBegin(queue.getExpectedServiceBegin())
            .setTimeSlotMessage(queue.getTimeSlotMessage());
    }

    @Mobile
    public JsonToken getPaidNextToken(
        String codeQR,
        String did,
        String qid,
        String guardianQid,
        long averageServiceTime,
        TokenServiceEnum tokenService
    ) {
        try {
            QueueEntity queue = queueManager.findQueuedOne(codeQR, did, qid);

            /* When not Queued or has been serviced which will not show anyway in the above query, get a new token. */
            if (null == queue) {
                /*
                 * Find storeHour early, helps prevent issuing token when queue is closed or due to some obstruction.
                 * To eliminate this, we need to let business know about queue closed and prevent clients from joining.
                 */
                BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
                /* This condition exists only for non paid system. */
                if (0 < bizStore.getBizName().getLimitServiceByDays()
                    && StringUtils.isNotBlank(qid) //Remove this condition when un-registered user is removed
                    && queueManagerJDBC.hasServicedInPastXDays(codeQR, qid, bizStore.getBizName().getLimitServiceByDays())) {
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.X)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
                DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
                StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);

                if (!bizStore.isActive() || storeHour.isDayClosed() || storeHour.isTempDayClosed() || storeHour.isPreventJoining()) {
                    LOG.warn("When queue closed or prevent joining, attempting to create new token");
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.C)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                int requesterTime = DateFormatter.getTimeIn24HourFormat(LocalTime.now(zoneId));
                int tokenFrom = storeHour.getTokenAvailableFrom();
                if (requesterTime < tokenFrom) {
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.B)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }

                Assertions.assertNotNull(tokenService, "TokenService cannot be null to generate new token");
                TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
                if (tokenQueue.getLastNumber() >= bizStore.getAvailableTokenCount() && bizStore.getAvailableTokenCount() > 0) {
                    return new JsonToken(codeQR, bizStore.getBusinessType())
                        .setToken(0)
                        .setServingNumber(0)
                        .setDisplayName(bizStore.getDisplayName())
                        .setQueueStatus(QueueStatusEnum.L)
                        .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
                }
                /* Since its a dummy number set before purchase there is a possibility of having more numbers than limit set. */
                tokenQueue.setLastNumber(Integer.parseInt(LocalDateTime.now().format(DateUtil.DTF_HH_MM_SS_SSS)));
                LOG.info("Assigned to queue with codeQR={} with new token={}", codeQR, tokenQueue.getLastNumber());

                try {
                    queue = new QueueEntity(codeQR, did, tokenService, qid, tokenQueue.getLastNumber(), tokenQueue.getDisplayName(), tokenQueue.getBusinessType());
                    if (StringUtils.isNotBlank(guardianQid)) {
                        /* Set this field when client is really a guardian and has at least one dependent in profile. */
                        queue.setGuardianQid(guardianQid);
                    }
                    queue.setQueueUserState(QueueUserStateEnum.I)
                        .setBizNameId(bizStore.getBizName().getId());
                    queueManager.insert(queue);
                    updateQueueWithUserDetail(codeQR, qid, queue);
                } catch (DuplicateKeyException e) {
                    LOG.error("Error adding to queue did={} codeQR={} reason={}", did, codeQR, e.getLocalizedMessage(), e);
                    return new JsonToken(codeQR, tokenQueue.getBusinessType());
                }

                return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setToken(queue.getTokenNumber())
                    .setServingNumber(tokenQueue.getCurrentlyServing())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(tokenQueue.getQueueStatus())
                    .setExpectedServiceBegin(queue.getExpectedServiceBegin())
                    .setTransactionId(queue.getTransactionId());
            }

            TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
            LOG.info("Already registered token={} topic={} qid={} did={} queueStatus={}",
                queue.getTokenNumber(), tokenQueue.getTopic(), qid, did, tokenQueue.getQueueStatus());

            doActionBasedOnQueueStatus(codeQR, tokenQueue);

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setToken(queue.getTokenNumber())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setExpectedServiceBegin(queue.getExpectedServiceBegin())
                .setTransactionId(queue.getTransactionId());
        } catch (Exception e) {
            LOG.error("Failed getting token reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed getting token");
        }
    }

    /** Update QueueEntity when payment is performed. */
    @Mobile
    public JsonToken updateJsonToken(String codeQR, String transactionId) {
        LOG.info("Updated Queue on Payment for codeQR={} transactionId={}", codeQR, transactionId);
        TokenQueueEntity existingStateOfTokenQueue = findByCodeQR(codeQR);
        QueueEntity queue = queueManager.findByTransactionId(codeQR, transactionId);
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);

        TokenQueueEntity tokenQueue;
        if (queue.getTokenNumber() > existingStateOfTokenQueue.getLastNumber()) {
            //This means payment is being made when getting a new token.
            TokenQueueEntity newTokenQueue = getNextToken(codeQR, bizStore.getAvailableTokenCount());
            if (newTokenQueue == null && bizStore.getAvailableTokenCount() > 0) {
                return new JsonToken(codeQR, bizStore.getBusinessType())
                    .setToken(0)
                    .setServingNumber(0)
                    .setDisplayName(bizStore.getDisplayName())
                    .setQueueStatus(QueueStatusEnum.L)
                    .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
            }

            doActionBasedOnQueueStatus(codeQR, newTokenQueue);
            tokenQueue = newTokenQueue;
        } else {
            //This means payment is being made on existing token. That is after token has been acquired.
            doActionBasedOnQueueStatus(codeQR, existingStateOfTokenQueue);
            tokenQueue = existingStateOfTokenQueue;
        }

        /*
         * Find storeHour early, helps prevent issuing token when queue is closed or due to some obstruction.
         * To eliminate this, we need to let business know about queue closed and prevent clients from joining.
         */
        ZoneId zoneId = TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId();
        DayOfWeek dayOfWeek = ZonedDateTime.now(zoneId).getDayOfWeek();
        StoreHourEntity storeHour = storeHourManager.findOne(bizStore.getId(), dayOfWeek);
        Date expectedServiceBegin = computeExpectedServiceBeginTime(bizStore.getAverageServiceTime(), zoneId, storeHour, tokenQueue);
        if (queue.getTokenNumber() > existingStateOfTokenQueue.getLastNumber()) {
            /*
             * Update expectedServiceBegin and Token Number when payment is being made while getting a new token.
             * Do not update otherwise as this will throw duplicate exception as it would update existing token with expectedServiceBegin.
             */
            boolean updatedState = queueManager.onPaymentChangeToQueue(
                queue.getId(),
                tokenQueue.getLastNumber(),
                expectedServiceBegin);

            LOG.info("Queue state updated successfully={}", updatedState);
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setToken(tokenQueue.getLastNumber())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setExpectedServiceBegin(expectedServiceBegin)
            .setTransactionId(queue.getTransactionId());
    }

    /** Calculate based on zone and then save the expected service time based on UTC. */
    Date computeExpectedServiceBeginTime(long averageServiceTime, ZoneId zoneId, StoreHourEntity storeHour, TokenQueueEntity tokenQueue) {
        Date expectedServiceBegin;
        if (0 != averageServiceTime) {
            ZonedDateTime zonedNow = ZonedDateTime.now(zoneId);
            LOG.debug("Time zonedNow={} at zoneId={} bizStoreId={}", zonedNow, zoneId.getId(), storeHour.getBizStoreId());
            ZonedDateTime zonedStartHour = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(zoneId), storeHour.startHour()), zoneId);
            ZonedDateTime zonedEndHour = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(zoneId), storeHour.endHour()), zoneId).minusMinutes(PREVENT_JOINING_BEFORE_CLOSING);
            Duration duration = Duration.between(zonedNow, zonedStartHour);
            LOG.debug("Duration in minutes={}", duration.toMinutes());

            /*
             * Why subtract 1, as the time has to be calculated for the start of service.
             * By keeping last number, service time is delayed. Additional 1 at the end is to make sure this equation does not
             * return 0.
             */
            long serviceInSeconds = new BigDecimal(averageServiceTime)
                .divide(new BigDecimal(GetTimeAgoUtils.SECOND_MILLIS), MathContext.DECIMAL64).setScale(2, RoundingMode.CEILING)
                .multiply(new BigDecimal(tokenQueue.getLastNumber() - 1 - tokenQueue.getCurrentlyServing()))
                .add(new BigDecimal(1)).longValue();
            LOG.debug("Service in serviceInSeconds={} averageServiceTime={}", serviceInSeconds, averageServiceTime);

            ZonedDateTime zonedServiceTime;
            if (duration.isNegative()) {
                LOG.debug("Store has already started or closed");
                zonedServiceTime = ZonedDateTime.of(
                    LocalDateTime.now(zoneId)
                        .plusSeconds(serviceInSeconds)
                        .plusMinutes(storeHour.getDelayedInMinutes()),
                    zoneId);
            } else {
                zonedServiceTime = ZonedDateTime.of(
                    LocalDateTime.now(zoneId)
                        .plusSeconds(serviceInSeconds)
                        .plusMinutes(duration.toMinutes())
                        .plusMinutes(storeHour.getDelayedInMinutes()),
                    zoneId);
                LOG.debug("Plus getDelayedInMinutes {}", zonedServiceTime);
            }

            if (storeHour.isLunchTimeEnabled()) {
                Duration breakTime = Duration.between(storeHour.lunchStartHour(), storeHour.lunchEndHour());
                ZonedDateTime zonedLunchStart = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(zoneId), storeHour.lunchStartHour()), zoneId);
                LOG.debug("Expected ServiceTime={} lunchTimeStart={}", zonedServiceTime, zonedLunchStart);
                if (zonedServiceTime.compareTo(zonedLunchStart) > 0) {
                    zonedServiceTime = zonedServiceTime.plusMinutes(breakTime.toMinutes());
                }
            }

            if (zonedServiceTime.compareTo(zonedEndHour) > 0) {
                LOG.error("After closing hour for {} zonedServiceTime={} endHour={} bizStoreId={}",
                    tokenQueue.getDisplayName(),
                    zonedServiceTime,
                    zonedEndHour,
                    storeHour.getBizStoreId());
                throw new ExpectedServiceBeyondStoreClosingHour("Serving time exceeds after store closing time");
            }

            /* Changed to UTC time before saving. */
            expectedServiceBegin = DateUtil.convertToDateTime_UTC(zonedServiceTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
            LOG.debug("Expected service time for token {} UTC {} {}", tokenQueue.getLastNumber(), expectedServiceBegin, zonedServiceTime);
        } else {
            LOG.error("AverageServiceTime is not set bizStoreId={}", storeHour.getBizStoreId());
            ZonedDateTime zonedServiceTime = ZonedDateTime.of(LocalDateTime.now(zoneId), zoneId);
            expectedServiceBegin = DateUtil.convertToDateTime_UTC(zonedServiceTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        }
        return expectedServiceBegin;
    }

    /** Invokes a refresh when some event or activity happens that needs to be notified. */
    @Mobile
    public void forceRefreshOnSomeActivity(String codeQR) {
        doActionBasedOnQueueStatus(codeQR, findByCodeQR(codeQR));
    }

    private void doActionBasedOnQueueStatus(String codeQR, TokenQueueEntity tokenQueue) {
        switch (tokenQueue.getQueueStatus()) {
            case D:
                sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue, null);
                tokenQueueManager.changeQueueStatus(codeQR, QueueStatusEnum.R);
                break;
            case S:
                sendMessageToTopic(codeQR, QueueStatusEnum.S, tokenQueue, null);
                break;
            case R:
                sendMessageToTopic(codeQR, QueueStatusEnum.R, tokenQueue, null);
                break;
            default:
                sendMessageToTopic(codeQR, QueueStatusEnum.N, tokenQueue, null);
                break;
        }
    }

    @Async
    public void updateQueueWithUserDetail(String codeQR, String qid, QueueEntity queue) {
        Assertions.assertNotNull(queue.getId(), "Queue should have been persisted before executing the code");
        if (StringUtils.isNotBlank(qid)) {
            /* Set record reference id when its blank. This help is changing patient when medical record already exists in queue. */
            if (StringUtils.isBlank(queue.getRecordReferenceId())) {
                queue.setRecordReferenceId(CommonUtil.generateHexFromObjectId());
            }

            UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
            queue.setCustomerName(userProfile.getName());
            if (StringUtils.isBlank(userProfile.getGuardianPhone())) {
                queue.setCustomerPhone(userProfile.getPhone());
            } else {
                queue.setCustomerPhone(userProfile.getGuardianPhone());
            }

            if (queueManagerJDBC.hasClientVisitedThisStoreAndServiced(codeQR, qid)) {
                queue.setClientVisitedThisStore(true);
                //Fails with Failed getting token reason=Incorrect result size: expected 1, actual 0 when users has not visited the store
                queue.setClientVisitedThisStoreDate(queueManagerJDBC.clientVisitedStoreAndServicedDate(codeQR, qid));
            }

            if (null != userProfile.getQidOfDependents() && !userProfile.getQidOfDependents().isEmpty()) {
                queue.setGuardianQid(qid);
            } else if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
                UserProfileEntity guardianProfile = accountService.checkUserExistsByPhone(userProfile.getGuardianPhone());
                if (null == guardianProfile) {
                    /* Failure could be because of phone migration or other reason. Investigate. */
                    LOG.error("Failed to find guardianPhone={} qid={}", userProfile.getGuardianPhone(), userProfile.getQueueUserId());
                } else {
                    queue.setGuardianQid(guardianProfile.getQueueUserId());
                }
            }

            /* Add business customer id if any associated with qid and codeQR. */
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());
            BusinessCustomerEntity businessCustomer = businessCustomerService.findOneByQidAndAttribute(
                qid,
                queue.getBizNameId(),
                CommonHelper.findBusinessCustomerAttribute(bizStore));
            if (null != businessCustomer) {
                queue.setBusinessCustomerId(businessCustomer.getBusinessCustomerId())
                    .setBusinessCustomerIdChangeCount(businessCustomer.getVersion())
                    .setCustomerPriorityLevel(businessCustomer.getCustomerPriorityLevel())
                    .setBusinessCustomerAttributes(businessCustomer.getBusinessCustomerAttributes());
            } else {
                queue.setBusinessCustomerId(null)
                    .setBusinessCustomerIdChangeCount(0);
            }

            /* Added for business offer to display for new user for that business. */
            queue.setClientVisitedThisBusiness(queueManagerJDBC.hasClientVisitedThisBusiness(queue.getBizNameId(), qid));

            LOG.debug("Updated Queue={}", queue);
            queueManager.save(queue);
        }
    }

    @Mobile
    public JsonToken updateServing(String codeQR, QueueStatusEnum queueStatus, int serving, String goTo) {
        TokenQueueEntity tokenQueue = tokenQueueManager.updateServing(codeQR, serving, queueStatus);
        sendMessageToTopic(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo);

        LOG.info("After sending message to merchant");
        QueueEntity queue = findOne(codeQR, tokenQueue.getCurrentlyServing());
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", queue.getQueueUserId(), queue.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setCustomerName(queue.getCustomerName())
                .setTransactionId(queue.getTransactionId());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(tokenQueue.getCurrentlyServing())
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber())
            .setTransactionId(queue.getTransactionId());
    }

    /**
     * This acquires the record of the person being served by server. No one gets informed when the record is
     * acquired other than the person who's record is acquired to be served next.
     */
    @Mobile
    public JsonToken updateThisServing(String codeQR, QueueStatusEnum queueStatus, int serving, String goTo) {
        TokenQueueEntity tokenQueue = findByCodeQR(codeQR);
        sendMessageToTopic(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo);
        /*
         * Do not inform anyone other than the person with the
         * token who is being served. This is personal message.
         * of being served out of order/sequence.
         */
        sendMessageToSelectedTokenUser(codeQR, tokenQueue.getQueueStatus(), tokenQueue, goTo, serving);

        LOG.info("After sending message to business and personal message to user of token");
        QueueEntity queue = findOne(codeQR, serving);
        if (queue != null && queue.getCustomerName() != null) {
            LOG.info("Sending message to merchant, queue qid={} did={}", queue.getQueueUserId(), queue.getDid());

            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setQueueStatus(tokenQueue.getQueueStatus())
                .setServingNumber(serving)
                .setDisplayName(tokenQueue.getDisplayName())
                .setToken(tokenQueue.getLastNumber())
                .setCustomerName(queue.getCustomerName())
                .setTransactionId(queue.getTransactionId());
        }

        return new JsonToken(codeQR, tokenQueue.getBusinessType())
            .setQueueStatus(tokenQueue.getQueueStatus())
            .setServingNumber(serving)
            .setDisplayName(tokenQueue.getDisplayName())
            .setToken(tokenQueue.getLastNumber())
            .setTransactionId(queue.getTransactionId());
    }

    /**
     * Send FCM message to Topic asynchronously.
     */
    private void sendMessageToTopic(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo) {
        switch (tokenQueue.getBusinessType().getMessageOrigin()) {
            case Q:
                executorService.submit(() -> invokeThreadSendMessageToTopic(codeQR, queueStatus, tokenQueue, goTo));
                break;
            case O:
                //Do Nothing
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getMessageOrigin());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    /**
     * Send FCM message to person with specific token number asynchronously.
     */
    private void sendMessageToSelectedTokenUser(String codeQR, QueueStatusEnum queueStatus, TokenQueueEntity tokenQueue, String goTo, int tokenNumber) {
        switch (tokenQueue.getBusinessType().getMessageOrigin()) {
            case Q:
                executorService.submit(() -> invokeThreadSendMessageToSelectedTokenUser(codeQR, queueStatus, tokenQueue, goTo, tokenNumber));
                break;
            case O:
                //Do Nothing
                break;
            default:
                LOG.error("Reached unreachable condition {}", tokenQueue.getBusinessType().getMessageOrigin());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String qid, MessageOriginEnum messageOrigin) {
        LOG.debug("Sending message to specific user title={} body={} qid={} messageOrigin={}", title, body, qid, messageOrigin);
        RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(qid);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, null, registeredDevice, messageOrigin);
        } else {
            LOG.warn("Skipped as no registered device found for qid={}", qid);
        }
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String imageURL, String qid, MessageOriginEnum messageOrigin) {
        LOG.debug("Sending message to specific user title={} body={} qid={} messageOrigin={}", title, body, qid, messageOrigin);
        RegisteredDeviceEntity registeredDevice = registeredDeviceManager.findRecentDevice(qid);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, imageURL, registeredDevice, messageOrigin);
        } else {
            LOG.warn("Skipped as no registered device found for qid={}", qid);
        }
    }

    /** Sends any message to a specific user. */
    public void sendMessageToSpecificUser(String title, String body, String imageURL, RegisteredDeviceEntity registeredDevice, MessageOriginEnum messageOrigin) {
        LOG.debug("Sending message to specific user title={} body={} messageOrigin={}", title, body, messageOrigin);
        if (null != registeredDevice) {
            createMessageToSendToSpecificUserOrDevice(title, body, imageURL, registeredDevice, messageOrigin);
        }
    }

    private void createMessageToSendToSpecificUserOrDevice(String title, String body, String imageURL, RegisteredDeviceEntity registeredDevice, MessageOriginEnum messageOrigin) {
        String token = registeredDevice.getToken();
        JsonMessage jsonMessage = new JsonMessage(token);
        JsonData jsonData = new JsonTopicData(messageOrigin, FirebaseMessageTypeEnum.P).getJsonAlertData();
        jsonData.setImageURL(imageURL);

        if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
            jsonMessage.getNotification()
                .setTitle(title)
                .setBody(body);
        } else {
            jsonMessage.setNotification(null);
            jsonData.setTitle(title)
                .setBody(body);
        }

        jsonMessage.setData(jsonData);
        LOG.info("Specific Message={}", jsonMessage.asJson());
        boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
        if (!fcmMessageBroadcast) {
            LOG.warn("Failed personal message={}", jsonMessage.asJson());
        } else {
            LOG.info("Sent personal message={}", jsonMessage.asJson());
        }
    }

    /**
     * Sends any message to all users subscribed to topic. This includes Client and Merchant.
     */
    @Mobile
    public void sendAlertMessageToAllOnSpecificTopic(String title, String body, TokenQueueEntity tokenQueue, QueueStatusEnum queueStatus) {
        LOG.debug("Sending message to all title={} body={}", title, body);
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Topic being sent to {}", tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(MessageOriginEnum.A, FirebaseMessageTypeEnum.P).getJsonAlertData()
                //Added additional info to message for Android to not crash as it looks for CodeQR.
                //TODO improve messaging to do some action on Client and Business app when status is Closed.
                .setCodeQR(tokenQueue.getId())
                .setBusinessType(tokenQueue.getBusinessType());

            if (DeviceTypeEnum.I == deviceType) {
                jsonMessage.getNotification()
                    .setTitle(title)
                    .setBody(body);
            } else {
                jsonMessage.setNotification(null);
                jsonData.setTitle(title)
                    .setBody(body);
            }

            jsonMessage.setData(jsonData);
            LOG.info("Broadcast Message={}", jsonMessage.asJson());
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.info("Sent message to all subscriber of topic message={}", jsonMessage.asJson());
            }
        }
    }

    /**
     * Formulates and send messages to FCM.
     */
    void invokeThreadSendMessageToTopic(
        String codeQR,
        QueueStatusEnum queueStatus,
        TokenQueueEntity tokenQueue,
        String goTo
    ) {
        LOG.debug("Sending message codeQR={} goTo={} tokenQueue={} firebaseMessageType={}", codeQR, goTo, tokenQueue, FirebaseMessageTypeEnum.P);
        int timeout = 2;
        for (DeviceTypeEnum deviceType : DeviceTypeEnum.values()) {
            LOG.debug("Topic being sent to {}", tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonMessage jsonMessage = new JsonMessage(tokenQueue.getCorrectTopic(queueStatus) + UNDER_SCORE + deviceType.name());
            JsonData jsonData = new JsonTopicData(tokenQueue.getBusinessType().getMessageOrigin(), tokenQueue.getFirebaseMessageType()).getJsonTopicQueueData()
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenQueue.getCurrentlyServing())
                .setCodeQR(codeQR)
                .setQueueStatus(queueStatus)
                .setGoTo(goTo)
                .setBusinessType(tokenQueue.getBusinessType());

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * Otherwise we will have to manage more number of topic.
             */
            switch (queueStatus) {
                case S:
                case R:
                case D:
                    //TODO remove me, added as messages go out fast, before records are propagated to other replica set
                    if (0 != timeout) {
                        try {
                            TimeUnit.SECONDS.sleep(timeout);
                            timeout = 0;
                        } catch (InterruptedException e) {
                            LOG.error("Failed adding delay reason={}", e.getLocalizedMessage());
                        }
                    }

                    /*
                     * This message has to go as the business with the opened queue
                     * will not get any update if some one joins. FCM makes sure the message is dispersed.
                     */
                    long confirmedWaiting = queueManager.countAllQueued(codeQR);
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                            .setBody("Now has " + tokenQueue.totalWaiting() + " waiting. Confirmed waiting " + confirmedWaiting)
                            .setTitle(tokenQueue.getDisplayName() + " Queue");
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now has " + tokenQueue.totalWaiting() + " waiting. Confirmed waiting " + confirmedWaiting)
                            .setTitle(tokenQueue.getDisplayName() + " Queue");
                    }
                    break;
                default:
                    List<JsonTextToSpeech> jsonTextToSpeeches = textToSpeechService.populateTextToSpeech(goTo, codeQR, tokenQueue);
                    if (DeviceTypeEnum.I == deviceType) {
                        jsonMessage.getNotification()
                            .setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                            .setLocKey("serving")
                            .setLocArgs(new String[]{String.valueOf(tokenQueue.getCurrentlyServing())})
                            .setTitle(tokenQueue.getDisplayName());

                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenQueue.getCurrentlyServing())
                            .setTitle(tokenQueue.getDisplayName());

                        jsonData.setJsonTextToSpeeches(jsonTextToSpeeches);
                    }
            }

            jsonMessage.setData(jsonData);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    /**
     * When servicing token that's out of order or sequence. Send message as the selected token is being served
     * and mark it Personal.
     */
    private void invokeThreadSendMessageToSelectedTokenUser(
        String codeQR,
        QueueStatusEnum queueStatus,
        TokenQueueEntity tokenQueue,
        String goTo,
        int tokenNumber
    ) {
        LOG.debug("Sending personal message codeQR={} goTo={} tokenNumber={}", codeQR, goTo, tokenNumber);
        QueueEntity queue = findOne(codeQR, tokenNumber);
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.findAll(queue.getQueueUserId(), queue.getDid());
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            LOG.debug("Personal message of being served is sent to qid={} deviceId={} deviceType={} with tokenNumber={}",
                registeredDevice.getQueueUserId(),
                registeredDevice.getDeviceId(),
                registeredDevice.getDeviceType(),
                tokenNumber);

            JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken());
            JsonData jsonData = new JsonTopicData(tokenQueue.getBusinessType().getMessageOrigin(), FirebaseMessageTypeEnum.P).getJsonTopicQueueData()
                .setLastNumber(tokenQueue.getLastNumber())
                .setCurrentlyServing(tokenNumber)
                .setCodeQR(codeQR)
                .setQueueStatus(queueStatus)
                .setGoTo(goTo)
                .setBusinessType(tokenQueue.getBusinessType());

            /*
             * Note: QueueStatus with 'S', 'R', 'D' should be ignore by client app.
             * As this is a personal message when server is planning to serve a specific token.
             */
            switch (queueStatus) {
                case S:
                case R:
                case D:
                    LOG.warn("Skipped sending personal message as queue status is not 'Next' but queueStatus={}", queueStatus);
                    break;
                case P:
                case C:
                    LOG.error("Cannot reach this state codeQR={}, queueStatus={}", codeQR, queueStatus);
                    return;
                case N:
                default:
                    LOG.debug("Personal device is of type={} did={} token={}",
                        registeredDevice.getDeviceType(),
                        registeredDevice.getDeviceId(),
                        registeredDevice.getToken());

                    if (DeviceTypeEnum.I == registeredDevice.getDeviceType()) {
                        jsonMessage.getNotification()
                            .setBody("Now Serving " + tokenNumber)
                            .setLocKey("serving")
                            .setLocArgs(new String[]{String.valueOf(tokenNumber)})
                            .setTitle(tokenQueue.getDisplayName());
                    } else {
                        jsonMessage.setNotification(null);
                        jsonData.setBody("Now Serving " + tokenNumber)
                            .setTitle(tokenQueue.getDisplayName());
                    }
            }

            jsonMessage.setData(jsonData);

            LOG.debug("Personal FCM message to be sent={}", jsonMessage);
            boolean fcmMessageBroadcast = firebaseMessageService.messageToTopic(jsonMessage);
            if (!fcmMessageBroadcast) {
                LOG.warn("Personal broadcast failed message={}", jsonMessage.asJson());
            } else {
                LOG.debug("Sent Personal topic={} message={}", tokenQueue.getTopic(), jsonMessage.asJson());
            }
        }
    }

    List<TokenQueueEntity> getTokenQueue(String[] ids) {
        return tokenQueueManager.getTokenQueues(ids);
    }

    public boolean isQueued(int tokenNumber, String codeQR) {
        return queueManager.isQueued(tokenNumber, codeQR);
    }

    public QueueEntity findQueuedByPhone(String codeQR, String phone) {
        return queueManager.findQueuedByPhone(codeQR, phone);
    }

    public QueueEntity findOne(String codeQR, int tokenNumber) {
        return queueManager.findOne(codeQR, tokenNumber);
    }

    void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus) {
        tokenQueueManager.changeQueueStatus(codeQR, queueStatus);
    }

    @Mobile
    @Async
    public void resetQueueSettingWhenQueueStarts(String codeQR) {
        LOG.info("Resetting queue when status started codeQR={}", codeQR);

        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        DayOfWeek dayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
        storeHourManager.resetQueueSettingWhenQueueStarts(bizStore.getId(), dayOfWeek);
    }
}
