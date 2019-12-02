package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.PurchaseOrderService;
import com.noqapp.service.QueueService;
import com.noqapp.view.form.business.CustomerHistoryForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 11/30/19 9:08 AM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/customerHistory")
public class CustomerHistoryController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerHistoryController.class);

    private String nextPage;

    private AccountService accountService;
    private QueueService queueService;
    private PurchaseOrderService purchaseOrderService;
    private BusinessUserService businessUserService;

    @Autowired
    public CustomerHistoryController(
        @Value("${nextPage:/business/customerHistory}")
        String nextPage,

        AccountService accountService,
        QueueService queueService,
        PurchaseOrderService purchaseOrderService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.accountService = accountService;
        this.queueService = queueService;
        this.purchaseOrderService = purchaseOrderService;
        this.businessUserService = businessUserService;
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("customerHistoryForm")
        CustomerHistoryForm customerHistoryForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on data visibility page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.errorMessage", model.asMap().get("result"));
        }

        return nextPage;
    }

    @PostMapping(value = "/landing", params = {"search"}, produces = "text/html;charset=UTF-8")
    public String search(
        @ModelAttribute("customerHistoryForm")
        CustomerHistoryForm customerHistoryForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Update data visibility qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BizNameEntity bizName = businessUser.getBizName();
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(customerHistoryForm.getLookupPhone().getText());
        if (null == userProfile) {
            LOG.info("User not found with phone={}", customerHistoryForm.getLookupPhone().getText());
            redirectAttrs.addFlashAttribute("customerHistoryForm", customerHistoryForm);
            return "redirect:" + "/business/customerHistory/landing" + ".htm";
        }
        customerHistoryForm.setBusinessCustomer(true);
        customerHistoryForm.setUserProfile(userProfile);
        customerHistoryForm.addQidNameMap(userProfile.getQueueUserId(), userProfile.getName());

        if (null != userProfile.getQidOfDependents()) {
            for (String qidOfDependent : userProfile.getQidOfDependents()) {
                UserProfileEntity dependentProfile = accountService.findProfileByQueueUserId(qidOfDependent);
                customerHistoryForm.addUserProfileOfDependent(dependentProfile);
                customerHistoryForm.addQidNameMap(qidOfDependent, dependentProfile.getName());
            }

            customerHistoryForm.getUserProfileOfDependents().sort(Comparator.comparing(UserProfileEntity::getBirthday).reversed());
        }

        populateWithCurrentAndPastQueue(customerHistoryForm, bizName.getId(), userProfile);
        populateWithCurrentAndPastPurchaseOrder(customerHistoryForm, bizName.getId(), userProfile);

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.customerHistoryForm", model.asMap().get("result"));
        } else {
            redirectAttrs.addFlashAttribute("customerHistoryForm", customerHistoryForm);
        }

        return "redirect:" + "/business/customerHistory/landing.htm";
    }

    @PostMapping(value = "/landing", params = {"cancel"})
    public String searchCancel() {
        LOG.info("Cancel search");
        return "redirect:/business/landing.htm";
    }

    private void populateWithCurrentAndPastQueue(CustomerHistoryForm customerHistoryForm, String bizNameId, UserProfileEntity userProfile) {
        List<QueueEntity> queues = queueService.findAllHistoricalQueue(userProfile.getQueueUserId());
        customerHistoryForm.addCurrentAndHistoricalQueues(queues);

        if (null != userProfile.getQidOfDependents()) {
            for (String qidOfDependent : userProfile.getQidOfDependents()) {
                queues = queueService.findAllHistoricalQueue(qidOfDependent);
                customerHistoryForm.addCurrentAndHistoricalQueues(queues);
            }
        }

        customerHistoryForm.getCurrentAndHistoricalQueues().sort(Comparator.comparing(QueueEntity::getCreated).reversed());
    }

    private void populateWithCurrentAndPastPurchaseOrder(CustomerHistoryForm customerHistoryForm, String bizNameId, UserProfileEntity userProfile) {
        List<PurchaseOrderEntity> purchaseOrders = purchaseOrderService.currentByQidAndBizNameId(userProfile.getQueueUserId(), bizNameId);
        customerHistoryForm.addCurrentPurchaseOrder(purchaseOrders);

        purchaseOrders = purchaseOrderService.historicalByQidAndBizNameId(userProfile.getQueueUserId(), bizNameId);
        customerHistoryForm.addHistoricalPurchaseOrder(purchaseOrders);

        if (null != userProfile.getQidOfDependents()) {
            for (String qidOfDependent : userProfile.getQidOfDependents()) {
                purchaseOrders = purchaseOrderService.currentByQidAndBizNameId(userProfile.getQueueUserId(), bizNameId);
                customerHistoryForm.addCurrentPurchaseOrder(purchaseOrders);

                purchaseOrders = purchaseOrderService.historicalByQidAndBizNameId(qidOfDependent, bizNameId);
                customerHistoryForm.addHistoricalPurchaseOrder(purchaseOrders);
            }
        }

        customerHistoryForm.getCurrentPurchaseOrders().sort(Comparator.comparing(PurchaseOrderEntity::getCreated).reversed());
        customerHistoryForm.getHistoricalPurchaseOrders().sort(Comparator.comparing(PurchaseOrderEntity::getCreated).reversed());
    }
}
