package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.StatsCronEntity;
import com.noqapp.repository.ForgotRecoverManager;
import com.noqapp.service.StatsCronService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 9/24/17 12:51 PM
 */
@SuppressWarnings({
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
    private StatsCronService statsCronService;

    @Autowired
    public ExpireRecordsProcess(
        @Value("${ExpireRecordsProcess.forgotPasswordSwitch}")
        String forgotPasswordSwitch,

        ForgotRecoverManager forgotRecoverManager,
        StatsCronService statsCronService
    ) {
        this.forgotPasswordSwitch = forgotPasswordSwitch;

        this.forgotRecoverManager = forgotRecoverManager;
        this.statsCronService = statsCronService;
    }

    /**
     * Expires the password reset link after stipulated time.
     */
    @Scheduled(fixedDelayString = "${loader.ExpireRecordsProcess.markExpiredForgotPassword}")
    public void markExpiredForgotPassword() {
        StatsCronEntity statsCron = new StatsCronEntity(
            ExpireRecordsProcess.class.getName(),
            "MarkExpiredForgotPassword",
            forgotPasswordSwitch);

        if ("OFF".equalsIgnoreCase(forgotPasswordSwitch)) {
            return;
        }

        int failure = 0;
        long recordsModified = 0;
        try {
            recordsModified = forgotRecoverManager.markInActiveAllOlderThanThreeHours();
        } catch (Exception e) {
            LOG.error("Failed marking records in active older than three hours, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != recordsModified || 0 != failure) {
                statsCron.addStats("failure", failure);
                statsCron.addStats("success", recordsModified);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete recordsModified={} failure={}", recordsModified, failure);
            }
        }
    }
}
