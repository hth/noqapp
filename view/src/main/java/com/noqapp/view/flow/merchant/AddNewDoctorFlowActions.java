package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.MailService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.view.form.MerchantRegistrationForm;
import com.noqapp.view.form.ProfessionalProfileEditForm;
import com.noqapp.view.form.ProfessionalProfileForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import java.util.HashSet;

@Component
public class AddNewDoctorFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewDoctorFlowActions.class);

    private AccountService accountService;
    private MailService mailService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private BizService bizService;
    private ProfessionalProfileService professionalProfileService;

    @Autowired
    public AddNewDoctorFlowActions(
        AccountService accountService,
        MailService mailService,
        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        BizService bizService,
        ProfessionalProfileService professionalProfileService
    ) {
        this.accountService = accountService;
        this.mailService = mailService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.bizService = bizService;
        this.professionalProfileService = professionalProfileService;
    }

    public ProfessionalProfileEditForm populateProfessionalProfileEditForm() {
        return new ProfessionalProfileEditForm();
    }

    public ProfessionalProfileForm populateProfessionalProfileForm() {
        return new ProfessionalProfileForm();
    }

    @SuppressWarnings("unused")
    public String findProfile(MerchantRegistrationForm merchantRegistration, MessageContext messageContext) {
        UserAccountEntity userAccount = accountService.findByUserId(merchantRegistration.getMail().getText());
        if (userAccount == null) {
            UserProfileEntity userProfile = accountService.checkUserExistsByPhone(merchantRegistration.getPhoneCountryCode() + merchantRegistration.getPhone());
            if (userProfile != null) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("phone")
                        .defaultText("User exists with similar phone number")
                        .build());

                userAccount = accountService.findByQueueUserId(userProfile.getQueueUserId());
            }
        } else {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("mail")
                    .defaultText("User exists with similar email address")
                    .build());
        }
        if (null != userAccount) {
            return "failure";
        } else {
            return "success";
        }
    }

    @SuppressWarnings("unused")
    public void beforeMigrationToDoctorProfile(
        MerchantRegistrationForm merchantRegistrationForm,
        String bizStoreId,
        ExternalContext externalContext,
        MessageContext messageContext
    ) {
        UserAccountEntity userAccount = accountService.findByUserId(merchantRegistrationForm.getMail().getText());
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(userAccount.getQueueUserId());
        accountService.updatePhoneNumber(
            userAccount.getQueueUserId(),
            merchantRegistrationForm.getPhoneCountryCode() + merchantRegistrationForm.getPhone(),
            userProfile.getCountryShortName(),
            userProfile.getTimeZone());
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);

        /* For updating address. */
        RegisterUser registerUser = new RegisterUser()
            .setEmail(merchantRegistrationForm.getMail())
            .setGender(userProfile.getGender())
            .setAddress(new ScrubbedInput(bizStore.getAddress()))
            .setCountryShortName(new ScrubbedInput(bizStore.getCountryShortName()))
            .setPhone(new ScrubbedInput(merchantRegistrationForm.getPhoneCountryCode() + merchantRegistrationForm.getPhone()))
            .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
            .setBirthday(merchantRegistrationForm.getBirthday())
            .setAddressOrigin(bizStore.getAddressOrigin())
            .setFirstName(merchantRegistrationForm.getFirstName())
            .setLastName(merchantRegistrationForm.getLastName());
        accountService.updateUserProfile(registerUser, merchantRegistrationForm.getMail().getText());
    }

    @SuppressWarnings("unused")
    public void updateProfessionalProfile(
        ProfessionalProfileForm professionalProfile,
        ProfessionalProfileEditForm professionalProfileEditForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        switch (professionalProfileEditForm.getAction()) {
            case "awards":
                if (professionalProfileEditForm.isNotValid()) {
                    professionalProfile.getAwards().add(
                        new NameDatePair()
                            .setName(professionalProfileEditForm.getName())
                            .setMonthYear(professionalProfileEditForm.getMonthYear()));
                }
                break;
            case "education":
                if (professionalProfileEditForm.isNotValid()) {
                    professionalProfile.getEducation().add(
                        new NameDatePair()
                            .setName(professionalProfileEditForm.getName())
                            .setMonthYear(professionalProfileEditForm.getMonthYear()));
                }
                break;
            case "licenses":
                if (professionalProfileEditForm.isNotValid()) {
                    professionalProfile.getLicenses().add(
                        new NameDatePair()
                            .setName(professionalProfileEditForm.getName())
                            .setMonthYear(professionalProfileEditForm.getMonthYear()));
                }
                break;
            default:
                LOG.error("Reached unsupported condition by qid={} action={}", queueUser.getQueueUserId(), professionalProfileEditForm.getAction());
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
    }

    public void completeDoctorProfile(
        MerchantRegistrationForm merchantRegistration,
        ProfessionalProfileForm professionalProfile,
        ExternalContext externalContext,
        MessageContext messageContext
    ) {
        LOG.info("Update professional profile {}", merchantRegistration.getMail());
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(merchantRegistration.getPhoneCountryCode() + merchantRegistration.getPhone());
        professionalProfile.setQid(userProfile.getQueueUserId());

        professionalProfileService.createProfessionalProfile(userProfile.getQueueUserId());
        ProfessionalProfileEntity professionalProfileEntity = professionalProfileService.findByQid(userProfile.getQueueUserId());
        professionalProfileEntity
            .setAboutMe(professionalProfile.getAboutMe())
            .setPracticeStart(professionalProfile.getPracticeStart())
            .setAwards(professionalProfile.getAwards())
            .setLicenses(professionalProfile.getLicenses())
            .setEducation(professionalProfile.getEducation());
        professionalProfileService.save(professionalProfileEntity);

        userProfile.setLevel(UserLevelEnum.S_MANAGER);
        accountService.save(userProfile);
        accountService.changeAccountRolesToMatchUserLevel(userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);
    }

    public String resetAwards(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setAwards(new HashSet<>());
        return "success";
    }

    public String resetEducation(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setEducation(new HashSet<>());
        return "success";
    }

    public String resetLicenses(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setLicenses(new HashSet<>());
        return "success";
    }
}