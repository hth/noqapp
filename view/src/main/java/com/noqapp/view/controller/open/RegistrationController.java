package com.noqapp.view.controller.open;

import com.noqapp.common.utils.ParseJsonStringToMap;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.MailService;
import com.noqapp.service.SmsService;
import com.noqapp.service.exceptions.DuplicateAccountException;
import com.noqapp.view.form.MerchantRegistrationForm;
import com.noqapp.view.helper.AvailabilityStatus;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * User: hitender
 * Date: 11/24/16 3:34 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/registrationMerchant")
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    private String smsTxtOnRegistration;

    private AccountService accountService;
    private MailService mailService;
    private LoginController loginController;
    private SmsService smsService;

    @Value("${registrationPage:/open/register}")
    private String registrationPage;

    @Autowired
    public RegistrationController(
        @Value("${sms.txt.on.registration}")
        String smsTxtOnRegistration,

        AccountService accountService,
        MailService mailService,
        LoginController loginController,
        SmsService smsService
    ) {
        this.smsTxtOnRegistration = smsTxtOnRegistration;

        this.accountService = accountService;
        this.mailService = mailService;
        this.loginController = loginController;
        this.smsService = smsService;
    }

    @PostMapping
    public String signUp(
        @ModelAttribute("merchantRegistration")
        MerchantRegistrationForm merchantRegistration
    ) {
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(merchantRegistration.getPhone());

        if (null != userProfile && !merchantRegistration.isNotAdult()) {
            LOG.warn("Account already exists with phone={}", merchantRegistration.getPhone());
            merchantRegistration.setAccountExists(true);
            return registrationPage;
        }

        UserAccountEntity userAccount;
        try {
            userAccount = accountService.createNewAccount(
                merchantRegistration.getPhone(),
                merchantRegistration.getFirstName().getText(),
                merchantRegistration.getLastName().getText(),
                StringUtils.lowerCase(merchantRegistration.getMail().getText()),
                StringUtils.isNotBlank(merchantRegistration.getBirthday().getText()) ? merchantRegistration.getBirthday().getText() : "",
                GenderEnum.valueOf(merchantRegistration.getGender().getText()),
                merchantRegistration.findCountryShortFromPhone(),
                /* Timezone from website is difficult to compute, hence passing null. */
                null,
                merchantRegistration.getPassword().getText(),
                null,
                true,
                merchantRegistration.isNotAdult());

            if (null == userAccount) {
                LOG.error("Failed creating account for phone={}", merchantRegistration.getPhone());
                return registrationPage;
            }

            smsService.sendPromotionalSMS(merchantRegistration.getPhone(), smsTxtOnRegistration);
        } catch (DuplicateAccountException e) {
            LOG.error("Duplicate Account found reason={}", e.getLocalizedMessage(), e);
            return registrationPage;
        } catch (RuntimeException e) {
            LOG.error("Failure in registering user reason={}", e.getLocalizedMessage(), e);
            return registrationPage;
        }

        LOG.info("Registered new user qid={}", userAccount.getQueueUserId());
        mailService.sendValidationMailOnAccountCreation(
            userAccount.getUserId(),
            userAccount.getQueueUserId(),
            userAccount.getName());

        LOG.info("Account registered success");
        String redirect = loginController.continueLoginAfterRegistration(userAccount.getQueueUserId());
        LOG.info("Redirecting user to link={}", redirect);
        return String.format("{ \"next\" : \"%s\" }", redirect);
    }

    /**
     * Ajax call to check if the account is available to register.
     *
     * @param body
     * @return
     * @throws IOException
     */
    @PostMapping(
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