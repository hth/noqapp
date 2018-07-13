package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.UserProfileEntity;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.UserProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mostly used one time to update, modify any data.
 *
 * hitender
 * 1/13/18 6:17 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private Environment environment;
    private BusinessUserStoreManager businessUserStoreManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            Environment environment,
            BusinessUserStoreManager businessUserStoreManager,
            UserProfileManager userProfileManager) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.businessUserStoreManager = businessUserStoreManager;
        this.userProfileManager = userProfileManager;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
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
        long count = 0;
        List<UserProfileEntity> userProfiles = userProfileManager.findAll();
        for (UserProfileEntity userProfile : userProfiles) {
            switch (userProfile.getLevel()) {
                case S_MANAGER:
                    count += businessUserStoreManager.updateUserLevel(userProfile.getQueueUserId(), userProfile.getLevel());
                    break;
                default:
                    LOG.info("Skipped for qid={} level={}", userProfile.getQueueUserId(), userProfile.getLevel());
            }
        }

        LOG.info("Update count={}", count);
    }
}
