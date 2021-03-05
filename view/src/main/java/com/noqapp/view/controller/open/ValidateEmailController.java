package com.noqapp.view.controller.open;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.EmailValidateService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 12/9/16 5:00 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/validate")
public class ValidateEmailController {
    private static final Logger LOG = LoggerFactory.getLogger(ValidateEmailController.class);

    private EmailValidateService emailValidateService;
    private AccountService accountService;
    private ApiHealthService apiHealthService;

    @Value("${emailValidate:redirect:/open/validate/result}")
    private String validateResult;

    @Value("${emailValidatePage:validate/success}")
    private String validateSuccessPage;

    @Value("${emailValidatePage:validate/failure}")
    private String validateFailurePage;

    @Autowired
    public ValidateEmailController(
        EmailValidateService emailValidateService,
        AccountService accountService,
        ApiHealthService apiHealthService
    ) {
        this.emailValidateService = emailValidateService;
        this.accountService = accountService;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping
    public String validateEmail(
        @RequestParam("authenticationKey")
        ScrubbedInput key,

        RedirectAttributes redirectAttrs,
        HttpServletResponse httpServletResponse
    ) throws IOException {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        try {
            EmailValidateEntity emailValidate = emailValidateService.findByAuthenticationKey(key.getText());
            if (null == emailValidate) {
                LOG.info("Email address authentication failed because its deleted/invalid auth={}", key);
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            } else if (!emailValidate.isActive()) {
                LOG.info("Email address authentication previously validated for auth={}", key);
                /* Expired link after validation. */
                httpServletResponse.sendError(HttpServletResponse.SC_GONE);
                return null;
            }

            UserAccountEntity userAccount = accountService.findByQueueUserId(emailValidate.getQueueUserId());
            if (userAccount.isAccountValidated()) {
                redirectAttrs.addFlashAttribute("success", "false");
                LOG.info("email address authentication failed for qid={}", userAccount.getQueueUserId());
            } else {
                accountService.validateAccount(emailValidate, emailValidate.getQueueUserId());
                redirectAttrs.addFlashAttribute("success", "true");

                LOG.info("email address authentication success for qid={}", userAccount.getQueueUserId());
            }
            return validateResult;
        } catch (Exception e) {
            LOG.error("Failed validating emailValidateKey={} reason={}", key.getText(), e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
            throw e;
        } finally {
            apiHealthService.insert(
                "/",
                "validateEmail",
                ValidateEmailController.class.getName(),
                Duration.between(start, Instant.now()),
                methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }

    @GetMapping(value = "/result")
    public String success(
        @ModelAttribute("success")
        ScrubbedInput success,

        Model model,
        HttpServletResponse httpServletResponse
    ) throws IOException {
        String nextPage = null;
        if (StringUtils.isNotBlank(success.getText())) {
            nextPage = Boolean.parseBoolean(success.getText()) ? validateSuccessPage : validateFailurePage;
            model.addAttribute(
                "registrationMessage",
                "Please log in with your email address and password entered during registration.");

        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return nextPage;
    }
}
