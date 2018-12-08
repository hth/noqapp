package com.noqapp.view.controller.admin;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.service.AccountService;
import com.noqapp.view.form.admin.SearchUserForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * hitender
 * 2018-12-07 10:40
 */
@Controller
@RequestMapping(value = "/admin/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private String nextPage;

    private AccountService accountService;

    @Autowired
    public UserController(
        @Value("${nextPage:/admin/user}")
        String nextPage,

        AccountService accountService
    ) {
        this.nextPage = nextPage;

        this.accountService = accountService;
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("searchUserForm")
        SearchUserForm searchUserForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin user control landed qid={}", queueUser.getQueueUserId());
        return nextPage;
    }

    @PostMapping(value = "/landing", params = {"search-user"}, produces = "text/html;charset=UTF-8")
    public String searchUser(
        @ModelAttribute("searchUserForm")
        SearchUserForm searchUserForm,

        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin user search qid={}", queueUser.getQueueUserId());

        UserAccountEntity userAccount = accountService.findByQueueUserId(searchUserForm.getQid().getText());
        if (null != userAccount) {
            searchUserForm.setQid(new ScrubbedInput(userAccount.getQueueUserId()))
                .setDisplayName(new ScrubbedInput(userAccount.getDisplayName()))
                .setAccountInactiveReason(userAccount.getAccountInactiveReason())
                .setStatus(userAccount.isActive())
                .setAccountInactiveReasons(AccountInactiveReasonEnum.asMapWithNameAsKey());
        }
        redirectAttrs.addFlashAttribute("searchUserForm", searchUserForm);
        return "redirect:" + "/admin/user/landing" + ".htm";
    }

    @PostMapping(value = "/landing", params = {"cancel-search-user"})
    public String postPreferredBusinessCancel() {
        LOG.info("Loading admin landing after user search cancelled");
        return "redirect:/admin/landing.htm";
    }

    @PostMapping(value = "/action", produces = "text/html;charset=UTF-8")
    public String actionOnUser(
        @ModelAttribute("searchUserForm")
        SearchUserForm searchUserForm,

        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Admin user action on user qid={}", queueUser.getQueueUserId());

        UserAccountEntity userAccount = accountService.findByQueueUserId(searchUserForm.getQid().getText());
        if (null != userAccount) {
            if(searchUserForm.getAccountInactiveReason() != null) {
                switch (searchUserForm.getAccountInactiveReason()) {
                    case ANV:
                        userAccount.setAccountInactiveReason(AccountInactiveReasonEnum.ANV);
                        userAccount.inActive();
                        break;
                    case BOC:
                        userAccount.setAccountInactiveReason(AccountInactiveReasonEnum.BOC);
                        userAccount.inActive();
                        break;
                    case BUP:
                        userAccount.setAccountInactiveReason(AccountInactiveReasonEnum.BUP);
                        userAccount.inActive();
                        break;
                    default:
                        LOG.error("Reached unsupported condition actionType={}", searchUserForm.getAccountInactiveReason());
                        throw new UnsupportedOperationException("Reached unsupported condition for actionType " + searchUserForm.getAccountInactiveReason());
                }
            } else {
                LOG.info("Activating account for qid={}", searchUserForm.getQid());
                userAccount.setAccountInactiveReason(null);
                userAccount.active();
            }
            accountService.save(userAccount);
            accountService.updateAuthenticationKey(userAccount.getUserAuthentication().getId());
            searchUserForm.setQid(new ScrubbedInput(userAccount.getQueueUserId()))
                .setDisplayName(new ScrubbedInput(userAccount.getDisplayName()))
                .setAccountInactiveReason(userAccount.getAccountInactiveReason())
                .setStatus(userAccount.isActive())
                .setAccountInactiveReasons(AccountInactiveReasonEnum.asMapWithNameAsKey());
        }
        redirectAttrs.addFlashAttribute("searchUserForm", searchUserForm);
        return "redirect:" + "/admin/user/landing" + ".htm";
    }
}
