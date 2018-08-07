package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.AccountService;

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
 * hitender
 * 1/13/18 6:17 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private AccountService accountService;
    private UserProfileManager userProfileManager;
    private Environment environment;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        AccountService accountService,
        UserProfileManager userProfileManager,
        Environment environment
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;
        this.accountService = accountService;
        this.userProfileManager = userProfileManager;

        this.environment = environment;
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
        //Update account role
        int count = 0;
        List<UserProfileEntity> userProfiles = userProfileManager.findAll();
        for (UserProfileEntity userProfile : userProfiles) {
            UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(userProfile.getQueueUserId(), userProfile.getLevel());
            accountService.save(userAccount);
            count ++;
        }
        LOG.info("Updated Roles count={}", count);
    }
}
