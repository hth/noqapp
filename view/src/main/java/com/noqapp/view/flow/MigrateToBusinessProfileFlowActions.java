package com.noqapp.view.flow;

import static java.util.concurrent.Executors.newCachedThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.TokenUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.EmailValidateService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.MailService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.utils.HashText;
import com.noqapp.utils.RandomString;
import com.noqapp.view.flow.exception.MigrateToBusinessProfileException;

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
    private ExecutorService service;

    @Autowired
    public MigrateToBusinessProfileFlowActions(
            ExternalService externalService,
            BizService bizService,
            TokenQueueService tokenQueueService,
            AccountService accountService,
            BusinessUserService businessUserService,
            MailService mailService,
            EmailValidateService emailValidateService
    ) {
        super(externalService, bizService, tokenQueueService);

        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.mailService = mailService;
        this.emailValidateService = emailValidateService;

        this.service = newCachedThreadPool();
    }

    public RegisterUser loadProfile() {
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = tokenUser.getRid();

        UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);
        UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(rid);

        RegisterUser registerUser = new RegisterUser();
        registerUser.setRid(userAccount.getReceiptUserId())
                .setGender(userProfile.getGender())
                .setBirthday(userProfile.getBirthday())
                .setEmail(userProfile.getEmail().endsWith("mail.noqapp.com") ? "" : userProfile.getEmail())
                .setFirstName(userProfile.getFirstName())
                .setLastName(userProfile.getLastName())
                .setAddress(userProfile.getAddress())
                .setCountryShortName(userProfile.getCountryShortName())
                .setPhone(userProfile.getPhoneRaw())
                .setEmailValidated(userAccount.isAccountValidated())
                .setPhoneValidated(userAccount.isPhoneValidated())
                /* Since user has already agreed to this agreement when they signed up. */
                //TODO why no accept agreement when business profile is created
                .setAcceptsAgreement(true);

        return registerUser;
    }

    public BusinessUserRegistrationStatusEnum registrationStatus(RegisterUser registerUser) {
        return businessUserService.findBusinessUser(registerUser.getRid()).getBusinessUserRegistrationStatus();
    }

    public boolean isRegistrationComplete(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        return isBusinessUserRegistrationComplete(businessUserRegistrationStatus);
    }

    public BusinessUserRegistrationStatusEnum completeRegistrationInformation(RegisterUser registerUser) {
        try {
            TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = tokenUser.getUsername();

            accountService.updateUserProfile(registerUser, username);
            service.submit(() -> updatePassword(registerUser));
            sendMailAndPhoneValidation(registerUser);

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(registerUser.getRid());
            businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);
            businessUserService.save(businessUser);

            return businessUser.getBusinessUserRegistrationStatus();
        } catch(Exception e) {
            LOG.error("Failed completing business profile rid={} reason={}", registerUser.getRid(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessProfileException("Failed to migrate to business profile");
        }
    }

    private void updatePassword(RegisterUser registerUser) {
        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeBCrypt(registerUser.getPassword()),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );

        UserAuthenticationEntity userAuthenticationLoaded = accountService.findByReceiptUserId(registerUser.getRid()).getUserAuthentication();
        userAuthentication.setId(userAuthenticationLoaded.getId());
        userAuthentication.setVersion(userAuthenticationLoaded.getVersion());
        userAuthentication.setCreated(userAuthenticationLoaded.getCreated());
        userAuthentication.setUpdated();
        accountService.updateAuthentication(userAuthentication);
    }

    private void sendMailAndPhoneValidation(RegisterUser registerUser) {
        if (!registerUser.isEmailValidated()) {
            service.submit(() -> sendEmail(registerUser));
        }

        if (!registerUser.isPhoneValidated()) {
            service.submit(() -> sendOTP(registerUser));
        }
    }

    private void sendEmail(RegisterUser registerUser) {
        UserAccountEntity userAccount = accountService.findByReceiptUserId(registerUser.getRid());

        /* Since account is not validated, send account validation email. */
        EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                userAccount.getReceiptUserId(),
                userAccount.getUserId());

        mailService.accountValidationMail(
                registerUser.getEmail(),
                userAccount.getName(),
                accountValidate.getAuthenticationKey());
    }

    private void sendOTP(RegisterUser registerUser) {
    }
}
