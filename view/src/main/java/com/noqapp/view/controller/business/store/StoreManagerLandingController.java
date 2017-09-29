package com.noqapp.view.controller.business.store;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.QueueSupervisorApproveRejectForm;
import com.noqapp.view.form.StoreManagerForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Shows current state and analytics of each queue to users with Store Manager Role.
 *
 * User: hitender
 * Date: 12/15/16 9:00 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business/store")
public class StoreManagerLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreManagerLandingController.class);

    private String nextPage;
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private TokenQueueService tokenQueueService;

    @Autowired
    public StoreManagerLandingController(
            @Value("${nextPage:/business/storeManagerLanding}")
            String nextPage,

            BizService bizService,
            BusinessUserStoreService businessUserStoreService,
            TokenQueueService tokenQueueService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.tokenQueueService = tokenQueueService;
    }

    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET,
            produces = "text/html;charset=UTF-8"
    )
    public String landing(
            @ModelAttribute("storeManagerForm")
            StoreManagerForm storeManagerForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        List<BusinessUserStoreEntity> businessUserStores =
                businessUserStoreService.findAllStoreQueueAssociated(queueUser.getQueueUserId());

        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String codeQR = businessUserStore.getCodeQR();
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);

            storeManagerForm
                    .setBizName(bizStore.getBizName().getBusinessName())
                    .addBizStore(bizStore)
                    .addTokenQueue(codeQR, tokenQueue);
            //TODO(hth) can add current average time by calculating serviced clients in queue.
        }

        return nextPage;
    }
}
