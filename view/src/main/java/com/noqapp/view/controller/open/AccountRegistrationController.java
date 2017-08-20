package com.noqapp.view.controller.open;

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

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.AccountService;
import com.noqapp.service.MailService;
import com.noqapp.utils.ParseJsonStringToMap;
import com.noqapp.utils.ScrubbedInput;
import com.noqapp.view.form.MerchantRegistrationForm;
import com.noqapp.view.helper.AvailabilityStatus;
import com.noqapp.view.validator.AccountValidator;

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
@RequestMapping (value = "/open/registrationMerchant")
public class AccountRegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountRegistrationController.class);

    private AccountValidator accountValidator;
    private AccountService accountService;
    private MailService mailService;
    private LoginController loginController;

    @Value ("${registrationPage:registrationMerchant}")
    private String registrationPage;

    @Value ("${registrationSuccess:redirect:/open/registrationMerchant/success.htm}")
    private String registrationSuccess;

    @Value ("${registrationSuccessPage:registrationsuccess}")
    private String registrationSuccessPage;

    @Value ("${recover:redirect:/open/forgot/recover.htm}")
    private String recover;

    @Autowired
    public AccountRegistrationController(
            AccountValidator accountValidator,
            AccountService accountService,
            MailService mailService,
            LoginController loginController
    ) {
        this.accountValidator = accountValidator;
        this.accountService = accountService;
        this.mailService = mailService;
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

    @RequestMapping (
            method = RequestMethod.POST,
            params = {"signup"})
    public String signup(
            @ModelAttribute ("merchantRegistrationForm")
            MerchantRegistrationForm merchantRegistrationForm,
            BindingResult result
    ) {
        accountValidator.validate(merchantRegistrationForm, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            return registrationPage;
        }

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(merchantRegistrationForm.getPhone());

        if (null != userProfile) {
            LOG.warn("Account already exists with phone={}", merchantRegistrationForm.getPhone());
            accountValidator.accountExists(merchantRegistrationForm, result);
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
                    null,
                    false);
        } catch (RuntimeException exce) {
            LOG.error("failure in registering user reason={}", exce.getLocalizedMessage(), exce);
            return registrationPage;
        }

        LOG.info("Registered new user qid={}", userAccount.getQueueUserId());
        mailService.sendValidationMailOnAccountCreation(
                userAccount.getUserId(),
                userAccount.getQueueUserId(),
                userAccount.getName());

        LOG.info("Account registered success");
        String redirectTo = loginController.continueLoginAfterRegistration(userAccount.getQueueUserId());
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
    @RequestMapping (
            method = RequestMethod.GET,
            value = "/success")
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
    @RequestMapping (
            method = RequestMethod.POST,
            params = {"recover"})
    public String recover(
            @ModelAttribute ("merchantRegistrationForm")
            MerchantRegistrationForm merchantRegistrationForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("merchantRegistrationForm", merchantRegistrationForm);
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
            method = RequestMethod.POST,
            value = "/availability",
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String getAvailability(@RequestBody String body) throws IOException {
        String email;
        try {
            email = StringUtils.lowerCase(ParseJsonStringToMap.jsonStringToMap(body).get("mail").getText());
        } catch (IOException e) {
            LOG.error("Failed parsing mail reason={}", e.getLocalizedMessage(), e);
            throw e;
        }

        UserProfileEntity userProfileEntity = accountService.doesUserExists(email);
        AvailabilityStatus availabilityStatus;
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