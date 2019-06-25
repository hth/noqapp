package com.noqapp.view.controller.open;

import static com.noqapp.common.utils.RandomString.MAIL_NOQAPP_COM;

import com.noqapp.common.utils.HashText;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.MailService;
import com.noqapp.view.form.ForgotAuthenticateForm;
import com.noqapp.view.form.ForgotRecoverForm;
import com.noqapp.view.util.HttpRequestResponseParser;
import com.noqapp.view.validator.ForgotAuthenticateValidator;
import com.noqapp.view.validator.ForgotRecoverValidator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 5/3/17 12:34 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/forgot")
public class ForgotController {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotController.class);

    /** Used in RedirectAttributes */
    private static final String SUCCESS_EMAIL = "success_email";

    /** Used in JSP page /forgot/authenticateConfirm */
    private static final String SUCCESS = "success";

    /** Called when user hits on forgot password. */
    @Value("${password:/forgot/password}")
    private String passwordPage;

    /** Called when user tries to register with email already existing. Then user is directed to recover instead. */
    @Value("${recoverPage:/forgot/recover}")
    private String recoverPage;

    @Value("${recoverConfirmPage:/forgot/recoverConfirm}")
    private String recoverConfirmPage;

    @Value("${recoverConfirm:redirect:/open/forgot/recoverConfirm.htm}")
    private String recoverConfirm;

    @Value("${authenticatePage:/forgot/authenticate}")
    private String authenticatePage;

    @Value("${authenticationConfirmPage:/forgot/authenticateConfirm}")
    private String authenticateConfirm;

    private AccountService accountService;
    private ForgotRecoverValidator forgotRecoverValidator;
    private ForgotAuthenticateValidator forgotAuthenticateValidator;
    private MailService mailService;

    @Autowired
    public ForgotController(
        AccountService accountService,
        ForgotRecoverValidator forgotRecoverValidator,
        ForgotAuthenticateValidator forgotAuthenticateValidator,
        MailService mailService
    ) {
        this.accountService = accountService;
        this.forgotRecoverValidator = forgotRecoverValidator;
        this.forgotAuthenticateValidator = forgotAuthenticateValidator;
        this.mailService = mailService;
    }

    @GetMapping(value = "password")
    public String onPasswordLinkClicked(
        @ModelAttribute("forgotRecoverForm")
        ForgotRecoverForm forgotRecoverForm
    ) {
        LOG.info("Password recovery page invoked");
        return passwordPage;
    }

    @PostMapping(value = "password", params = {"forgot_password"})
    public String emailUserForPasswordRecovery(
        @ModelAttribute("forgotRecoverForm")
        ForgotRecoverForm forgotRecoverForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        forgotRecoverValidator.validate(forgotRecoverForm, result);
        if (result.hasErrors()) {
            LOG.warn("Failed validation mail={} captcha={}", forgotRecoverForm.getMail(), forgotRecoverForm.getCaptcha());
            if ("recover".equalsIgnoreCase(forgotRecoverForm.getOrigin().getText())) {
                return recoverPage;
            }

            return passwordPage;
        }

        if (forgotRecoverForm.getMail().getText().endsWith(MAIL_NOQAPP_COM)) {
            LOG.warn("Failed password recovery on domain {} for {}", MAIL_NOQAPP_COM, forgotRecoverForm.getMail().getText());

            /* Always send success when attempting to recover on mail.noqapp.com domain. */
            redirectAttrs.addFlashAttribute(SUCCESS_EMAIL, MailTypeEnum.SUCCESS);
            return recoverConfirm;
        }

        MailTypeEnum mailType = mailService.mailRecoverLink(forgotRecoverForm.getMail().getText().toLowerCase());
        if (MailTypeEnum.FAILURE == mailType) {
            LOG.error("Failed to send recovery email for user={}", forgotRecoverForm.getMail());
        }

        /* But we show success to user on failure. Not sure if we should show a failure message when mail fails. */
        switch (mailType) {
            case FAILURE:
            case ACCOUNT_NOT_VALIDATED:
            case ACCOUNT_NOT_FOUND:
            case SUCCESS:
                redirectAttrs.addFlashAttribute(
                    SUCCESS_EMAIL,
                    mailType == MailTypeEnum.ACCOUNT_NOT_VALIDATED ? mailType : MailTypeEnum.SUCCESS);
                break;
            default:
                LOG.error("Reached unreachable condition, user={}", forgotRecoverForm.getMail().getText().toLowerCase());
        }

        return recoverConfirm;
    }

    /**
     * Add this gymnastic to make sure the page does not process when refreshed again or bookmarked.
     */
    @GetMapping(value = "recoverConfirm")
    public String showConfirmationPageForProcessingPasswordRecovery(
        @ModelAttribute(SUCCESS_EMAIL)
        String success,

        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse
    ) throws IOException {

        //TODO(hth) strengthen the check here as this can be hacked to get a dummy confirmation page
        if (StringUtils.isNotBlank(success)) {
            return recoverConfirmPage;
        }
        LOG.warn(
            "404 request access={} success={} header={}",
            recoverConfirmPage,
            success,
            HttpRequestResponseParser.printHeader(httpServletRequest)
        );
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
    }


    /**
     * Used for resetting password.
     *
     * @param key
     * @param forgotAuthenticateForm
     * @return
     */
    @GetMapping(value = "authenticate")
    public String whenClickedOnEmailLink(
        @RequestParam("authenticationKey")
        ScrubbedInput key,

        ForgotAuthenticateForm forgotAuthenticateForm
    ) {
        LOG.info("Email link clicked on for password reset");
        ForgotRecoverEntity forgotRecover = accountService.findByAuthenticationKey(key.getText());
        if (forgotRecover != null) {
            forgotAuthenticateForm.setAuthenticationKey(key.getText());
            forgotAuthenticateForm.setQueueUserId(forgotRecover.getQueueUserId());
        }
        return authenticatePage;
    }

    @PostMapping(value = "authenticate", params = {"update_password"})
    public String updatePassword(
        @ModelAttribute("forgotAuthenticateForm")
        ForgotAuthenticateForm forgotAuthenticateForm,

        BindingResult result,
        ModelMap modelMap
    ) {
        forgotAuthenticateValidator.validate(forgotAuthenticateForm, result);
        if (result.hasErrors()) {
            LOG.warn("Failed validation");
            return authenticatePage;
        } else {
            ForgotRecoverEntity forgotRecover = accountService.findByAuthenticationKey(forgotAuthenticateForm.getAuthenticationKey());
            if (null == forgotRecover) {
                modelMap.addAttribute(SUCCESS, false);
            } else {
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(forgotRecover.getQueueUserId());
                UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                    HashText.computeSCrypt(forgotAuthenticateForm.getPassword()),
                    HashText.computeBCrypt(RandomString.newInstance().nextString())
                );

                UserAuthenticationEntity userAuthenticationLoaded = accountService.findByQueueUserId(userProfile.getQueueUserId()).getUserAuthentication();
                userAuthentication.setId(userAuthenticationLoaded.getId());
                userAuthentication.setVersion(userAuthenticationLoaded.getVersion());
                userAuthentication.setCreated(userAuthenticationLoaded.getCreated());
                userAuthentication.setUpdated();
                try {
                    accountService.updateAuthentication(userAuthentication);
                    accountService.invalidateAllEntries(forgotRecover.getQueueUserId());
                    modelMap.addAttribute(SUCCESS, true);
                } catch (Exception e) {
                    LOG.error("Error during updating of the old authentication key message={}", e.getLocalizedMessage(), e);
                    modelMap.addAttribute(SUCCESS, false);
                }
            }
            return authenticateConfirm;
        }
    }
}

