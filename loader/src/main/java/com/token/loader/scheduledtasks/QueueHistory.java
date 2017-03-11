package com.token.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.token.domain.BizStoreEntity;
import com.token.domain.CronStatsEntity;
import com.token.domain.QueueEntity;
import com.token.loader.repository.QueueManagerJDBC;
import com.token.repository.BizStoreManager;
import com.token.repository.QueueManager;
import com.token.service.CronStatsService;
import com.token.service.ExternalService;

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
            QueueManagerJDBC queueManagerJDBC,
            CronStatsService cronStatsService,
            ExternalService externalService
    ) {
        this.moveToRDBS = moveToRDBS;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
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
                        LOG.info("deleted and insert exact bizStore={}", bizStore.getId());
                    } else {
                        LOG.error("mis-match in deleted and insert bizStore={} size={} delete={}", bizStore.getId(), queues.size(), deleted);
                    }

                    TimeZone timeZone = TimeZone.getTimeZone(bizStore.getTimeZoneId());
                    Date nextDay = externalService.computeNextRunTimeAtUTC(timeZone, bizStore.storeClosingHourOfDay(), bizStore.storeClosingMinuteOfDay());
                    bizStoreManager.setZoneIdAndQueueHistory(bizStore.getId(), bizStore.getTimeZoneId(), nextDay);
                    success++;
                } catch (Exception e) {
                    failure++;
                    LOG.error("Insert fail to RDB bizStore={}", bizStore.getId());
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to execute QueueHistory move to RDBS");
        } finally {
            if (found != 0 || failure != 0 || success != 0) {
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
