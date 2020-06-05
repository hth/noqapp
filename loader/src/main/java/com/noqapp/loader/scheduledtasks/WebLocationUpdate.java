package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.service.BizService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * To be confirmed if updates are happening before deleting it.
 * hitender
 * 6/5/20 4:03 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class WebLocationUpdate {
    private static final Logger LOG = LoggerFactory.getLogger(WebLocationUpdate.class);

    private String oneTimeStatusSwitch;

    private BizService bizService;

    @Autowired
    public WebLocationUpdate(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        BizService bizService
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.bizService = bizService;
    }

    /**
     * Update web location periodically when business store or names of business store is changed.
     * This is a fall back process. Mostly update should happen when names are changed.
     */
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void updateWebLocationOfStores() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Updating web location of stores");

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
