package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.catgeory.MarketplaceRejectReasonEnum;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.StatsCronService;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * hitender
 * 11/9/21 7:56 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class MarketplaceRejectionWhenMissingImage {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceRejectionWhenMissingImage.class);

    private String autoRejectMarketplaceWhenMissingImageSwitch;

    private HouseholdItemService householdItemService;
    private PropertyRentalService propertyRentalService;
    private StatsCronService statsCronService;

    private UserProfileManager userProfileManager;

    private Environment environment;

    @Autowired
    public MarketplaceRejectionWhenMissingImage(
        @Value("${autoRejectMarketplaceWhenMissingImageSwitch:ON}")
        String autoRejectMarketplaceWhenMissingImageSwitch,

        Environment environment,

        UserProfileManager userProfileManager,

        HouseholdItemService householdItemService,
        PropertyRentalService propertyRentalService,
        StatsCronService statsCronService
    ) {
        this.autoRejectMarketplaceWhenMissingImageSwitch = autoRejectMarketplaceWhenMissingImageSwitch;
        this.environment = environment;

        this.userProfileManager = userProfileManager;

        this.householdItemService = householdItemService;
        this.propertyRentalService = propertyRentalService;
        this.statsCronService = statsCronService;
    }

    @Scheduled(fixedDelayString = "${loader.FilesUploadToS3.uploadOnS3}")
    public void autoRejectMarketplaceWhenMissingImage() {
        StatsCronEntity statsCron = new StatsCronEntity(
            MarketplaceRejectionWhenMissingImage.class.getName(),
            "autoRejectMarketplaceWhenMissingImage",
            autoRejectMarketplaceWhenMissingImageSwitch);

        if ("OFF".equalsIgnoreCase(autoRejectMarketplaceWhenMissingImageSwitch)) {
            LOG.warn("Auto Reject Marketplace Post is {}", autoRejectMarketplaceWhenMissingImageSwitch);
            return;
        }

        int success = 0, failure = 0;
        try {
            List<HouseholdItemEntity> householdItems = householdItemService.findAllPendingApprovalWithoutImage();
            for (HouseholdItemEntity householdItem : householdItems) {
                try {
                    householdItemService.changeStatusOfMarketplace(
                        householdItem.getId(),
                        ActionTypeEnum.REJECT,
                        MarketplaceRejectReasonEnum.ADIM,
                        environment.getProperty("build.env").equalsIgnoreCase("prod")
                            ? userProfileManager.findOneByMail("beta@noqapp.com").getQueueUserId()
                            : "100000000002");
                    success ++;
                } catch (Exception e) {
                    LOG.error("Failed auto rejecting household item reason={}", e.getLocalizedMessage(), e);
                    failure ++;
                }
            }

            List<PropertyRentalEntity> propertyRentals = propertyRentalService.findAllPendingApprovalWithoutImage();
            for (PropertyRentalEntity propertyRental : propertyRentals) {
                try {
                    propertyRentalService.changeStatusOfMarketplace(
                        propertyRental.getId(),
                        ActionTypeEnum.REJECT,
                        MarketplaceRejectReasonEnum.ADIM,
                        environment.getProperty("build.env").equalsIgnoreCase("prod")
                            ? userProfileManager.findOneByMail("beta@noqapp.com").getQueueUserId()
                            : "100000000002");
                    success ++;
                } catch (Exception e) {
                    LOG.error("Failed auto rejecting property rental item reason={}", e.getLocalizedMessage(), e);
                    failure ++;
                }
            }
        } catch (Exception e) {
            LOG.error("Failed rejecting marketplace post reason={}", e.getLocalizedMessage(), e);
        } finally {
            if (0 != success || 0 != failure) {
                statsCron.addStats("failure", failure);
                statsCron.addStats("success", success);
                statsCronService.save(statsCron);

                /* Without if condition it is too noisy. */
                LOG.info("Complete marketplace post reject without image recordsFound={} failure={}", success, failure);
            }
        }
    }
}
