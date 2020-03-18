package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.service.BizService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Mostly used one time to update, modify any data.
 *
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

    private Environment environment;
    private BizService bizService;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        BizService bizService
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
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
        try (Stream<BizStoreEntity> stream = bizService.findAllWithStream()) {
            stream.iterator().forEachRemaining(bizStore -> {
                String webLocation = bizService.buildWebLocationForStore(
                    bizStore.getArea(),
                    bizStore.getTown(),
                    bizStore.getStateShortName(),
                    bizStore.getCountryShortName(),
                    bizStore.getBizName().getBusinessName(),
                    bizStore.getDisplayName(),
                    bizStore.getId(),
                    bizStore.getBizName().getWebLocation()
                );

                LOG.info("Store web Location {} : {}", webLocation, bizStore.getWebLocation());
            });
        }
    }
}
