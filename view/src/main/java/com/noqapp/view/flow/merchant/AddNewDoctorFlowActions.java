package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.json.JsonUserAddress;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.GenerateUserIdService;
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

import java.util.ArrayList;

@Component
public class AddNewDoctorFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewDoctorFlowActions.class);

    private AccountService accountService;
    private BusinessUserStoreService businessUserStoreService;
    private BizService bizService;
    private ProfessionalProfileService professionalProfileService;
    private GenerateUserIdService generateUserIdService;
    private ExternalService externalService;

    @Autowired
    public AddNewDoctorFlowActions(
        AccountService accountService,
        BusinessUserStoreService businessUserStoreService,
        BizService bizService,
        ProfessionalProfileService professionalProfileService,
        GenerateUserIdService generateUserIdService,
        ExternalService externalService
    ) {
        this.accountService = accountService;
        this.businessUserStoreService = businessUserStoreService;
        this.bizService = bizService;
        this.professionalProfileService = professionalProfileService;
        this.generateUserIdService = generateUserIdService;
        this.externalService = externalService;
    }

    @SuppressWarnings("unused")
    public ProfessionalProfileEditForm populateProfessionalProfileEditForm() {
        return new ProfessionalProfileEditForm();
    }

    @SuppressWarnings("unused")
    public ProfessionalProfileForm populateProfessionalProfileForm() {
        return new ProfessionalProfileForm();
    }

    @SuppressWarnings("unused")
    public String findProfile(MerchantRegistrationForm merchantRegistration, String bizStoreId, MessageContext messageContext) {
        /* Compute email when unclaimed business. */
        whenBusinessIsNotClaimed(merchantRegistration, bizStoreId);

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
            .setAddress(new ScrubbedInput(bizStore.getAddress()))
            .setCountryShortName(new ScrubbedInput(bizStore.getCountryShortName()))
            .setPhone(new ScrubbedInput(merchantRegistrationForm.getPhoneCountryCode() + merchantRegistrationForm.getPhone()))
            .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
            .setBirthday(merchantRegistrationForm.getBirthday())
            .setFirstName(merchantRegistrationForm.getFirstName())
            .setLastName(merchantRegistrationForm.getLastName())
            .setGender(userProfile.getGender());

        Geocode geocode = Geocode.newInstance(externalService.getGeocodingResults(registerUser.getAddress()), registerUser.getAddress());
        registerUser.setFoundAddresses(geocode.getFoundAddresses());
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);
        JsonUserAddress jsonUserAddress = JsonUserAddress.populateJsonUserAddressFromDecode(decodedAddress, registerUser.getAddress());
        registerUser.setJsonUserAddress(jsonUserAddress);

        accountService.updateUserProfile(registerUser, merchantRegistrationForm.getMail().getText());

        /* Mark phone validated to change role in next step. */
        userAccount = accountService.findByUserId(merchantRegistrationForm.getMail().getText());
        userAccount.setPhoneValidated(true);
        accountService.save(userAccount);
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

                    professionalProfileEditForm
                        .setName(null)
                        .setMonthYear(null);
                }
                break;
            case "education":
                if (professionalProfileEditForm.isNotValid()) {
                    professionalProfile.getEducation().add(
                        new NameDatePair()
                            .setName(professionalProfileEditForm.getName())
                            .setMonthYear(professionalProfileEditForm.getMonthYear()));

                    professionalProfileEditForm
                        .setName(null)
                        .setMonthYear(null);
                }
                break;
            case "licenses":
                if (professionalProfileEditForm.isNotValid()) {
                    professionalProfile.getLicenses().add(
                        new NameDatePair()
                            .setName(professionalProfileEditForm.getName())
                            .setMonthYear(professionalProfileEditForm.getMonthYear()));

                    professionalProfileEditForm
                        .setName(null)
                        .setMonthYear(null);
                }
                break;
            default:
                LOG.error("Reached unsupported condition by qid={} action={}", queueUser.getQueueUserId(), professionalProfileEditForm.getAction());
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
    }

    @SuppressWarnings("unused")
    public void completeDoctorProfile(
        MerchantRegistrationForm merchantRegistration,
        ProfessionalProfileForm professionalProfile,
        ExternalContext externalContext,
        MessageContext messageContext
    ) {
        try {
            LOG.info("Update professional profile {}", merchantRegistration.getMail());
            UserProfileEntity userProfile = accountService.doesUserExists(merchantRegistration.getMail().getText());
            ProfessionalProfileEntity professionalProfileEntity = new ProfessionalProfileEntity(userProfile.getQueueUserId(), CommonUtil.generateHexFromObjectId())
                .setAboutMe(professionalProfile.getAboutMe())
                .setPracticeStart(professionalProfile.getPracticeStart())
                .setAwards(professionalProfile.getAwards())
                .setLicenses(professionalProfile.getLicenses())
                .setEducation(professionalProfile.getEducation());
            professionalProfileService.save(professionalProfileEntity);

            long change = businessUserStoreService.changeUserLevel(userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER, BusinessTypeEnum.DO);
            if (-1 == change) {
                LOG.info("Failed changing to same userLevel for qid={} to userLevel={}", userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);
            } else if (2 <= change) {
                LOG.info("Changed userLevel successfully for qid={} to userLevel={}", userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);
            } else {
                LOG.error("Failed changing userLevel for qid={} to userLevel={}", userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);
            }
        } catch (Exception e) {
            LOG.error("Failed creating doctor profile mail={} need rectification reason={}", merchantRegistration.getMail(), e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @SuppressWarnings("unused")
    public String resetAwards(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setAwards(new ArrayList<>());
        return "success";
    }

    @SuppressWarnings("unused")
    public String resetEducation(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setEducation(new ArrayList<>());
        return "success";
    }

    @SuppressWarnings("unused")
    public String resetLicenses(ProfessionalProfileForm professionalProfile) {
        professionalProfile.setLicenses(new ArrayList<>());
        return "success";
    }

    private void whenBusinessIsNotClaimed(MerchantRegistrationForm merchantRegistration, String bizStoreId) {
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);
        if (bizStore.getBizName().isNotClaimed()) {
            String expectedQid = String.valueOf(generateUserIdService.getLastGenerateUserId() + 1);
            String computedMail = RandomString.generateEmailAddressWithDomain(new ScrubbedInput(""), new ScrubbedInput(""), expectedQid);
            LOG.info("Business is not claimed hence setting email={} and phone={}", computedMail, expectedQid);
            merchantRegistration.setMail(new ScrubbedInput(computedMail));
            merchantRegistration.setPhone(expectedQid);
        }
    }
}
