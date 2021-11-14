package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.DateUtil.DAY.TODAY;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.loader.service.ComputeNextRunService;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.BizService;
import com.noqapp.service.FileService;
import com.noqapp.service.MessageCustomerService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.StoreHourService;
import com.noqapp.service.utils.RandomBannerImage;
import com.noqapp.service.utils.ServiceUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

/**
 * User: hitender
 * Date: 3/10/17 2:57 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class ArchiveAndReset {
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveAndReset.class);

    private String moveToRDBS;
    private int timeDelayInMinutes;

    private BizStoreManager bizStoreManager;
    private StatsBizStoreDailyManager statsBizStoreDailyManager;
    private QueueManager queueManager;
    private TokenQueueManager tokenQueueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private StatsCronService statsCronService;
    private BizService bizService;
    private ComputeNextRunService computeNextRunService;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;
    private FileService fileService;
    private StoreHourService storeHourService;
    private MessageCustomerService messageCustomerService;

    private ScheduledExecutorService executorService;

    private StatsCronEntity statsCron;

    @Autowired
    public ArchiveAndReset(
        @Value("${ArchiveAndReset.moveToRDBS}")
        String moveToRDBS,

        @Value("${ArchiveAndReset.timeDelayInMinutes}")
        int timeDelayInMinutes,

        BizStoreManager bizStoreManager,
        StatsBizStoreDailyManager statsBizStoreDailyManager,
        QueueManager queueManager,
        TokenQueueManager tokenQueueManager,
        QueueManagerJDBC queueManagerJDBC,
        StatsCronService statsCronService,
        BizService bizService,
        ComputeNextRunService computeNextRunService,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC,
        FileService fileService,
        StoreHourService storeHourService,
        MessageCustomerService messageCustomerService
    ) {
        this.moveToRDBS = moveToRDBS;
        this.timeDelayInMinutes = timeDelayInMinutes;

        this.bizStoreManager = bizStoreManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.statsCronService = statsCronService;
        this.bizService = bizService;
        this.computeNextRunService = computeNextRunService;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
        this.fileService = fileService;
        this.storeHourService = storeHourService;
        this.messageCustomerService = messageCustomerService;

        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Scheduled(fixedDelayString = "${loader.ArchiveAndReset.queuePastData}")
    public void doArchiveAndReset() {
        statsCron = new StatsCronEntity(
            ArchiveAndReset.class.getName(),
            "queuePastData",
            moveToRDBS);

        int found = 0, failure = 0, success = 0;
        if ("OFF".equalsIgnoreCase(moveToRDBS)) {
            LOG.debug("feature is {}", moveToRDBS);
        }

        try {
            /*
             * Date is based on UTC time of the System.
             * Hence, it is important to run on UTC time.
             *
             * Order stores are delayed by 5 minutes.
             */
            Date date = Date.from(Instant.now().minus(1, ChronoUnit.MINUTES));
            List<BizStoreEntity> bizOrderStores = archiveOnlyStoreClosedForTheDay(bizStoreManager.findAllOrderEndedForTheDay(date));
            found = bizOrderStores.size();
            LOG.info("Order Stores found={} date={}", found, date);
            for (BizStoreEntity bizStore : bizOrderStores) {
                try {
                    runSelectiveArchiveBasedOnBusinessType(bizStore);
                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail on Orders to RDB bizStore={} codeQR={} reason={}",
                        bizStore.getId(),
                        bizStore.getCodeQR(),
                        e.getLocalizedMessage(),
                        e);
                }
            }

            /* Queue store which are service store can have a different delay. Currently supporting 60 minutes. */
            date = Date.from(Instant.now().minus(timeDelayInMinutes, ChronoUnit.MINUTES));

            /*
             * Only find stores that are active and not deleted. It processes only queues.
             */
            List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueEndedForTheDay(date);
            found += bizStores.size();
            LOG.info("Queue Stores found={} date={}", bizStores.size(), date);
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    runSelectiveArchiveBasedOnBusinessType(bizStore);
                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail on Queues to RDB bizStore={} codeQR={} reason={}",
                        bizStore.getId(),
                        bizStore.getCodeQR(),
                        e.getLocalizedMessage(),
                        e);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to execute archive move to RDB");
        } finally {
            if (0 != found || 0 != failure || 0 != success) {
                statsCron.addStats("found", found);
                statsCron.addStats("failure", failure);
                statsCron.addStats("success", success);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete found={} failure={} success={}", found, failure, success);
            }
        }
    }

    /**
     * Prevents reset when store not closed for the day. This is to prevent running of archive when store is open and running.
     * Just delay the reset to today's end of day.
     */
    private List<BizStoreEntity> archiveOnlyStoreClosedForTheDay(List<BizStoreEntity> bizOrderStores) {
        List<BizStoreEntity> readyForArchive = new LinkedList<>();
        for (BizStoreEntity bizStore : bizOrderStores) {
            DayOfWeek nowDayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
            StoreHourEntity storeHour = storeHourService.findStoreHour(bizStore.getId(), nowDayOfWeek);
            if (storeHour.isDayClosed() || storeHour.isTempDayClosed()) {
                readyForArchive.add(bizStore);
            } else {
                LocalTime now = DateUtil.getTimeAtTimeZone(bizStore.getTimeZone());
                LocalTime endHour = storeHour.endHour();
                if (now.isAfter(endHour)) {
                    readyForArchive.add(bizStore);
                } else {
                    try {
                        /* Expected to run at UTC. */
                        Date wasExpectedToRun = bizStore.getQueueHistory();
                        ZonedDateTime nextRun = DateUtil.computeNextRunTimeAtUTC(
                            TimeZone.getTimeZone(bizStore.getTimeZone()),
                            storeHour.storeClosingHourOfDay(),
                            storeHour.storeClosingMinuteOfDay(),
                            TODAY);

                        /* Delay archive when difference is greater than 30 minutes. */
                        if (Constants.MINUTES_30 < DateUtil.getMinutesBetween(DateUtil.asLocalDateTime(wasExpectedToRun), DateUtil.asLocalDateTime(Date.from(nextRun.toInstant())))) {
                            long delayedArchiveByHours = DateUtil.getHoursBetween(
                                DateUtil.asLocalDateTime(wasExpectedToRun),
                                DateUtil.asLocalDateTime(Date.from(nextRun.toInstant())));

                            long delayedArchiveByMinutes = DateUtil.getMinutesBetween(
                                DateUtil.asLocalDateTime(wasExpectedToRun),
                                DateUtil.asLocalDateTime(Date.from(nextRun.toInstant())));

                            LOG.error("Archive history date re-computed {} store={} biz={} expected={} newArchiveTime={} delayedArchiveByHours={} delayedArchiveByMinutes={} now={} endHour={} nowDayOfWeek={}",
                                bizStore.getId(),
                                bizStore.getBizName().getBusinessName(),
                                bizStore.getDisplayName(),
                                wasExpectedToRun,
                                Date.from(nextRun.toInstant()),
                                delayedArchiveByHours,
                                delayedArchiveByMinutes,
                                now,
                                endHour,
                                nowDayOfWeek);

                            bizStore.setQueueHistory(Date.from(nextRun.toInstant()));
                            bizStoreManager.save(bizStore);
                        } else {
                            LOG.info("Archiving ready store={} biz={}", bizStore.getId(), bizStore.getBizName().getId());
                            readyForArchive.add(bizStore);
                        }
                    } catch (Exception e) {
                        LOG.warn("Skipped bizStore {} {}", bizStore.getId(), e.getLocalizedMessage(), e);
                    }
                }
            }
        }

        return bizOrderStores;
    }

    private void runSelectiveArchiveBasedOnBusinessType(BizStoreEntity bizStore) {
        switch (bizStore.getBusinessType().getMessageOrigin()) {
            case Q:
                queueRemoveAllWithQueueUserStateAsInitial(bizStore);
                queueManager.duringArchiveMarkAllAsServedInQueue(bizStore.getCodeQR());
                executorService.schedule(() -> queueArchiveAndReset(bizStore), 20, TimeUnit.SECONDS);
                break;
            case O:
                orderArchiveAndReset(bizStore);
                break;
            default:
                LOG.error("Reached un-supported condition bizStoreId={}", bizStore.getId());
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
    }

    private void queueRemoveAllWithQueueUserStateAsInitial(BizStoreEntity bizStore) {
        LOG.info("Remove {} state queue={} lastRun={} bizName={} id={}",
            QueueUserStateEnum.I,
            bizStore.getDisplayName(),
            bizStore.getQueueHistory(),
            bizStore.getBizName().getBusinessName(),
            bizStore.getId());

        List<QueueEntity> queues = queueManager.findByCodeQRWithInitialStateAndTransactionId(bizStore.getCodeQR());
        for (QueueEntity queue : queues) {
            PurchaseOrderEntity purchaseOrder = purchaseOrderManager.findByTransactionId(queue.getTransactionId());
            //Only for DeliveryMode QS
            purchaseOrderProductManager.removePurchaseOrderProduct(purchaseOrder.getId());
            purchaseOrderManager.removePurchaseOrderForService(queue.getTransactionId());

            queueManager.deleteReferenceToTransactionId(queue.getCodeQR(), queue.getTransactionId());
        }

        LOG.info("Deleted {} records with {} state", queues.size(), QueueUserStateEnum.I);
    }

    private void queueArchiveAndReset(BizStoreEntity bizStore) {
        LOG.info("Stats for bizStore queue={} lastRun={} bizName={} id={}",
            bizStore.getDisplayName(),
            bizStore.getQueueHistory(),
            bizStore.getBizName().getBusinessName(),
            bizStore.getId());

        List<QueueEntity> queues = queueManager.findByCodeQRSortedByTokenIgnoreInitialState(bizStore.getCodeQR());
        StatsBizStoreDailyEntity statsBizStoreDaily;
        try {
            statsBizStoreDaily = saveDailyQueueStat(bizStore.getId(), bizStore.getBizName().getId(), bizStore.getCodeQR(), queues);
            queueManagerJDBC.batchQueues(queues);

            /* Complete transaction once data moved. */
            if (statsBizStoreDaily.getTotalClient() > 0) {
                statsBizStoreDailyManager.save(statsBizStoreDaily);
                LOG.info("Saved daily queue store stat={}", statsBizStoreDaily);
            } else {
                /* Skip stats when totalClient for queue has been zero. */
                LOG.info("Skipped daily queue store totalClient={} stat={}", statsBizStoreDaily.getTotalClient(), statsBizStoreDaily);
            }

            messageCustomerService.unsubscribeWhenUserInQueueHaveStatusAborted(queues, bizStore);
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed bulk update. Complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            queueManagerJDBC.rollbackQueues(queues);
            LOG.error("Completed rollback for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            throw e;
        }

        bizStore.setStoreHours(storeHourService.findAllStoreHours(bizStore.getId()));
        long numberOfRecordsToBeDeleted = queueManager.countByCodeQR(bizStore.getCodeQR());
        if (queues.size() == numberOfRecordsToBeDeleted) {
            queueManager.deleteByCodeQR(bizStore.getCodeQR());
            LOG.info("Deleted and insert queue exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
        } else {
            AtomicInteger count = new AtomicInteger();
            List<QueueEntity> toBeDeletedQueues = queueManager.findAllByCodeQR(bizStore.getCodeQR());
            try (Stream<QueueEntity> stream = toBeDeletedQueues.stream()) {
                stream.iterator().forEachRemaining(queue -> {
                    if (queue.getQueueUserState() == QueueUserStateEnum.I) {
                        count.getAndIncrement();
                    }
                });
            }

            queueManager.deleteByCodeQR(bizStore.getCodeQR());
            if (numberOfRecordsToBeDeleted - count.intValue() - queues.size() == 0) {
                LOG.info("Deleted and insert queue exact bizStore={} mismatch={} delete={} codeQR={}",
                    bizStore.getId(), count.intValue(), numberOfRecordsToBeDeleted, bizStore.getCodeQR());
            } else {
                LOG.error("Mis-match in deleted and insert queue bizStore={} size={} mismatch={} delete={}",
                    bizStore.getId(), queues.size(), count.intValue(), numberOfRecordsToBeDeleted);
            }
        }

        doReset(bizStore, statsBizStoreDaily);
        orderArchiveAndReset(bizStore);
    }

    private void orderArchiveAndReset(BizStoreEntity bizStore) {
        Date until = Date.from(Instant.now().minus(timeDelayInMinutes, ChronoUnit.MINUTES));
        LOG.info("Stats for bizStore queue={} lastRun={} bizName={} id={} until={}",
            bizStore.getDisplayName(),
            bizStore.getQueueHistory(),
            bizStore.getBizName().getBusinessName(),
            bizStore.getId(),
            until);

        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderManager.findAllOrderByCodeQRUntil(bizStore.getCodeQR(), until);
        try {
            for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
                List<PurchaseOrderProductEntity> purchaseOrderProducts = purchaseOrderProductManager.getAllByPurchaseOrderId(purchaseOrder.getId());
                purchaseOrderProductManagerJDBC.batchPurchaseOrderProducts(purchaseOrderProducts);
            }
        } catch (DataIntegrityViolationException e) {
            purchaseOrderProductManagerJDBC.rollbackPurchaseOrders(purchaseOrders);
            LOG.error("Completed rollback POP for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            throw e;
        }

        StatsBizStoreDailyEntity statsBizStoreDaily;
        try {
            statsBizStoreDaily = saveDailyOrderStat(bizStore.getId(), bizStore.getBizName().getId(), bizStore.getCodeQR(), purchaseOrders);
            purchaseOrderManagerJDBC.batchPurchaseOrder(purchaseOrders);

            /* Complete Transaction once data has moved. */
            if (statsBizStoreDaily.getTotalClient() > 0) {
                statsBizStoreDailyManager.save(statsBizStoreDaily);
                LOG.info("Saved daily order store stat={}", statsBizStoreDaily);
            } else {
                /* Skip stats when totalClient for queue has been zero. */
                LOG.info("Skipped daily queue store totalClient={} stat={}", statsBizStoreDaily.getTotalClient(), statsBizStoreDaily);
            }
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed bulk update. Complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            purchaseOrderManagerJDBC.rollbackPurchaseOrder(purchaseOrders);
            purchaseOrderProductManagerJDBC.rollbackPurchaseOrders(purchaseOrders);
            LOG.error("Completed rollback PO for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            throw e;
        }

        bizStore.setStoreHours(storeHourService.findAllStoreHours(bizStore.getId()));
        long deleted = purchaseOrderManager.deleteByCodeQR(bizStore.getCodeQR(), until);
        purchaseOrderProductManager.deleteByCodeQR(bizStore.getCodeQR(), until);
        if (purchaseOrders.size() == deleted) {
            LOG.info("Deleted and insert order exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
        } else {
            LOG.error("Mis-match in deleted and insert order bizStore={} size={} delete={}", bizStore.getId(), purchaseOrders.size(), deleted);
        }

        switch (bizStore.getBusinessType().getMessageOrigin()) {
            case Q:
                /* Skip for queue as its already reset in queueArchiveAndReset. */
                break;
            case O:
                doReset(bizStore, statsBizStoreDaily);
                break;
            default:
                LOG.error("Failed as messageOrigin={} not defined", bizStore.getBusinessType().getMessageOrigin());
                throw new UnsupportedOperationException("Un-supported condition reached");
        }

        if (bizStore.getStoreInteriorImages().size() == 0) {
            systemAddedStoreBannerImage(bizStore.getCodeQR(), bizStore.getBusinessType());
        }
    }

    private void doReset(BizStoreEntity bizStore, StatsBizStoreDailyEntity statsBizStoreDaily) {
        DayOfWeek nowDayOfWeek = computeDayOfWeekHistoryIsSupposeToRun(bizStore);
        resetStoreOfToday(bizStore, nowDayOfWeek);

        StatsBizStoreDailyEntity bizStoreRating = statsBizStoreDailyManager.computeRatingForEachQueue(bizStore.getId());
        /* In queue history, we set things for tomorrow. */
        ZonedDateTime archiveNextRun = computeNextRunService.setupStoreForTomorrow(bizStore);
        StoreHourEntity storeHour = storeHourService.findStoreHour(bizStore.getId(), archiveNextRun.getDayOfWeek());
        long averageServiceTime = ServiceUtils.computeAverageServiceTime(storeHour, bizStore.getAvailableTokenCount());
        LOG.info("AverageServiceTime in codeQR={} {} {} existing={} new={}",
            bizStore.getCodeQR(),
            bizStore.getDisplayName(),
            bizStore.getAvailableTokenCount(),
            bizStore.getAverageServiceTime(),
            averageServiceTime);

        if (null != bizStoreRating) {
            bizStoreManager.updateNextRunAndRatingWithAverageServiceTime(
                bizStore.getId(),
                bizStore.getTimeZone(),
                /* Converting to date remove everything to do with UTC, hence important to run server on UTC time. */
                Date.from(archiveNextRun.toInstant()),
                bizStore.getAppointmentState() != AppointmentStateEnum.O ? Date.from(computeNextRunService.setupTokenAvailableForTomorrow(bizStore).toInstant()) : null,
                (float) bizStoreRating.getTotalRating() / bizStoreRating.getTotalCustomerRated(),
                bizStoreRating.getTotalCustomerRated(),
                statsBizStoreDaily.getAverageServiceTime(),
                averageServiceTime);
        } else {
            bizStoreManager.updateNextRun(
                bizStore.getId(),
                bizStore.getTimeZone(),
                Date.from(archiveNextRun.toInstant()),
                bizStore.getAppointmentState() != AppointmentStateEnum.O ? Date.from(computeNextRunService.setupTokenAvailableForTomorrow(bizStore).toInstant()) : null,
                averageServiceTime);
        }

        tokenQueueManager.resetForNewDay(bizStore.getCodeQR());
    }

    /**
     * Note: When time is 11:59 PM and during run the clock switches to 12:00 AM, queue reset is missed. This ensures reset for correct
     * history.
     */
    DayOfWeek computeDayOfWeekHistoryIsSupposeToRun(BizStoreEntity bizStore) {
        DayOfWeek nowDayOfWeek = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId()).getDayOfWeek();
        DayOfWeek historyRunDayOfWeek = DateUtil.convertToLocalDateTime(bizStore.getQueueHistory()).getDayOfWeek();
        if (nowDayOfWeek != historyRunDayOfWeek) {
            LOG.info("nowDayOfWeek={} history run dayOfWeek={}", nowDayOfWeek, historyRunDayOfWeek);
            nowDayOfWeek = historyRunDayOfWeek;
        }
        return nowDayOfWeek;
    }

    private void resetStoreOfToday(BizStoreEntity bizStore, DayOfWeek dayOfWeek) {
        /* Always reset storeHour and other settings after the end of day. */
        StoreHourEntity today = bizStore.getStoreHours().get(dayOfWeek.getValue() - 1);
        LOG.info("Reset Store dayOfWeek={} name={} id={}", DayOfWeek.of(today.getDayOfWeek()), bizStore.getDisplayName(), bizStore.getId());
        bizService.resetTemporarySettingsOnStoreHour(today.getId());
    }

    /** Saves daily stats for BizStore Queues. */
    private StatsBizStoreDailyEntity saveDailyQueueStat(
        String bizStoreId,
        String bizNameId,
        String codeQR,
        List<QueueEntity> queues
    ) {
        long totalServiceTimeInMilliSeconds = 0, totalHoursSaved = 0;
        int totalServiced = 0,
            totalNoShow = 0,
            totalAbort = 0,
            totalRating = 0,
            totalCustomerRated = 0,
            clientsVisitedThisStore = 0,
            clientsVisitedThisBusiness = 0;

        for (QueueEntity queue : queues) {
            try {
                switch (queue.getQueueUserState()) {
                    case S:
                        if (queue.hasClientVisitedThisStore()) {
                            clientsVisitedThisStore++;
                        }

                        if (queue.hasClientVisitedThisBusiness()) {
                            clientsVisitedThisBusiness++;
                        }

                        totalServiceTimeInMilliSeconds += queue.timeTakenForServiceInMilliSeconds();
                        if (queue.getRatingCount() > 0) {
                            totalRating += queue.getRatingCount();
                            totalCustomerRated++;
                        }
                        totalServiced++;
                        int minutes;
                        switch (queue.getHoursSaved()) {
                            case 1:
                                /* Half hour. */
                                minutes = 30;
                                break;
                            case 2:
                                /* One hour. */
                                minutes = 30 * 2;
                                break;
                            case 3:
                                /* Two hours. */
                                minutes = 30 * 4;
                                break;
                            case 4:
                                /* Three hours. */
                                minutes = 30 * 6;
                                break;
                            case 5:
                                /* Four hours. */
                                minutes = 30 * 8;
                                break;
                            default:
                                /* Eight hours. */
                                minutes = 30 * 16;
                        }
                        int hours = minutes / 60;
                        totalHoursSaved += hours;
                        LOG.info("Hours saved {} total={} in milliseconds={}", hours, totalHoursSaved, hours * 60 * 1000);
                        break;
                    case A:
                        totalAbort += 1;
                        break;
                    case N:
                        totalNoShow += 1;
                        break;
                    case Q:
                        LOG.warn("Cannot be queued at this stage. Anyhow should be computed NoShow");
                        totalNoShow += 1;
                        break;
                }
            } catch (Exception e) {
                LOG.error("Failed computing daily stat QueueHistory id={} reason={}", queue.getId(), e.getLocalizedMessage(), e);
            }
        }
        StatsBizStoreDailyEntity statsBizStoreDaily = new StatsBizStoreDailyEntity();

        /* Store meta data. */
        statsBizStoreDaily
            .setBizStoreId(bizStoreId)
            .setBizNameId(bizNameId)
            .setCodeQR(codeQR);

        /* Service time and number of clients. */
        statsBizStoreDaily
            .setTotalServiceTime(totalServiceTimeInMilliSeconds)
            .setTotalServiced(totalServiced)
            .setTotalAbort(totalAbort)
            .setTotalNoShow(totalNoShow)
            .setTotalClient(totalServiced + totalAbort + totalNoShow)
            .setAverageServiceTime(0 == totalServiced ? 0 : totalServiceTimeInMilliSeconds / totalServiced)
            .setClientsPreviouslyVisitedThisStore(clientsVisitedThisStore)
            .setClientsPreviouslyVisitedThisBusiness(clientsVisitedThisBusiness);

        /* Rating and hours saved is computed only for people who have rated. This comes from review screen. */
        statsBizStoreDaily
            .setTotalRating(totalRating)
            .setTotalCustomerRated(totalCustomerRated)
            .setTotalHoursSaved(totalHoursSaved);

        computeBeginAndEndTimeOfService(queues, statsBizStoreDaily);
        return statsBizStoreDaily;
    }

    void computeBeginAndEndTimeOfService(List<QueueEntity> queues, StatsBizStoreDailyEntity statsBizStoreDaily) {
        String firstServicedOrSkipped = null;
        String lastServicedOrSkipped = null;

        QueueEntity queueFirst = queues.stream()
            .filter(queue -> queue.getQueueUserState() == QueueUserStateEnum.S || queue.getQueueUserState() == QueueUserStateEnum.N)
            .findFirst()
            .orElse(null);

        if (null != queueFirst) {
            QueueEntity queueLast = queues.stream()
                .filter(queue -> queue.getQueueUserState() == QueueUserStateEnum.S || queue.getQueueUserState() == QueueUserStateEnum.N)
                .reduce((first, second) -> second)
                .orElse(null);

            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(statsBizStoreDaily.getCodeQR());
            firstServicedOrSkipped = String.valueOf(CommonUtil.getTimeIn24HourFormat(DateUtil.convertToLocalDateTime(queueFirst.getServiceBeginTime(), bizStore.getTimeZone())));
            if (null != queueLast) {
                lastServicedOrSkipped = String.valueOf(CommonUtil.getTimeIn24HourFormat(DateUtil.convertToLocalDateTime(queueLast.getServiceEndTime(), bizStore.getTimeZone())));
            }

            LOG.info("Computed {} {} {}", statsBizStoreDaily.getCodeQR(), firstServicedOrSkipped, lastServicedOrSkipped);
        }

        statsBizStoreDaily
            .setFirstServicedOrSkipped(firstServicedOrSkipped)
            .setLastServicedOrSkipped(lastServicedOrSkipped);
    }

    /** Saves daily stats for BizStore Order. */
    private StatsBizStoreDailyEntity saveDailyOrderStat(
        String bizStoreId,
        String bizNameId,
        String codeQR,
        List<PurchaseOrderEntity> purchaseOrders
    ) {
        long totalServiceTimeInMilliSeconds = 0, totalHoursSaved = 0;
        int totalServiced = 0,
            totalAbort = 0,
            totalRating = 0,
            totalCustomerRated = 0;

        for (PurchaseOrderEntity purchaseOrder : purchaseOrders) {
            try {
                switch (purchaseOrder.getPresentOrderState()) {
                    case OD:
                        if (purchaseOrder.getRatingCount() > 0) {
                            totalRating += purchaseOrder.getRatingCount();
                            totalCustomerRated++;
                        }
                        totalServiced++;
                        break;
                    case CO:
                        totalAbort += 1;
                        break;
                    default:
                        LOG.warn("Cannot be queued at this stage. Anyhow should be computed NoShow");
                        break;
                }
            } catch (Exception e) {
                LOG.error("Failed computing daily stat PurchaseOrder id={} reason={}", purchaseOrder.getId(), e.getLocalizedMessage(), e);
            }
        }
        StatsBizStoreDailyEntity statsBizStoreDaily = new StatsBizStoreDailyEntity();

        /* Store meta data. */
        statsBizStoreDaily
            .setBizStoreId(bizStoreId)
            .setBizNameId(bizNameId)
            .setCodeQR(codeQR);

        /* Service time and number of clients. */
        statsBizStoreDaily
            .setTotalServiceTime(totalServiceTimeInMilliSeconds)
            .setTotalServiced(totalServiced)
            .setTotalAbort(totalAbort)
            .setTotalNoShow(0)
            .setTotalClient(totalServiced + totalAbort)
            .setAverageServiceTime(0 == totalServiced ? 0 : totalServiceTimeInMilliSeconds / totalServiced);

        /* Rating and hours saved is computed only for people who have rated. This comes from review screen. */
        statsBizStoreDaily
            .setTotalRating(totalRating)
            .setTotalCustomerRated(totalCustomerRated)
            .setTotalHoursSaved(totalHoursSaved);
        return statsBizStoreDaily;
    }

    /** Add default image for store. */
    public void systemAddedStoreBannerImage(String codeQR, BusinessTypeEnum businessType) {
        String filename = RandomBannerImage.pickRandomImage(businessType);
        if (null != filename) {
            try {
                File storeImage = new File(filename);
                LOG.info("System adding default store image {}", filename);
                String mimeType = FileUtil.detectMimeType(storeImage);
                BufferedImage bufferedImage = ImageIO.read(storeImage);
                fileService.addStoreImage(
                    null,
                    codeQR,
                    FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(filename, mimeType),
                    bufferedImage,
                    false);
            } catch (IOException e) {
                LOG.error("Failed finding image codeQR={} filename={} reason={}", codeQR, filename, e.getLocalizedMessage(), e);
            }
        }
    }
}
