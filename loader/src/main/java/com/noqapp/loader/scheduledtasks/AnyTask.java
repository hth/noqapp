package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.repository.BizNameManager;
import com.noqapp.service.BizService;
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
    private BizNameManager bizNameManager;
    private BizService bizService;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            Environment environment,
            BizNameManager bizNameManager,
            BizService bizService
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.bizNameManager = bizNameManager;
        this.bizService = bizService;
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

        List<BizNameEntity> bizNames = bizNameManager.findAll(0, 100);
        for (BizNameEntity bizName : bizNames) {
            String webLocation = bizService.buildWebLocationForBiz(
                    bizName.getTown(),
                    bizName.getStateShortName(),
                    bizName.getCountryShortName(),
                    bizName.getBusinessName()
            );
            bizName.setWebLocation(webLocation);
            bizService.saveName(bizName);
        }
    }
}
