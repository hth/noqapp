package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.QueueService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.report.pdf.PeopleInQueue;
import com.noqapp.view.form.StoreManagerForm;
import com.noqapp.view.helper.WebUtil;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
    private BizStoreElasticService bizStoreElasticService;
    private QueueService queueService;

    @Autowired
    public StoreManagerLandingController(
        @Value("${nextPage:/business/storeManagerLanding}")
        String nextPage,

        BizService bizService,
        BusinessUserStoreService businessUserStoreService,
        TokenQueueService tokenQueueService,
        BusinessUserService businessUserService,
        BizStoreElasticService bizStoreElasticService,
        QueueService queueService
    ) {
        this.nextPage = nextPage;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.tokenQueueService = tokenQueueService;
        this.businessUserService = businessUserService;
        this.bizStoreElasticService = bizStoreElasticService;
        this.queueService = queueService;
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
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        storeManagerForm.populateBusinessTypeMaps(businessUser.getBizName().getBusinessType());
        List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(queueUser.getQueueUserId());

        for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
            String codeQR = businessUserStore.getCodeQR();
            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
            TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);

            storeManagerForm
                //TODO(hth) added biz name multiple times
                .setBizName(bizStore.getBizName().getBusinessName())
                .setBusinessType(bizStore.getBizName().getBusinessType())
                .addBizStore(bizStore)
                .addTokenQueue(codeQR, tokenQueue);
            //TODO(hth) can add current average time by calculating serviced clients in queue.
        }

        return nextPage;
    }

    /**
     * Manager should not have the permission as it removes all users managing the store. Best to let admin manage the offline and online
     * of the store. Store Active(Online) and InActive(Offline).
     */
    @PostMapping(
        value = "/onlineOrOffline",
        headers = "Accept=application/json",
        produces = "application/json")
    @ResponseBody
    public String onlineOrOffline(
        @RequestParam("storeId")
        ScrubbedInput storeId,

        @RequestParam ("action")
        ScrubbedInput action,

        HttpServletResponse response
    ) {
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
            if (null == businessUser) {
                LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }
            LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
            /* Above condition to make sure users with right roles and access gets access. */

            switch (queueUser.getUserLevel()) {
                case M_ADMIN:
                case SUPERVISOR:
                    break;
                default:
                    LOG.warn("Permission denied for qid={} having access as business user", queueUser.getQueueUserId());
                    response.sendError(SC_NOT_FOUND, "Could not find");
                    return null;
            }

            ActionTypeEnum actionType = ActionTypeEnum.valueOf(action.getText());
            BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());

            switch (actionType) {
                case ACTIVE:
                    bizStore.active();
                    bizService.saveStore(bizStore, "Store is now online");
                    BizStoreElastic bizStoreElastic = DomainConversion.getAsBizStoreElastic(bizStore, bizService.findAllStoreHours(bizStore.getId()));
                    bizStoreElasticService.save(bizStoreElastic);
                    bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
                    return String.format("{ \"storeId\" : \"%s\", \"action\" : \"%s\" }", storeId.getText(), ActionTypeEnum.INACTIVE.name());
                case INACTIVE:
                    bizStore.inActive();
                    bizService.saveStore(bizStore, "Store is now offline");
                    bizStoreElasticService.delete(bizStore.getId());
                    bizService.deleteAllManagingStore(bizStore.getId());
                    bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
                    return String.format("{ \"storeId\" : \"%s\", \"action\" : \"%s\" }", storeId.getText(), ActionTypeEnum.ACTIVE.name());
                default:
                    LOG.error("Reached unreachable condition {}", actionType);
                    throw new UnsupportedOperationException("Reached unreachable condition");
            }
        } catch (Exception e) {
            LOG.error("Failed to change store={} action={}", storeId.getText(), action.getText());
            return String.format("{ \"storeId\" : \"%s\", \"action\" : \"%s\" }", storeId.getText(), "FAILED");
        }
    }

    /** Generate list of people in queue listed in PDF. */
    @GetMapping(value = "/inQueueReport/{codeQR}")
    public void inQueueReport(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        HttpServletResponse response
    ) {
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
            if (null == businessUser) {
                LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return;
            }
            LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
            /* Above condition to make sure users with right roles and access gets access. */

            BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());
            JsonQueuePersonList jsonQueuePersonList = queueService.findAllClient(codeQR.getText());
            File file = new PeopleInQueue().setBizStore(bizStore).setJsonQueuePersonList(jsonQueuePersonList).generateReport();
            WebUtil.setContentType(file.getName(), response);
            String fileName = (bizStore.getDisplayName() + "_" + bizStore.getDisplayName()).replace(" ", "_");
            response.setHeader("Content-Disposition", "inline; filename=\"" + "NoQueue_" + fileName + ".pdf\"");
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            LOG.error("Failed generating image for codeQR reason={}", e.getLocalizedMessage());
        }
    }
}
