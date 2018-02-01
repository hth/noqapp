package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.repository.BizNameManager;
import com.noqapp.repository.BizStoreManager;
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
    private BizStoreManager bizStoreManager;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:OFF}")
            String oneTimeStatusSwitch,

            Environment environment,
            BizNameManager bizNameManager,
            BizService bizService,
            BizStoreManager bizStoreManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.bizNameManager = bizNameManager;
        this.bizService = bizService;
        this.bizStoreManager = bizStoreManager;
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
                    bizName.getBusinessName(),
                    bizName.getId()
            );
            bizName.setWebLocation(webLocation);
            bizService.saveName(bizName);
        }

        List<BizStoreEntity> bizStores = bizStoreManager.getAll(0, 1000);
        for (BizStoreEntity bizStore : bizStores) {
            String webLocation = bizService.buildWebLocationForStore(
                    bizStore.getTown(),
                    bizStore.getStateShortName(),
                    bizStore.getCountryShortName(),
                    bizStore.getBizName().getBusinessName(),
                    bizStore.getDisplayName(),
                    bizStore.getId()
            );
            bizStore.setWebLocation(webLocation);
            bizService.saveStore(bizStore);
        }
    }
}
