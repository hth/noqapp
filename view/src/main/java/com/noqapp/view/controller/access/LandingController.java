package com.noqapp.view.controller.access;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.QueueService;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;
import com.noqapp.view.form.LandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.Instant;

/**
 * User: hitender
 * Date: 12/6/16 8:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class LandingController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    public static final String SUCCESS = "success";

    private String nextPage;
    private String migrateToBusinessRegistrationFlowActions;

    private BusinessUserService businessUserService;
    private QueueService queueService;
    private ApiHealthService apiHealthService;
    private AccountService accountService;
    private PropertyRentalService propertyRentalService;
    private HouseholdItemService householdItemService;

    @Autowired
    public LandingController(
        @Value ("${nextPage:/access/landing}")
        String nextPage,

        @Value ("${migrateToBusinessRegistrationFlowActions:redirect:/migrate/business/registration}")
        String migrateToBusinessRegistrationFlowActions,

        BusinessUserService businessUserService,
        QueueService queueService,
        ApiHealthService apiHealthService,
        AccountService accountService,
        PropertyRentalService propertyRentalService,
        HouseholdItemService householdItemService
    ) {
        this.nextPage = nextPage;
        this.migrateToBusinessRegistrationFlowActions = migrateToBusinessRegistrationFlowActions;

        this.businessUserService = businessUserService;
        this.queueService = queueService;
        this.apiHealthService = apiHealthService;
        this.accountService = accountService;
        this.propertyRentalService = propertyRentalService;
        this.householdItemService = householdItemService;
    }

    @GetMapping(value = "/landing")
    public String landing(
        @ModelAttribute("landingForm")
        LandingForm landingForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null != businessUser) {
            landingForm
                .setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus())
                .setBusinessAccountSignedUp(businessUser.getUpdated());
        }

        landingForm
            .setCurrentQueues(queueService.findAllQueuedByQid(queueUser.getQueueUserId()))
            .setHistoricalQueues(queueService.findAllHistoricalQueue(queueUser.getQueueUserId()))
            .setMinorUserProfiles(accountService.findDependentProfiles(queueUser.getQueueUserId()))
            .addPropertyMarketplaceForm(propertyRentalService.findPostedByMeOnMarketplace(queueUser.getQueueUserId()))
            .addHouseholdItemMarketplaceForm(householdItemService.findPostedByMeOnMarketplace(queueUser.getQueueUserId()));

        LOG.info("Current size={} and Historical size={}", landingForm.getCurrentQueues().size(), landingForm.getHistoricalQueues().size());
        apiHealthService.insert(
            "/landing",
            "landing",
            LandingController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return nextPage;
    }

    @GetMapping(value = "/landing/business/migrate")
    public String businessMigrate() {
        LOG.info("Requested business registration {}", migrateToBusinessRegistrationFlowActions);
        return migrateToBusinessRegistrationFlowActions;
    }
}
