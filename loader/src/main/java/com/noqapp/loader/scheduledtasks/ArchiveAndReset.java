package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.DateUtil.DAY.TOMORROW;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.PurchaseOrderProductEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.ScheduledTaskEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.PurchaseOrderManager;
import com.noqapp.repository.PurchaseOrderManagerJDBC;
import com.noqapp.repository.PurchaseOrderProductManager;
import com.noqapp.repository.PurchaseOrderProductManagerJDBC;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.ScheduledTaskManager;
import com.noqapp.repository.StatsBizStoreDailyManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.BizService;
import com.noqapp.service.StatsCronService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    private ScheduledTaskManager scheduledTaskManager;
    private PurchaseOrderManager purchaseOrderManager;
    private PurchaseOrderProductManager purchaseOrderProductManager;
    private PurchaseOrderManagerJDBC purchaseOrderManagerJDBC;
    private PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC;

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
        ScheduledTaskManager scheduledTaskManager,
        PurchaseOrderManager purchaseOrderManager,
        PurchaseOrderProductManager purchaseOrderProductManager,
        PurchaseOrderManagerJDBC purchaseOrderManagerJDBC,
        PurchaseOrderProductManagerJDBC purchaseOrderProductManagerJDBC
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
        this.scheduledTaskManager = scheduledTaskManager;
        this.purchaseOrderManager = purchaseOrderManager;
        this.purchaseOrderProductManager = purchaseOrderProductManager;
        this.purchaseOrderManagerJDBC = purchaseOrderManagerJDBC;
        this.purchaseOrderProductManagerJDBC = purchaseOrderProductManagerJDBC;
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
             * Hence its important to run on UTC time.
             *
             * Order stores are delayed by 5 minutes.
             */
            Date date = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
            List<BizStoreEntity> bizOrderStores = bizStoreManager.findAllOrderEndedForTheDay(date);
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
             * Only find stores that are active and not deleted. This is a back up for order too.
             * Its a catch all store that are suppose to be closed.
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

    private void runSelectiveArchiveBasedOnBusinessType(BizStoreEntity bizStore) {
        switch (bizStore.getBusinessType().getMessageOrigin()) {
            case Q:
                queueArchiveAndReset(bizStore);
                break;
            case O:
                orderArchiveAndReset(bizStore);
                break;
            default:
                LOG.error("Reached un-supported condition bizStoreId={}", bizStore.getId());
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
    }

    private void queueArchiveAndReset(BizStoreEntity bizStore) {
        LOG.info("Stats for bizStore queue={} lastRun={} bizName={} id={}",
            bizStore.getDisplayName(),
            bizStore.getQueueHistory(),
            bizStore.getBizName().getBusinessName(),
            bizStore.getId());

        List<QueueEntity> queues = queueManager.findByCodeQRSortedByToken(bizStore.getCodeQR());
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
        } catch (DataIntegrityViolationException e) {
            LOG.error("Failed bulk update. Complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            queueManagerJDBC.rollbackQueues(queues);
            LOG.error("Completed rollback for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
            throw e;
        }

        bizStore.setStoreHours(bizService.findAllStoreHours(bizStore.getId()));
        long deleted = queueManager.deleteByCodeQR(bizStore.getCodeQR());
        if (queues.size() == deleted) {
            LOG.info("Deleted and insert queue exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
        } else {
            LOG.error("Mis-match in deleted and insert queue bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
        }

        doReset(bizStore, statsBizStoreDaily);
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

        bizStore.setStoreHours(bizService.findAllStoreHours(bizStore.getId()));
        long deleted = purchaseOrderManager.deleteByCodeQR(bizStore.getCodeQR());
        //TODO(hth) has to come under transaction
        purchaseOrderProductManager.deleteByCodeQR(bizStore.getCodeQR());
        if (purchaseOrders.size() == deleted) {
            LOG.info("Deleted and insert order exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
        } else {
            LOG.error("Mis-match in deleted and insert order bizStore={} size={} delete={}", bizStore.getId(), purchaseOrders.size(), deleted);
        }

        doReset(bizStore, statsBizStoreDaily);
    }

    private void doReset(BizStoreEntity bizStore, StatsBizStoreDailyEntity statsBizStoreDaily) {
        DayOfWeek nowDayOfWeek = computeDayOfWeekHistoryIsSupposeToRun(bizStore);
        /* In queue history, we set things for tomorrow. */
        ZonedDateTime archiveNextRun = setupStoreForTomorrow(bizStore, nowDayOfWeek);
        resetStoreOfToday(bizStore, nowDayOfWeek);

        StatsBizStoreDailyEntity bizStoreRating = statsBizStoreDailyManager.computeRatingForEachQueue(bizStore.getId());
        if (null != bizStoreRating) {
            bizStoreManager.updateNextRunAndRatingWithAverageServiceTime(
                bizStore.getId(),
                bizStore.getTimeZone(),
                /* Converting to date remove everything to do with UTC, hence important to run server on UTC time. */
                Date.from(archiveNextRun.toInstant()),
                (float) bizStoreRating.getTotalRating() / bizStoreRating.getTotalCustomerRated(),
                bizStoreRating.getTotalCustomerRated(),
                //TODO(hth) should we compute with yesterday average time or overall average time?
                statsBizStoreDaily.getAverageServiceTime());
        } else {
            bizStoreManager.updateNextRun(
                bizStore.getId(),
                bizStore.getTimeZone(),
                Date.from(archiveNextRun.toInstant()));
        }

        tokenQueueManager.resetForNewDay(bizStore.getCodeQR());
    }

    /**
     * Note: When time is 11:59 PM and during run the clock switches to 12:00 AM, queue reset is missed. This ensures reset for correct
     * history.
     */
    private DayOfWeek computeDayOfWeekHistoryIsSupposeToRun(BizStoreEntity bizStore) {
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

    private ZonedDateTime setupStoreForTomorrow(BizStoreEntity bizStore, DayOfWeek dayOfWeek) {
        StoreHourEntity tomorrow = bizStore.getStoreHours().get(CommonUtil.getNextDayOfWeek(dayOfWeek).getValue() - 1);
        if (StringUtils.isNotBlank(bizStore.getScheduledTaskId())) {
            populateForScheduledTask(bizStore, tomorrow);
        }

        TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
        /* When closed set hour to 23 and minute to 59. */
        int hourOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 23 : tomorrow.storeClosingHourOfDay();
        int minuteOfDay = tomorrow.isDayClosed() || tomorrow.isTempDayClosed() ? 59 : tomorrow.storeClosingMinuteOfDay();
        LOG.info("Tomorrow Closing dayOfWeek={} Hour={} Minutes={} id={}",
            DayOfWeek.of(tomorrow.getDayOfWeek()), hourOfDay, minuteOfDay, tomorrow.getId());
        return DateUtil.computeNextRunTimeAtUTC(timeZone, hourOfDay, minuteOfDay, TOMORROW);
    }

    private void populateForScheduledTask(BizStoreEntity bizStore, StoreHourEntity storeHour) {
        ScheduledTaskEntity scheduledTask = scheduledTaskManager.findOneById(bizStore.getScheduledTaskId());
        Date from = DateUtil.convertToDate(scheduledTask.getFrom(), bizStore.getTimeZone());
        Date until = DateUtil.convertToDate(scheduledTask.getUntil(), bizStore.getTimeZone());
        if (DateUtil.isThisDayBetween(from, until, TOMORROW, ZoneId.of(bizStore.getTimeZone()))) {
            switch (scheduledTask.getScheduleTask()) {
                case CLOSE:
                    storeHour.setTempDayClosed(true);
                    break;
                default:
                    throw new UnsupportedOperationException("Reached Unsupported Condition");
            }

            bizService.modifyOne(storeHour);
        } else {
            Date today = DateUtil.dateAtTimeZone(bizStore.getTimeZone());
            if (today.after(until)) {
                /* Remove schedule only when today is after set until schedule. */
                LOG.info("Removing schedule displayName={} today={} until={}", bizStore.getDisplayName(), today, until);
                bizService.unsetScheduledTask(bizStore.getId());
                scheduledTaskManager.inActive(bizStore.getScheduledTaskId());
            }
        }
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
}
