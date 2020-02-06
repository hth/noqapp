package com.noqapp.view.flow.merchant;

import static com.noqapp.common.utils.RandomString.MAIL_NOQAPP_COM;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.HashText;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.EmailValidateService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.MailService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.flow.merchant.exception.MigrateToBusinessProfileException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * User: hitender
 * Date: 7/16/17 7:07 PM
 */
@Component
public class MigrateToBusinessProfileFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessProfileFlowActions.class);

    private AccountService accountService;
    private BusinessUserService businessUserService;
    private MailService mailService;
    private EmailValidateService emailValidateService;
    private ExecutorService executorService;

    @Autowired
    public MigrateToBusinessProfileFlowActions(
        Environment environment,
        ExternalService externalService,
        BizService bizService,
        TokenQueueService tokenQueueService,
        AccountService accountService,
        BusinessUserService businessUserService,
        MailService mailService,
        EmailValidateService emailValidateService,
        BizStoreElasticService bizStoreElasticService
    ) {
        super(environment, externalService, bizService, tokenQueueService, bizStoreElasticService, accountService, mailService);

        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.mailService = mailService;
        this.emailValidateService = emailValidateService;

        this.executorService = newCachedThreadPool();
    }

    @SuppressWarnings("all")
    public RegisterUser loadProfile() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        UserAccountEntity userAccount = accountService.findByQueueUserId(qid);
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);

        RegisterUser registerUser = new RegisterUser();
        registerUser.setQueueUserId(userAccount.getQueueUserId())
            .setGender(userProfile.getGender())
            .setBirthday(new ScrubbedInput(userProfile.getBirthday()))
            .setEmail(userProfile.getEmail().endsWith(MAIL_NOQAPP_COM) ? new ScrubbedInput("") : new ScrubbedInput(userProfile.getEmail()))
            .setFirstName(new ScrubbedInput(userProfile.getFirstName()))
            .setLastName(new ScrubbedInput(userProfile.getLastName()))
            .setAddress(new ScrubbedInput(userProfile.getAddress()))
            .setCountryShortName(new ScrubbedInput(userProfile.getCountryShortName()))
            .setPhone(new ScrubbedInput(userProfile.getPhoneRaw()))
            .setEmailValidated(userAccount.isAccountValidated())
            .setPhoneValidated(userAccount.isPhoneValidated())
            /* Since user has already agreed to this agreement when they signed up. */
            //TODO why no accept agreement when business profile is created
            .setAcceptsAgreement(true);

        LOG.info("Registered User={}", registerUser);
        return registerUser;
    }

    @SuppressWarnings ("unused")
    public BusinessUserRegistrationStatusEnum registrationStatus(RegisterUser registerUser) {
        return businessUserService.loadBusinessUser().getBusinessUserRegistrationStatus();
    }

    @SuppressWarnings ("unused")
    public String isRegistrationComplete(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        return isBusinessUserRegistrationComplete(businessUserRegistrationStatus);
    }

    @SuppressWarnings ("unused")
    public BusinessUserRegistrationStatusEnum completeRegistrationInformation(RegisterUser registerUser) {
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = queueUser.getUsername();

            accountService.updateUserProfile(registerUser, username);
            if (StringUtils.isNotBlank(registerUser.getPassword())) {
                //TODO(hth) add condition to set password on web page when profile is being modified as user could be registered via Phone and not through email
                executorService.submit(() -> updatePassword(registerUser));
            }
            sendMailAndPhoneValidation(registerUser);
            return businessUserService.markBusinessUserProfileCompleteOnProfileUpdate(registerUser.getQueueUserId(), null);
        } catch(Exception e) {
            LOG.error("Failed completing business profile qid={} reason={}", registerUser.getQueueUserId(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessProfileException("Failed to migrate to business profile");
        }
    }

    private void updatePassword(RegisterUser registerUser) {
        if (StringUtils.isBlank(registerUser.getPassword()))  {
            LOG.error("No password supplied for updating credentials");
            throw new RuntimeException("Failed to update credentials when password is empty");
        }

        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
            HashText.computeSCrypt(registerUser.getPassword()),
            HashText.computeBCrypt(RandomString.newInstance().nextString())
        );

        UserAuthenticationEntity userAuthenticationLoaded = accountService.findByQueueUserId(registerUser.getQueueUserId()).getUserAuthentication();
        userAuthentication.setId(userAuthenticationLoaded.getId());
        userAuthentication.setVersion(userAuthenticationLoaded.getVersion());
        userAuthentication.setCreated(userAuthenticationLoaded.getCreated());
        userAuthentication.setUpdated();
        accountService.updateAuthentication(userAuthentication);
    }

    private void sendMailAndPhoneValidation(RegisterUser registerUser) {
        if (!registerUser.isEmailValidated()) {
            executorService.submit(() -> sendEmail(registerUser));
        }

        if (!registerUser.isPhoneValidated()) {
            executorService.submit(() -> sendOTP(registerUser));
        }
    }

    private void sendEmail(RegisterUser registerUser) {
        UserAccountEntity userAccount = accountService.findByQueueUserId(registerUser.getQueueUserId());

        /* Since account is not validated, send account validation email. */
        EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
            userAccount.getQueueUserId(),
            userAccount.getUserId());

        mailService.accountValidationMail(
            registerUser.getEmail(),
            userAccount.getName(),
            accountValidate.getAuthenticationKey());
    }

    private void sendOTP(RegisterUser registerUser) {
    }
}
