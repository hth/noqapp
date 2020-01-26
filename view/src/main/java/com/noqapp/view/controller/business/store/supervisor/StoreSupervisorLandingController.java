package com.noqapp.view.controller.business.store.supervisor;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.json.JsonTopic;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.QueueService;
import com.noqapp.view.form.business.BusinessLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * All queue and order supervisor comes to this controller.
 * It lists all the assigned stores to be supervised.
 *
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
@RequestMapping (value = "/business/store/sup")
public class StoreSupervisorLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreSupervisorLandingController.class);

    private String nextPage;
    private String migrateBusinessProfileFlow;

    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private QueueService queueService;
    private BizService bizService;

    @Autowired
    public StoreSupervisorLandingController(
        @Value("${nextPage:/business/storeSupervisorLanding}")
        String nextPage,

        @Value("${migrateBusinessProfileFlow:redirect:/migrate/business/profile.htm}")
        String migrateBusinessProfileFlow,

        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        QueueService queueService,
        BizService bizService
    ) {
        this.nextPage = nextPage;
        this.migrateBusinessProfileFlow = migrateBusinessProfileFlow;

        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.queueService = queueService;
        this.bizService = bizService;
    }

    /**
     * Loading landing page for user with queue supervisor role.
     *
     * @param businessLandingForm
     * @return
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute ("businessLandingForm")
        BusinessLandingForm businessLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        return nextPage(businessUser, businessLandingForm);
    }

    @SuppressWarnings("Duplicates")
    private String nextPage(
        BusinessUserEntity businessUser,
        BusinessLandingForm businessLandingForm
    ) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                populateLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case N:
                businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
                businessUserService.save(businessUser);
                /* After setting status as incomplete, continue to call migrateBusinessProfileFlow. */
                LOG.info("Migrate to business registration qid={} userLevel={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessProfileFlow;
            case C:
            case I:
                LOG.info("Migrate to business registration qid={} userLevel={}", businessUser.getQueueUserId(), businessUser.getUserLevel());
                return migrateBusinessProfileFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    private void populateLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
        Assert.notNull(businessUser, "Business user should not be null");
        BizNameEntity bizName = businessUser.getBizName();
        businessLandingForm
            .setBizName(bizName.getBusinessName())
            .setBusinessType(bizName.getBusinessType());
        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());
        List<JsonTopic> jsonTopics = businessUserStoreService.getAssignedTokenAndQueues(businessUser.getQueueUserId());
        businessLandingForm.setJsonTopics(jsonTopics);

        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        for (BizStoreEntity bizStore : bizStores) {
            QueueDetail queueDetail = new QueueDetail()
                    .setId(bizStore.getCodeQR())
                    .setPreviouslyVisitedClientCount(queueService.getPreviouslyVisitedClientCount(bizStore.getCodeQR()))
                    .setNewVisitClientCount(queueService.getNewVisitClientCount(bizStore.getCodeQR()));

            businessLandingForm.addQueueDetail(queueDetail);
        }
    }
}
