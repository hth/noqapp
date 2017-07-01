package com.noqapp.view.controller.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.analytic.BizDimensionEntity;
import com.noqapp.domain.site.TokenUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.analytic.BizDimensionService;
import com.noqapp.utils.ScrubbedInput;
import com.noqapp.view.form.business.BusinessLandingForm;
import com.noqapp.view.form.business.QueueManagerForm;

import java.util.List;

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
    private String addStoreFlowActions;
    private String listQueueManagerPage;

    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;

    @Autowired
    public BusinessLandingController(
            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${migrateBusinessRegistrationFlow:redirect:/migrate/business/registration.htm}")
            String migrateBusinessRegistrationFlow,

            @Value ("${addStoreFlowActions:redirect:/store/addStore.htm}")
            String addStoreFlowActions,

            @Value("${listQueueManagerPage:/business/listQueueManager.htm}")
            String listQueueManagerPage,

            BusinessUserService businessUserService,
            BizDimensionService bizDimensionService,
            BizService bizService,
            BusinessUserStoreService businessUserStoreService) {
        this.nextPage = nextPage;
        this.businessUserService = businessUserService;
        this.addStoreFlowActions = addStoreFlowActions;
        this.listQueueManagerPage = listQueueManagerPage;

        this.migrateBusinessRegistrationFlow = migrateBusinessRegistrationFlow;
        this.bizDimensionService = bizDimensionService;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
    }

    /**
     * Loading landing page for business.
     * This link is mapped in web flow after merchant adds new store to existing business.
     *
     * @param businessLandingForm
     * @return
     */
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET,
            produces = "text/html;charset=UTF-8"
    )
    public String landing(
            @ModelAttribute ("businessLandingForm")
            BusinessLandingForm businessLandingForm
    ) {
        TokenUser receiptUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        return nextPage(businessUserService.findBusinessUser(receiptUser.getRid()), businessLandingForm);
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
        } else {
            businessLandingForm.setBizName(bizName.getBusinessName());
        }

        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        businessLandingForm.setBizStores(bizStores);
        for (BizStoreEntity bizStore : bizStores) {
            long assignedToQueue  = businessUserStoreService.findNumberOfPeopleAssignedToQueue(bizStore.getId());
            businessLandingForm.addAssignedQueueManagers(bizStore.getId(), assignedToQueue);
        }
    }

    @RequestMapping (
            value = "/{storeId}/listQueueManager",
            method = RequestMethod.GET,
            produces = "text/html;charset=UTF-8"
    )
    public String listQueueManager(
            @ModelAttribute ("queueManagerForm")
            QueueManagerForm queueManagerForm,

            @PathVariable ("storeId")
            ScrubbedInput storeId
    ) {
        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        queueManagerForm.setQueueName(bizStore.getDisplayName());

        List<UserProfileEntity> userProfiles = businessUserStoreService.getAllQueueManagers(storeId.getText());
        queueManagerForm.setUserProfiles(userProfiles);
        return listQueueManagerPage;
    }

    @Timed
    @ExceptionMetered
    @RequestMapping (
            value = "/addStore",
            method = RequestMethod.GET,
            produces = "text/html;charset=UTF-8"
    )
    public String addStore() {
        LOG.info("Add store to business {}", addStoreFlowActions);
        return addStoreFlowActions;
    }
}
