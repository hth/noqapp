package com.noqapp.view.controller.emp;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.ExternalAccessEntity;
import com.noqapp.domain.site.JsonBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalAccessService;
import com.noqapp.view.form.SearchForm;
import com.noqapp.view.form.emp.AccountAccessForm;
import com.noqapp.view.validator.SearchValidator;

import org.apache.commons.lang3.StringUtils;

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

import java.util.List;

import javax.validation.Valid;

/**
 * Provide access to businesses externally. This will help fixing issue on business account.
 * //TODO Make sure to delete all access when ROLE is downgraded in even of user leaving
 * hitender
 * 2/5/18 7:33 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/emp/landing/account/access")
public class AccountAccessController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountAccessController.class);

    private String nextPage;

    private ExternalAccessService externalAccessService;
    private BizService bizService;
    private BusinessUserService businessUserService;
    private SearchValidator searchValidator;

    @Autowired
    public AccountAccessController(
        @Value("${nextPage:/emp/accountAccess}")
        String nextPage,

        ExternalAccessService externalAccessService,
        BizService bizService,
        BusinessUserService businessUSerService,
        SearchValidator searchValidator
    ) {
        this.nextPage = nextPage;

        this.externalAccessService = externalAccessService;
        this.bizService = bizService;
        this.businessUserService = businessUSerService;
        this.searchValidator = searchValidator;
    }

    /**
     * Loading landing page for business category.
     * Gymnastic for PRG.
     */
    @GetMapping
    public String landing(
        @ModelAttribute("accountAccessForm")
        AccountAccessForm accountAccessForm,

        @ModelAttribute("searchForm")
        SearchForm searchForm,

        Model model,
        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed at Account access qid={}", queueUser.getQueueUserId());

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.searchForm", model.asMap().get("result"));
            searchForm.setSearch((ScrubbedInput) model.asMap().get("search"));
        } else {
            redirectAttrs.addFlashAttribute("searchForm", searchForm);
        }

        accountAccessForm.setJsonBusinesses(externalAccessService.findAccessToAllBiz(queueUser.getQueueUserId()));
        return nextPage;
    }

    @PostMapping(value = "/search")
    public String searchByBizName(
        @Valid @ModelAttribute("accountAccessForm")
        AccountAccessForm accountAccessForm,

        @ModelAttribute("searchForm")
        SearchForm searchForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        LOG.info("Search for business by name={}", searchForm.getSearch().getText());
        searchValidator.validate(searchForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/emp/landing/account/access.htm";
        }
        List<JsonBusiness> jsonBusinesses = bizService.findDistinctBizWithMatchingName(searchForm.getSearch().getText());
        accountAccessForm.setJsonBusinessesMatchingSearch(jsonBusinesses);
        redirectAttrs.addFlashAttribute("accountAccessForm", accountAccessForm);
        return "redirect:/emp/landing/account/access.htm";
    }

    /**
     * On cancelling addition of new category.
     */
    @PostMapping(value = "/search", params = {"cancel_Search"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel business category qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/emp/landing.htm";
    }

    /**
     * Access business by creating BusinessUserEntity.
     */
    @GetMapping(value = "/{externalAccessId}")
    public String accessBusiness(
        @PathVariable("externalAccessId")
        ScrubbedInput externalAccessId
    ) {
        LOG.info("Access business externalAccessId={}", externalAccessId.getText());
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ExternalAccessEntity externalAccess = externalAccessService.findById(externalAccessId.getText());

        String qid = queueUser.getQueueUserId();
        if (StringUtils.isNotBlank(externalAccess.getApproverQID()) && qid.equalsIgnoreCase(externalAccess.getQid())) {
            BizNameEntity bizName = bizService.getByBizNameId(externalAccess.getBizId());

            /*
             * There is only one instance at a time for Supervisor or Technician accessing business externally.
             * This is also enforced on collection level too.
             */
            BusinessUserEntity businessUser = businessUserService.findByQid(qid);
            if (null == businessUser) {
                businessUser = BusinessUserEntity.newInstance(qid, UserLevelEnum.M_ADMIN);
            }
            businessUser.setValidateByQid(externalAccess.getApproverQID());
            businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
            businessUser.setExternalAccessId(externalAccess);
            businessUser.setBizName(bizName);
            businessUserService.save(businessUser);

            LOG.info("ExternalAccess to businessName={} qid={}", bizName.getBusinessName(), qid);
            return "redirect:/business/landing.htm";
        }

        return "redirect:/emp/landing/account/access.htm";
    }

    /**
     * Remove access to a specific business.
     */
    @PostMapping(value = "/actionExternalAccess")
    public String actionOnBusinessAccess(
        @ModelAttribute("accountAccessForm")
        AccountAccessForm accountAccessForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            switch (accountAccessForm.getAction().getText()) {
                case "REMOVE":
                    externalAccessService.revokePermission(accountAccessForm.getId().getText());
                    break;
                case "SEND":
                    externalAccessService.requestPermission(accountAccessForm.getId().getText(), queueUser.getQueueUserId());
                    break;
                default:
                    LOG.warn("Reached un-reachable condition {}", accountAccessForm.getAction());
                    throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
            }

            return "redirect:/emp/landing/account/access.htm";
        } catch (Exception e) {
            LOG.error("Failed updated status for bizId={} status={} qid={} reason={}",
                accountAccessForm.getId().getText(),
                accountAccessForm.getAction().getText(),
                queueUser.getQueueUserId(),
                e.getLocalizedMessage(),
                e);

            return "redirect:/emp/landing/account/access.htm";
        }
    }
}
