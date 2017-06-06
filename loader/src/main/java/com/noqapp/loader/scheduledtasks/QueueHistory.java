package com.noqapp.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CronStatsEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.CronStatsService;
import com.noqapp.service.ExternalService;

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
            ExternalService externalService
    ) {
        this.moveToRDBS = moveToRDBS;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.tokenQueueManager = tokenQueueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.cronStatsService = cronStatsService;
        this.externalService = externalService;
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
                    List<QueueEntity> queues = queueManager.findByCodeQR(bizStore.getCodeQR());
                    queueManagerJDBC.batchQueue(queues);
                    int deleted = queueManager.deleteByCodeQR(bizStore.getCodeQR());
                    if (queues.size() == deleted) {
                        LOG.info("deleted and insert exact bizStore={} codeQR={}", bizStore.getId(), bizStore.getCodeQR());
                    } else {
                        LOG.error("mis-match in deleted and insert bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
                    }

                    TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZone());
                    Date nextDay = externalService.computeNextRunTimeAtUTC(
                            timeZone,
                            bizStore.storeClosingHourOfDay(),
                            bizStore.storeClosingMinuteOfDay());
                    
                    bizStoreManager.setNextRun(bizStore.getId(), bizStore.getTimeZone(), nextDay);
                    tokenQueueManager.resetForNewDay(bizStore.getCodeQR());

                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail to RDB bizStore={} reason={}", bizStore.getId(), e.getLocalizedMessage(), e);
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
