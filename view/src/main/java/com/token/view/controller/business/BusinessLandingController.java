package com.token.view.controller.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.token.domain.BizNameEntity;
import com.token.domain.BusinessUserEntity;
import com.token.domain.analytic.BizDimensionEntity;
import com.token.domain.site.TokenUser;
import com.token.service.BusinessUserService;
import com.token.service.analytic.BizDimensionService;
import com.token.view.form.business.BusinessLandingForm;

/**
 * User: hitender
 * Date: 12/7/16 11:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business")
public class BusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLandingController.class);

    private String nextPage;
    private String migrateBusinessRegistrationFlow;

    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;

    @Autowired
    public BusinessLandingController(
            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${migrateBusinessRegistrationFlow:redirect:/migrate/business/registration.htm}")
            String migrateBusinessRegistrationFlow,

            BusinessUserService businessUserService,
            BizDimensionService bizDimensionService
    ) {
        this.nextPage = nextPage;
        this.businessUserService = businessUserService;
        this.migrateBusinessRegistrationFlow = migrateBusinessRegistrationFlow;
        this.bizDimensionService = bizDimensionService;
    }

    /**
     * Loading landing page for business.
     *
     * @param businessLandingForm
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_BUSINESS')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(@ModelAttribute ("businessLandingForm") BusinessLandingForm businessLandingForm) {
        TokenUser receiptUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(receiptUser.getRid());
        return nextPage(businessUser, businessLandingForm);
    }

    private String nextPage(
            BusinessUserEntity businessUser,
            BusinessLandingForm businessLandingForm) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                populateBusinessLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case C:
            case I:
            case N:
                LOG.info("Migrate to business registration rid={} level={}", businessUser.getReceiptUserId(), businessUser.getUserLevel());
                return migrateBusinessRegistrationFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    private void populateBusinessLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
        Assert.notNull(businessUser, "Business user should not be null");
        BizNameEntity bizName = businessUser.getBizName();
        String bizNameId = bizName.getId();
        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());

        BizDimensionEntity bizDimension = bizDimensionService.findBy(bizNameId);
        if (null != bizDimension) {
            businessLandingForm.setBizName(bizDimension.getBizName());
        }
    }

}
