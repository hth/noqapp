package com.noqapp.loader.scheduledtasks;

import static com.noqapp.common.utils.DateUtil.Day.TOMORROW;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.ScheduledTaskEntity;
import com.noqapp.domain.StatsBizStoreDailyEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.repository.BizStoreManager;
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
public class QueueHistory {
    private static final Logger LOG = LoggerFactory.getLogger(QueueHistory.class);

    private String moveToRDBS;

    private BizStoreManager bizStoreManager;
    private StatsBizStoreDailyManager statsBizStoreDailyManager;
    private QueueManager queueManager;
    private TokenQueueManager tokenQueueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private StatsCronService statsCronService;
    private BizService bizService;
    private ScheduledTaskManager scheduledTaskManager;

    private StatsCronEntity statsCron;

    @Autowired
    public QueueHistory(
            @Value("${QueueHistory.moveToRDBS}")
            String moveToRDBS,

            BizStoreManager bizStoreManager,
            StatsBizStoreDailyManager statsBizStoreDailyManager,
            QueueManager queueManager,
            TokenQueueManager tokenQueueManager,
            QueueManagerJDBC queueManagerJDBC,
            StatsCronService statsCronService,
            BizService bizService,
            ScheduledTaskManager scheduledTaskManager
    ) {
        this.moveToRDBS = moveToRDBS;
        this.bizStoreManager = bizStoreManager;
        this.statsBizStoreDailyManager = statsBizStoreDailyManager;
        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.statsCronService = statsCronService;
        this.bizService = bizService;
        this.scheduledTaskManager = scheduledTaskManager;
    }

    @Scheduled(fixedDelayString = "${loader.QueueHistory.queuePastData}")
    public void queuePastData() {
        statsCron = new StatsCronEntity(
                QueueHistory.class.getName(),
                "queuePastData",
                moveToRDBS);

        int found, failure = 0, success = 0;
        if ("OFF".equalsIgnoreCase(moveToRDBS)) {
            LOG.debug("feature is {}", moveToRDBS);
        }

        /*
         * Date is based on UTC time of the System.
         * Hence its important to run on UTC time.
         *
         * Added lag of 60 minutes. This should be 5 minutes. The day we get stores open 24hrs, this should be
         * reverted back to 5 minutes.
         */
        Date date = Date.from(Instant.now().minus(60, ChronoUnit.MINUTES));
        List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueEndedForTheDay(date);
        found = bizStores.size();
        LOG.info("found={} date={}", found, date);

        try {
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    LOG.info("Stats for bizStore queue={} lastRun={} bizName={} id={}",
                            bizStore.getDisplayName(),
                            bizStore.getQueueHistory(),
                            bizStore.getBizName().getBusinessName(),
                            bizStore.getId());

                    List<QueueEntity> queues = queueManager.findByCodeQR(bizStore.getCodeQR());
                    StatsBizStoreDailyEntity statsBizStoreDaily;
                    try {
                        statsBizStoreDaily = saveDailyStat(bizStore.getId(), bizStore.getBizName().getId(), bizStore.getCodeQR(), queues);
                        queueManagerJDBC.batchQueues(queues);
                    } catch (DataIntegrityViolationException e) {
                        LOG.error("Failed bulk update. Complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        queueManagerJDBC.rollbackQueues(queues);
                        LOG.error("Completed rollback for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        throw e;
                    }

                    bizStore.setStoreHours(bizService.findAllStoreHours(bizStore.getId()));
                    long deleted = queueManager.deleteByCodeQR(bizStore.getCodeQR());
                    if (queues.size() == deleted) {
                        LOG.info("Deleted and insert exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                    } else {
                        LOG.error("Mis-match in deleted and insert bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
                    }

                    DayOfWeek nowDayOfWeek = computeDayOfWeekHistoryIsSupposeToRun(bizStore);
                    /* In queue history, we set things for tomorrow. */
                    ZonedDateTime queueHistoryNextRun = setupStoreForTomorrow(bizStore, nowDayOfWeek);
                    resetStoreOfToday(bizStore, nowDayOfWeek);

                    StatsBizStoreDailyEntity bizStoreRating = statsBizStoreDailyManager.computeRatingForEachQueue(bizStore.getId());
                    if (null != bizStoreRating) {
                        bizStoreManager.updateNextRunAndRatingWithAverageServiceTime(
                                bizStore.getId(),
                                bizStore.getTimeZone(),
                                /* Converting to date remove everything to do with UTC, hence important to run server on UTC time. */
                                Date.from(queueHistoryNextRun.toInstant()),
                                (float) bizStoreRating.getTotalRating() / bizStoreRating.getTotalCustomerRated(),
                                bizStoreRating.getTotalCustomerRated(),
                                //TODO(hth) should we compute with yesterday average time or overall average time?
                                statsBizStoreDaily.getAverageServiceTime());
                    } else {
                        bizStoreManager.updateNextRun(
                                bizStore.getId(),
                                bizStore.getTimeZone(),
                                Date.from(queueHistoryNextRun.toInstant()));
                    }

                    tokenQueueManager.resetForNewDay(bizStore.getCodeQR());

                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail to RDB bizStore={} codeQR={} reason={}",
                            bizStore.getId(),
                            bizStore.getCodeQR(),
                            e.getLocalizedMessage(),
                            e);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to execute QueueHistory move to RDB");
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
            bizService.unsetScheduledTask(bizStore.getId());
            scheduledTaskManager.inActive(bizStore.getScheduledTaskId());
        }
    }

    /**
     * Saves daily stats for BizStore Queues.
     *
     * @param bizStoreId
     * @param bizNameId
     * @param queues
     */
    private StatsBizStoreDailyEntity saveDailyStat(
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
                if (queue.hasClientVisitedThisStore()) {
                    clientsVisitedThisStore++;
                }

                if (queue.hasClientVisitedThisBusiness()) {
                    clientsVisitedThisBusiness++;
                }

                switch (queue.getQueueUserState()) {
                    case S:
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

        statsBizStoreDailyManager.save(statsBizStoreDaily);
        LOG.info("Saved daily store stat={}", statsBizStoreDaily);
        return statsBizStoreDaily;
    }
}
