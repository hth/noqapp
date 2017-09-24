package com.noqapp.view.controller.business.store.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.BusinessLandingForm;

/**
 * User: hitender
 * Date: 7/16/17 7:57 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business/store/queue")
public class QueueSupervisorLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisorLandingController.class);

    private String nextPage;
    private String migrateBusinessProfileFlow;

    private BusinessUserService businessUserService;

    @Autowired
    public QueueSupervisorLandingController(
            @Value ("${nextPage:/business/queueLanding}")
            String nextPage,

            @Value ("${migrateBusinessProfileFlow:redirect:/migrate/business/profile.htm}")
            String migrateBusinessProfileFlow,

            BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.migrateBusinessProfileFlow = migrateBusinessProfileFlow;

        this.businessUserService = businessUserService;
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
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        return nextPage(businessUserService.findBusinessUser(queueUser.getQueueUserId()), businessLandingForm);
    }

    private String nextPage(
            BusinessUserEntity businessUser,
            BusinessLandingForm businessLandingForm) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                //populateLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case N:
                businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
                businessUserService.save(businessUser);
                /* After setting status as incomplete, continue to call migrateBusinessProfileFlow. */
            case C:
            case I:
                LOG.info("Migrate to business registration qid={} level={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessProfileFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

//    private void populateLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
//        Assert.notNull(businessUser, "Business user should not be null");
//        BizNameEntity bizName = businessUser.getBizName();
//        String bizNameId = bizName.getId();
//        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());
//
//        BizDimensionEntity bizDimension = bizDimensionService.findBy(bizNameId);
//        if (null != bizDimension) {
//            businessLandingForm.setBizName(bizDimension.getBizName());
//        } else {
//            businessLandingForm.setBizName(bizName.getBusinessName());
//        }
//
//        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
//        businessLandingForm.setBizStores(bizStores);
//        for (BizStoreEntity bizStore : bizStores) {
//            long assignedToQueue  = businessUserStoreService.findNumberOfPeopleAssignedToQueue(bizStore.getId());
//            businessLandingForm.addAssignedQueueManagers(bizStore.getId(), assignedToQueue);
//        }
//    }
}
