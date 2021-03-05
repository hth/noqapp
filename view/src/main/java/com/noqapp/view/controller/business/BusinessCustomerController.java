package com.noqapp.view.controller.business;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.service.BusinessCustomerPriorityService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.BusinessCustomerPriorityForm;
import com.noqapp.view.validator.BusinessCustomerPriorityValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 5/15/20 6:33 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/customer")
public class BusinessCustomerController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessCustomerController.class);

    private String nextPage;

    private BusinessUserService businessUserService;
    private BusinessCustomerPriorityService businessCustomerPriorityService;
    private BusinessCustomerPriorityValidator businessCustomerPriorityValidator;

    @Autowired
    public BusinessCustomerController(
        @Value("${nextPage:/business/customer/landing}")
        String nextPage,

        BusinessUserService businessUserService,
        BusinessCustomerPriorityService businessCustomerPriorityService,
        BusinessCustomerPriorityValidator businessCustomerPriorityValidator
    ) {
        this.nextPage = nextPage;

        this.businessUserService = businessUserService;
        this.businessCustomerPriorityService = businessCustomerPriorityService;
        this.businessCustomerPriorityValidator = businessCustomerPriorityValidator;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("businessCustomerPriorityForm")
        BusinessCustomerPriorityForm businessCustomerPriorityForm,

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
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        businessCustomerPriorityForm.setPriorityAccess(businessUser.getBizName().getPriorityAccess());
        businessCustomerPriorityForm.setBusinessCustomerPriorities(businessCustomerPriorityService.findAll(businessUser.getBizName().getId()));

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.businessCustomerPriorityForm", model.asMap().get("result"));
        } else {
            redirectAttrs.addFlashAttribute("businessCustomerPriorityForm", businessCustomerPriorityForm);
        }

        return this.nextPage;
    }

    @PostMapping(value = "/priority", produces = "text/html;charset=UTF-8", params = "edit")
    public String update(
        @ModelAttribute("businessCustomerPriorityForm")
        BusinessCustomerPriorityForm businessCustomerPriorityForm,

        BindingResult result,
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
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        businessCustomerPriorityValidator.validatePriorityAccess(businessCustomerPriorityForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/business/customer/landing";
        }
        businessCustomerPriorityService.changePriorityAccess(businessUser.getBizName().getId(), businessCustomerPriorityForm.getPriorityAccess());
        return "redirect:/business/customer/landing";
    }

    @PostMapping(value = "/priority",  produces = "text/html;charset=UTF-8", params = "cancel-edit")
    public String update() {
        LOG.info("Cancel priority ON/OFF");
        return "redirect:/business/landing";
    }

    @PostMapping(value = "/priority/{action}", produces = "text/html;charset=UTF-8")
    public String priorityAction(
        @PathVariable("action")
        ScrubbedInput action,

        @ModelAttribute("businessCustomerPriorityForm")
        BusinessCustomerPriorityForm businessCustomerPriorityForm,

        BindingResult result,
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
        LOG.info("Landed on business page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        businessCustomerPriorityForm.setBizNameId(businessUser.getBizName().getId());
        businessCustomerPriorityValidator.validate(businessCustomerPriorityForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/business/customer/landing";
        }

        ActionTypeEnum actionType = ActionTypeEnum.valueOf(action.getText().toUpperCase());
        switch (actionType) {
            case ADD:
                businessCustomerPriorityService.addCustomerPriority(
                    businessUser.getBizName().getId(),
                    businessCustomerPriorityForm.getPriorityName().getText(),
                    businessCustomerPriorityForm.getPriorityLevel());
                break;
            case REMOVE:
                break;
            case EDIT:
                break;
            default:
                LOG.error("Reached un-supported condition");
        }

        return "redirect:/business/customer/landing";
    }

    @PostMapping(value = "/priority/{action}",  produces = "text/html;charset=UTF-8", params = "cancel-add")
    public String priorityAction(
        @PathVariable("action")
        ScrubbedInput action
    ) {
        LOG.info("Cancel priority {}", action);
        return "redirect:/business/landing";
    }
}
