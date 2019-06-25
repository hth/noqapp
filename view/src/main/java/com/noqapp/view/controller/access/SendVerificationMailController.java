package com.noqapp.view.controller.access;

import static com.noqapp.common.utils.RandomString.MAIL_NOQAPP_COM;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.MailService;
import com.noqapp.view.form.business.ProfileForm;

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
 * 12/22/17 7:37 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/sendVerificationMail")
public class SendVerificationMailController {
    private static final Logger LOG = LoggerFactory.getLogger(SendVerificationMailController.class);

    private String nextPage;

    private MailService mailService;
    private AccountService accountService;
    private ApiHealthService apiHealthService;

    @Autowired
    public SendVerificationMailController(
        @Value("${nextPage:/access/sendVerificationMail}")
        String nextPage,

        MailService mailService,
        AccountService accountService,
        ApiHealthService apiHealthService
    ) {
        this.nextPage = nextPage;
        this.mailService = mailService;
        this.accountService = accountService;
        this.apiHealthService = apiHealthService;
    }

    /**
     * Gymnastic for PRG.
     * @param profile
     * @return
     */
    @GetMapping
    public String getSendVerificationMailController(
        @ModelAttribute("profile")
        ProfileForm profile
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on sendMailVerification page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());
        if (userAccount.getUserId().endsWith(MAIL_NOQAPP_COM)) {
            /* Re-direct to enter email address. */
            LOG.warn("No email found. Attempted bad behaviour by {} {}", queueUser.getQueueUserId(), userAccount.getUserId());
            //TODO(hth) create email update page.
        }

        if (userAccount.isAccountValidated()) {
            profile.setAccountValidated(true);
        }

        if (!profile.isSubmitState() && !profile.isAccountValidated()) {
            profile.setMail(queueUser.getUsername());
        }

        return nextPage;
    }

    @PostMapping
    public String sendVerificationMailController(
        @ModelAttribute("profile")
        ProfileForm profile,

        RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on sendMailVerification page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());
        mailService.sendValidationMailOnAccountCreation(
            userAccount.getUserId(),
            userAccount.getQueueUserId(),
            userAccount.getName()
        );

        profile.setSubmitState(true);
        redirectAttrs.addFlashAttribute("profile", profile);
        return "redirect:" + nextPage + ".htm";
    }
}
