package com.noqapp.loader.scheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.noqapp.domain.CronStatsEntity;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.service.CronStatsService;

/**
 * User: hitender
 * Date: 9/24/17 12:51 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ExpireRecordsProcess {
    private static final Logger LOG = LoggerFactory.getLogger(ExpireRecordsProcess.class);

    private final String forgotPasswordSwitch;

    private ForgotRecoverManager forgotRecoverManager;
    private CronStatsService cronStatsService;

    @Autowired
    public ExpireRecordsProcess(
            @Value ("${ExpireRecordsProcess.forgotPasswordSwitch}")
            String forgotPasswordSwitch,

            ForgotRecoverManager forgotRecoverManager,
            CronStatsService cronStatsService
    ) {
        this.forgotPasswordSwitch = forgotPasswordSwitch;

        this.forgotRecoverManager = forgotRecoverManager;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (fixedDelayString = "${loader.ExpireRecordsProcess.markExpiredForgotPassword}")
    public void markExpiredForgotPassword() {
        CronStatsEntity cronStats = new CronStatsEntity(
                ExpireRecordsProcess.class.getName(),
                "MarkExpiredForgotPassword",
                forgotPasswordSwitch);

        if ("OFF".equalsIgnoreCase(forgotPasswordSwitch)) {
            return;
        }

        int failure = 0;
        int recordsModified = 0;
        try {
            recordsModified = forgotRecoverManager.markInActiveAllOlderThanThreeHours();
        } catch (Exception e) {
            LOG.error("Failed marking records in active older than three hours, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != recordsModified || 0 !=  failure) {
                cronStats.addStats("failure", failure);
                cronStats.addStats("success", recordsModified);
                cronStatsService.save(cronStats);

                /* Without if condition its too noisy. */
                LOG.info("Complete recordsModified={} failure={}", recordsModified, failure);
            }
        }
    }
}
