package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.StatsCronEntity;
import com.noqapp.repository.InviteManager;
import com.noqapp.service.StatsCronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Auto increase the number of remote joins.
 *
 * hitender
 * 2/5/18 3:42 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class RemoteJoin {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteJoin.class);

    private String increaseRemoteJoinSwitch;

    private InviteManager inviteManager;
    private StatsCronService statsCronService;

    @Autowired
    public RemoteJoin(
            @Value("${RemoteJoin.increaseRemoteJoinSwitch:ON}")
            String increaseRemoteJoinSwitch,

            InviteManager inviteManager,
            StatsCronService statsCronService
    ) {
        this.increaseRemoteJoinSwitch = increaseRemoteJoinSwitch;

        this.inviteManager = inviteManager;
        this.statsCronService = statsCronService;
    }

    /**
     * Increase Remote Joins when they fall below certain number.
     */
    @Scheduled(fixedDelayString = "${loader.ExpireRecordsProcess.markExpiredForgotPassword}")
    public void increaseRemoteJoin() {
        StatsCronEntity statsCron = new StatsCronEntity(
                ExpireRecordsProcess.class.getName(),
                "IncreaseRemoteJoin",
                increaseRemoteJoinSwitch);

        if ("OFF".equalsIgnoreCase(increaseRemoteJoinSwitch)) {
            return;
        }

        int failure = 0;
        long success = 0;
        try {
            success = inviteManager.increaseRemoteJoin(25);
        } catch (Exception e) {
            LOG.error("Failed marking records in active older than three hours, reason={}", e.getLocalizedMessage(), e);
            failure++;
        } finally {
            if (0 != success || 0 !=  failure) {
                statsCron.addStats("failure", failure);
                statsCron.addStats("success", success);
                statsCronService.save(statsCron);

                /* Without if condition its too noisy. */
                LOG.info("Complete success={} failure={}", success, failure);
            }
        }
    }
}
