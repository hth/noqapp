package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ProfessionalProfileService;
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
    private BusinessUserStoreService businessUserStoreService;
    private ProfessionalProfileService professionalProfileService;
    private BizService bizService;
    private AccountService accountService;
    private UserProfileManager userProfileManager;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            Environment environment,
            BusinessUserStoreService businessUserStoreService,
            ProfessionalProfileService professionalProfileService,
            BizService bizService,
            AccountService accountService,
            UserProfileManager userProfileManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.businessUserStoreService = businessUserStoreService;
        this.professionalProfileService = professionalProfileService;
        this.bizService = bizService;
        this.accountService = accountService;
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
        List<UserProfileEntity> userAccounts = userProfileManager.findAll();
        for (UserProfileEntity userAccount : userAccounts) {
            switch (userAccount.getLevel()) {
                case S_MANAGER:
                    ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(userAccount.getQueueUserId());
                    List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(userAccount.getQueueUserId());
                    for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
                        professionalProfile.addManagerAtStoreCodeQR(businessUserStore.getCodeQR());
                    }
                    professionalProfileService.save(professionalProfile);
                    break;
                default:
                    LOG.info("Skipped qid={} role={}", userAccount.getQueueUserId(), userAccount.getLevel());
            }
        }
    }
}
