package com.token.view.controller.open;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.token.domain.EmailValidateEntity;
import com.token.domain.UserAccountEntity;
import com.token.domain.UserProfileEntity;
import com.token.service.AccountService;
import com.token.service.EmailValidateService;
import com.token.service.MailService;
import com.token.utils.ParseJsonStringToMap;
import com.token.utils.ScrubbedInput;
import com.token.view.form.MerchantRegistrationForm;
import com.token.view.helper.AvailabilityStatus;
import com.token.view.validator.UserRegistrationValidator;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 11/24/16 3:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/registration")
public class AccountRegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountRegistrationController.class);

    private UserRegistrationValidator userRegistrationValidator;
    private AccountService accountService;
    private MailService mailService;
    private EmailValidateService emailValidateService;
    private LoginController loginController;

    @Value ("${registrationPage:registration}")
    private String registrationPage;

    @Value ("${registrationSuccess:redirect:/open/registration/success.htm}")
    private String registrationSuccess;

    @Value ("${registrationSuccessPage:registrationsuccess}")
    private String registrationSuccessPage;

    @Value ("${recover:redirect:/open/forgot/recover.htm}")
    private String recover;

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    @Autowired
    public AccountRegistrationController(
            UserRegistrationValidator userRegistrationValidator,
            AccountService accountService,
            MailService mailService,
            EmailValidateService emailValidateService,
            LoginController loginController) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.accountService = accountService;
        this.mailService = mailService;
        this.emailValidateService = emailValidateService;
        this.loginController = loginController;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("merchantRegistrationForm")
            MerchantRegistrationForm merchantRegistrationForm
    ) {
        LOG.info("New Account Registration invoked");
        return registrationPage;
    }

    @RequestMapping (method = RequestMethod.POST, params = {"signup"})
    public String signup(
            @ModelAttribute ("merchantRegistrationForm")
            MerchantRegistrationForm merchantRegistrationForm,
            BindingResult result
    ) {
        userRegistrationValidator.validate(merchantRegistrationForm, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            return registrationPage;
        }

        UserProfileEntity userProfile = accountService.doesUserExists(merchantRegistrationForm.getMail());
        if (userProfile != null) {
            LOG.warn("account exists");
            userRegistrationValidator.accountExists(merchantRegistrationForm, result);
            merchantRegistrationForm.setAccountExists(true);
            return registrationPage;
        }
                                                               
        UserAccountEntity userAccount;
        try {
            userAccount = accountService.createNewAccount(
                    merchantRegistrationForm.getPhone(),
                    merchantRegistrationForm.getFirstName(),
                    merchantRegistrationForm.getLastName(),
                    merchantRegistrationForm.getMail(),
                    StringUtils.isNotBlank(merchantRegistrationForm.getBirthday()) ? merchantRegistrationForm.getBirthday() : "",
                    merchantRegistrationForm.getGender(),
                    merchantRegistrationForm.getCountryShortName(),
                    merchantRegistrationForm.getTimeZone(),
                    merchantRegistrationForm.getPassword(),
                    null);
        } catch (RuntimeException exce) {
            LOG.error("failure in registering user reason={}", exce.getLocalizedMessage(), exce);
            return registrationPage;
        }

        LOG.info("Registered new user Id={}", userAccount.getReceiptUserId());
        EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                userAccount.getReceiptUserId(),
                userAccount.getUserId());

        mailService.accountValidationMail(
                userAccount.getUserId(),
                userAccount.getName(),
                accountValidate.getAuthenticationKey());

        LOG.info("Account registered success");
        String redirectTo = loginController.continueLoginAfterRegistration(userAccount.getReceiptUserId());
        LOG.info("Redirecting user to {}", redirectTo);
        return "redirect:" + redirectTo;
    }

    /**
     * Starts the account recovery process.
     *
     * @param email
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping (method = RequestMethod.GET, value = "/success")
    public String success(
            @ModelAttribute ("email")
            ScrubbedInput email,

            HttpServletResponse httpServletResponse
    ) throws IOException {
        if (StringUtils.isNotBlank(email.getText())) {
            return registrationSuccessPage;
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    /**
     * Starts the account recovery process.
     *
     * @param merchantRegistrationForm
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (method = RequestMethod.POST, params = {"recover"})
    public String recover(
            @ModelAttribute ("merchantRegistrationForm")
            MerchantRegistrationForm merchantRegistrationForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("userRegistrationForm", merchantRegistrationForm);
        return recover;
    }

    /**
     * Ajax call to check if the account is available to register.
     *
     * @param body
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/availability",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String getAvailability(@RequestBody String body) throws IOException {
        String email = StringUtils.lowerCase(ParseJsonStringToMap.jsonStringToMap(body).get("mail").getText());
        AvailabilityStatus availabilityStatus;

        UserProfileEntity userProfileEntity = accountService.doesUserExists(email);
        if (null != userProfileEntity && userProfileEntity.getEmail().equals(email)) {
            LOG.info("Email={} provided during registration exists", email);
            availabilityStatus = AvailabilityStatus.notAvailable(email);
            return String.format("{ \"valid\" : %b, \"message\" : \"<b>%s</b> is already registered. %s\" }",
                    availabilityStatus.isAvailable(),
                    email,
                    StringUtils.join(availabilityStatus.getSuggestions()));
        }
        LOG.info("Email available={} for registration", email);
        availabilityStatus = AvailabilityStatus.available();
        return String.format("{ \"valid\" : %b }", availabilityStatus.isAvailable());
    }
}