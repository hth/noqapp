package com.noqapp.view.controller.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.TokenUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.LandingForm;

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

    /**
     * Refers to landing.jsp
     */
    private String nextPage;
    private String migrateToBusinessRegistrationFlowActions;

    private BusinessUserService businessUserService;

    @Autowired
    public LandingController(
            @Value ("${nextPage:/access/landing}")
            String nextPage,

            @Value ("${migrateToBusinessRegistrationFlowActions:redirect:/migrate/business/registration.htm}")
            String migrateToBusinessRegistrationFlowActions,

            BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.migrateToBusinessRegistrationFlowActions = migrateToBusinessRegistrationFlowActions;
        this.businessUserService = businessUserService;
    }

    @Timed
    @ExceptionMetered
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET
    )
    public String loadForm(
            @ModelAttribute ("landingForm")
            LandingForm landingForm
    ) {
        LOG.info("Landed on next page");
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(tokenUser.getRid());
        if (null != businessUser) {
            landingForm.setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus());
            landingForm.setBusinessAccountSignedUp(businessUser.getUpdated());
        }

        return nextPage;
    }

    @Timed
    @ExceptionMetered
    @RequestMapping (
            value = "/landing/business/migrate",
            method = RequestMethod.GET
    )
    public String businessMigrate() {
        LOG.info("Requested business registration {}", migrateToBusinessRegistrationFlowActions);
        return migrateToBusinessRegistrationFlowActions;
    }
}
