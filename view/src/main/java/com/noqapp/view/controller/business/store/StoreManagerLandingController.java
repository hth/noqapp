package com.noqapp.view.controller.business.store;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.service.BusinessUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.form.StoreManagerForm;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

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
    private BusinessUserService businessUserService;

    @Autowired
    public StoreManagerLandingController(
            @Value("${nextPage:/business/storeManagerLanding}")
            String nextPage,

            BizService bizService,
            BusinessUserStoreService businessUserStoreService,
            TokenQueueService tokenQueueService,
            BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.tokenQueueService = tokenQueueService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
            @ModelAttribute("storeManagerForm")
            StoreManagerForm storeManagerForm,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        storeManagerForm.populateBusinessTypeMaps(businessUser.getBizName().getBusinessType());
        List<BusinessUserStoreEntity> businessUserStores =
                businessUserStoreService.findAllStoreQueueAssociated(queueUser.getQueueUserId());

        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String codeQR = businessUserStore.getCodeQR();
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);

            storeManagerForm
                    //TODO(hth) added biz name multiple times
                    .setBizName(bizStore.getBizName().getBusinessName())
                    .addBizStore(bizStore)
                    .addTokenQueue(codeQR, tokenQueue);
            //TODO(hth) can add current average time by calculating serviced clients in queue.
        }

        return nextPage;
    }
}
