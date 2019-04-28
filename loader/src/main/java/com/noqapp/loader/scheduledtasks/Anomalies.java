package com.noqapp.loader.scheduledtasks;

import com.noqapp.service.anomaly.MissingGeneratedUserId;
import com.noqapp.service.anomaly.UserAuthenticationAnomaly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 2019-04-28 06:05
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class Anomalies {
    private static final Logger LOG = LoggerFactory.getLogger(Anomalies.class);

    private String oneTimeStatusSwitch;

    private Environment environment;
    private UserAuthenticationAnomaly userAuthenticationAnomaly;
    private MissingGeneratedUserId missingGeneratedUserId;

    @Autowired
    public Anomalies(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        UserAuthenticationAnomaly userAuthenticationAnomaly,
        MissingGeneratedUserId missingGeneratedUserId
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));

        this.userAuthenticationAnomaly = userAuthenticationAnomaly;
        this.missingGeneratedUserId = missingGeneratedUserId;
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @SuppressWarnings("all")
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void someTask() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Run someTask in AnyTask");

        /* Write your method after here. Un-comment @Scheduled. */

        userAuthenticationAnomaly.listOrphanData();
        missingGeneratedUserId.populateWithMissingQID();
    }
}
