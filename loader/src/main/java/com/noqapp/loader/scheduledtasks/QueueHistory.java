package com.noqapp.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CronStatsEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.BizService;
import com.noqapp.service.CronStatsService;
import com.noqapp.service.ExternalService;

import java.time.ZonedDateTime;
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
    private QueueManager queueManager;
    private TokenQueueManager tokenQueueManager;
    private QueueManagerJDBC queueManagerJDBC;
    private CronStatsService cronStatsService;
    private ExternalService externalService;
    private BizService bizService;

    private CronStatsEntity cronStats;

    @Autowired
    public QueueHistory(
            @Value ("${QueueHistory.moveToRDBS}")
            String moveToRDBS,

            BizStoreManager bizStoreManager,
            QueueManager queueManager,
            TokenQueueManager tokenQueueManager,
            QueueManagerJDBC queueManagerJDBC,
            CronStatsService cronStatsService,
            ExternalService externalService,
            BizService bizService
    ) {
        this.moveToRDBS = moveToRDBS;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.cronStatsService = cronStatsService;
        this.externalService = externalService;
        this.bizService = bizService;
    }

    @Scheduled (fixedDelayString = "${loader.QueueHistory.queuePastData}")
    public void queuePastData() {
        cronStats = new CronStatsEntity(
                QueueHistory.class.getName(),
                "QueueHistory",
                moveToRDBS);

        int found, failure = 0, success = 0;
        if ("OFF".equalsIgnoreCase(moveToRDBS)) {
            LOG.debug("feature is {}", moveToRDBS);
        }

        /* Date is based on UTC time of the System. Hence its important to run on UTC time. */
        List<BizStoreEntity> bizStores = bizStoreManager.findAllQueueEndedForTheDay(new Date());
        found = bizStores.size();

        try {
            for (BizStoreEntity bizStore : bizStores) {
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());
                    List<QueueEntity> queues = queueManager.findByCodeQR(bizStore.getCodeQR());
                    try {
                        queueManagerJDBC.batchQueues(queues);
                    } catch (DataIntegrityViolationException e) {
                        LOG.error("Failed bulk update. Doing complete rollback bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        queueManagerJDBC.rollbackQueues(queues);
                        LOG.error("Completed rollback for bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                        throw e;
                    }

                    bizStore.setStoreHours(bizService.finalAllStoreHours(bizStore.getId()));
                    int deleted = queueManager.deleteByCodeQR(bizStore.getCodeQR());
                    if (queues.size() == deleted) {
                        LOG.info("Deleted and insert exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                    } else {
                        LOG.error("Mis-match in deleted and insert bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
                    }

                    TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
                    Date nextDay = externalService.computeNextRunTimeAtUTC(
                            timeZone,
                            bizStore.getStoreHours().get(zonedDateTime.getDayOfWeek().getValue() - 1).storeClosingHourOfDay(),
                            bizStore.getStoreHours().get(zonedDateTime.getDayOfWeek().getValue() - 1).storeClosingMinuteOfDay());

                    bizStoreManager.setNextRun(bizStore.getId(), bizStore.getTimeZone(), nextDay);
                    tokenQueueManager.resetForNewDay(bizStore.getCodeQR());

                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail to RDB bizStore={} codeQR={} reason={}", bizStore.getId(), bizStore.getCodeQR(), e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to execute QueueHistory move to RDB");
        } finally {
            if (0 != found || 0 != failure || 0 != success) {
                cronStats.addStats("found", found);
                cronStats.addStats("failure", failure);
                cronStats.addStats("success", success);
                cronStatsService.save(cronStats);

                /* Without if condition its too noisy. */
                LOG.info("complete found={} failure={} success={}", found, failure, success);
            }
        }
    }
}
