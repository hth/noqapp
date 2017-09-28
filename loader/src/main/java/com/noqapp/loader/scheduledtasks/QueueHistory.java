package com.noqapp.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreDailyStatEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.repository.BizStoreDailyStatManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.BizService;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.ExternalService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 3/10/17 2:57 PM
 */
@SuppressWarnings ({
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
    private BizStoreDailyStatManager bizStoreDailyStatManager;
    private QueueManager queueManager;
    private TokenQueueManager tokenQueueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private StatsCronService statsCronService;
    private ExternalService externalService;
    private BizService bizService;

    private StatsCronEntity statsCron;

    @Autowired
    public QueueHistory(
            @Value ("${QueueHistory.moveToRDBS}")
            String moveToRDBS,

            BizStoreManager bizStoreManager,
            BizStoreDailyStatManager bizStoreDailyStatManager,
            QueueManager queueManager,
            TokenQueueManager tokenQueueManager,
            QueueManagerJDBC queueManagerJDBC,
            StatsCronService statsCronService,
            ExternalService externalService,
            BizService bizService
    ) {
        this.moveToRDBS = moveToRDBS;
        this.bizStoreManager = bizStoreManager;
        this.bizStoreDailyStatManager = bizStoreDailyStatManager;
        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.statsCronService = statsCronService;
        this.externalService = externalService;
        this.bizService = bizService;
    }

    @Scheduled (fixedDelayString = "${loader.QueueHistory.queuePastData}")
    public void queuePastData() {
        statsCron = new StatsCronEntity(
                QueueHistory.class.getName(),
                "QueueHistory",
                moveToRDBS);

        int found, failure = 0, success = 0;
        if ("OFF".equalsIgnoreCase(moveToRDBS)) {
            LOG.debug("feature is {}", moveToRDBS);
        }

        /*
         * Date is based on UTC time of the System.
         * Hence its important to run on UTC time.
         * Added lag of 5 minutes.
         */
        Date date = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
        List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueEndedForTheDay(date);
        found = bizStores.size();
        LOG.info("found={} date={}", found, date);

        try {
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());
                    List<QueueEntity> queues = queueManager.findByCodeQR(bizStore.getCodeQR());
                    try {
                        saveDailyStat(bizStore.getId(), bizStore.getBizName().getId(), queues);
                        queueManagerJDBC.batchQueues(queues);
                    } catch (DataIntegrityViolationException e) {
                        LOG.error("Failed bulk update. Doing complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        queueManagerJDBC.rollbackQueues(queues);
                        LOG.error("Completed rollback for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        throw e;
                    }

                    bizStore.setStoreHours(bizService.findAllStoreHours(bizStore.getId()));
                    int deleted = queueManager.deleteByCodeQR(bizStore.getCodeQR());
                    if (queues.size() == deleted) {
                        LOG.info("Deleted and insert exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                    } else {
                        LOG.error("Mis-match in deleted and insert bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
                    }

                    TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
                    StoreHourEntity storeHour = bizStore.getStoreHours().get(zonedDateTime.getDayOfWeek().getValue() - 1);
                    Date nextDay = externalService.computeNextRunTimeAtUTC(
                            timeZone,
                            /* When closed set hour to 23 and minute to 59. */
                            storeHour.isDayClosed() ? 23 : storeHour.storeClosingHourOfDay(),
                            storeHour.isDayClosed() ? 59 : storeHour.storeClosingMinuteOfDay());

                    BizStoreDailyStatEntity bizStoreDailyStat = bizStoreDailyStatManager.computeRatingForEachQueue(bizStore.getId());
                    if (bizStoreDailyStat != null) {
                        bizStoreManager.updateNextRunAndRating(
                                bizStore.getId(),
                                bizStore.getTimeZone(),
                                nextDay,
                                (float) bizStoreDailyStat.getTotalRating() / bizStoreDailyStat.getTotalCustomerRated(),
                                bizStoreDailyStat.getTotalCustomerRated());
                    } else {
                        bizStoreManager.updateNextRun(
                                bizStore.getId(),
                                bizStore.getTimeZone(),
                                nextDay);
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
     * Saves daily stats for BizStore Queues.
     *
     * @param bizStoreId
     * @param bizNameId
     * @param queues
     */
    private void saveDailyStat(String bizStoreId, String bizNameId, List<QueueEntity> queues) {
        long totalServiceTime = 0, totalHoursSaved = 0;
        int totalCustomerServed = 0, totalRating = 0, totalCustomerRated = 0;
        for (QueueEntity queue : queues) {
            switch (queue.getQueueUserState()) {
                case S:
                    totalServiceTime += queue.timeTakenForService();
                    if (queue.getRatingCount() > 0) {
                        totalRating += queue.getRatingCount();
                        totalCustomerRated++;
                    }
                    totalCustomerServed++;
                    int hours;
                    switch (queue.getHoursSaved()) {
                        case 1:
                            /* Half hour. */
                            hours = 30;
                            break;
                        case 2:
                            /* One hour. */
                            hours = 30 * 2;
                            break;
                        case 3:
                            /* Two hours. */
                            hours = 30 * 4;
                            break;
                        case 4:
                            /* Three hours. */
                            hours = 30 * 6;
                            break;
                        case 5:
                            /* Four hours. */
                            hours = 30 * 8;
                            break;
                        default:
                            /* Eight hours. */
                            hours = 30 * 16;
                    }

                    totalHoursSaved += hours * 60 * 1000;
                    break;
            }
        }
        BizStoreDailyStatEntity bizStoreDailyStat = new BizStoreDailyStatEntity();
        bizStoreDailyStat.setBizStoreId(bizStoreId);
        bizStoreDailyStat.setBizNameId(bizNameId);
        /* Service time is auto calculated. */
        bizStoreDailyStat.setTotalServiceTime(totalServiceTime);
        bizStoreDailyStat.setTotalCustomerServed(totalCustomerServed);
        /* Rating and hours saved is computed only for people who have rated. This comes from review screen. */
        bizStoreDailyStat.setTotalRating(totalRating);
        bizStoreDailyStat.setTotalCustomerRated(totalCustomerRated);
        bizStoreDailyStat.setTotalHoursSaved(totalHoursSaved);
        bizStoreDailyStatManager.save(bizStoreDailyStat);
        LOG.debug("Saved daily stat={}", bizStoreDailyStat);
    }
}
