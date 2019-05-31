package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.MigrateToBusinessRegistration;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AmenityEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.FacilityEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.FetcherService;
import com.noqapp.service.MailService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.view.flow.merchant.exception.MigrateToBusinessRegistrationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/9/16 1:20 PM
 */
@Component
public class MigrateToBusinessRegistrationFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessRegistrationFlowActions.class);

    private FetcherService fetcherService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private AccountService accountService;
    private BusinessUserService businessUserService;

    @SuppressWarnings ("all")
    @Autowired
    public MigrateToBusinessRegistrationFlowActions(
        Environment environment,
        FetcherService fetcherService,
        UserProfilePreferenceService userProfilePreferenceService,
        AccountService accountService,
        BusinessUserService businessUserService,
        BizService bizService,
        ExternalService externalService,
        TokenQueueService tokenQueueService,
        BizStoreElasticService bizStoreElasticService,
        MailService mailService
    ) {
        super(environment, externalService, bizService, tokenQueueService, bizStoreElasticService, accountService, mailService);
        this.fetcherService = fetcherService;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.accountService = accountService;
        this.businessUserService = businessUserService;
    }

    public Set<String> findAllDistinctBizName(String bizName) {
        return fetcherService.findAllDistinctBizName(bizName);
    }

    @SuppressWarnings ("unused")
    public Register createBusinessRegistration() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        List<StoreHourEntity> storeHours = null;
        /* Loads any existing business profile. */
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            businessUser = BusinessUserEntity.newInstance(qid, UserLevelEnum.M_ADMIN);
            businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
        } else {
            LOG.info("Un-approved business being edited qid={}", businessUser.getQueueUserId());
        }

        Register register = MigrateToBusinessRegistration.newInstance(businessUser, null);
        UserAccountEntity userAccount = accountService.findByQueueUserId(qid);
        UserProfileEntity userProfile = userProfilePreferenceService.findByQueueUserId(qid);
        register.getRegisterUser().setEmail(new ScrubbedInput(userProfile.getEmail()))
            .setGender(userProfile.getGender())
            .setBirthday(new ScrubbedInput(userProfile.getBirthday()))
            .setFirstName(new ScrubbedInput(userProfile.getFirstName()))
            .setLastName(new ScrubbedInput(userProfile.getLastName()))
            .setAddress(new ScrubbedInput(userProfile.getAddress()))
            .setCountryShortName(new ScrubbedInput(userProfile.getCountryShortName()))
            .setPhone(new ScrubbedInput(userProfile.getPhoneRaw()))
            .setEmailValidated(userAccount.isAccountValidated())
            .setPhoneValidated(userAccount.isPhoneValidated());

        LOG.info("Register={}", register);
        return register;
    }

    @SuppressWarnings ("unused")
    public String isRegistrationComplete(Register register) {
        return isBusinessUserRegistrationComplete(register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus());
    }

    @SuppressWarnings ("unused")
    public Register populateBusiness(String bizId) {
        if (StringUtils.isBlank(bizId)) {
            return createBusinessRegistration();
        } else {
            return editBusiness(bizId);
        }
    }

    @SuppressWarnings ("unused")
    public Register editBusiness(String bizNameId) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser || !businessUser.getBizName().getId().equalsIgnoreCase(bizNameId)) {
            /* Should never reach here. */
            LOG.error("Reached unexpected condition for user={}", queueUser.getQueueUserId());
            throw new UnsupportedOperationException("Failed loading Business");
        }

        return MigrateToBusinessRegistration.newInstance(businessUser, null);
    }

    @SuppressWarnings ("unused")
    public void additionalAttributes(RegisterBusiness registerBusiness, String modelType) {
        switch (modelType) {
            case "bizName":
                addAvailableAmenities(registerBusiness, registerBusiness.getBusinessType(), modelType);
                addAvailableFacilities(registerBusiness,  registerBusiness.getBusinessType(), modelType);
                break;
            case "bizStore":
                addAvailableAmenities(registerBusiness, registerBusiness.getStoreBusinessType(), modelType);
                addAvailableFacilities(registerBusiness, registerBusiness.getStoreBusinessType(), modelType);
                break;
            default:
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
    }

    private void addAvailableFacilities(RegisterBusiness registerBusiness, BusinessTypeEnum businessType, String modelType) {
        switch (businessType) {
            case HS:
            case DO:
                switch (modelType) {
                    case "bizName":
                        registerBusiness.addFacilitiesAvailable(FacilityEnum.DOCTOR_HOSPITAL);
                        break;
                    case "bizStore":
                        break;
                    default:
                        throw new UnsupportedOperationException("Reached Unsupported Condition");
                }
                break;
            case GS:
                registerBusiness.setFacilitiesAvailable(FacilityEnum.GROCERY);
                break;
            case RS:
                registerBusiness.setFacilitiesAvailable(FacilityEnum.RESTAURANT);
                break;
            default:
                registerBusiness
                    .addFacilitiesAvailable(FacilityEnum.GROCERY)
                    .addFacilitiesAvailable(FacilityEnum.RESTAURANT);
                break;
        }
    }

    private void addAvailableAmenities(RegisterBusiness registerBusiness, BusinessTypeEnum businessType, String modelType) {
        switch (businessType) {
            case HS:
            case DO:
                switch (modelType) {
                    case "bizName":
                        registerBusiness.addAmenitiesAvailable(AmenityEnum.ALL);
                        break;
                    case "bizStore":
                        break;
                    default:
                        throw new UnsupportedOperationException("Reached Unsupported Condition");
                }
                break;
            case GS:
                registerBusiness.setAmenitiesAvailable(AmenityEnum.ALL);
                break;
            case RS:
                registerBusiness.setAmenitiesAvailable(AmenityEnum.ALL);
                break;
            default:
                registerBusiness.addAmenitiesAvailable(AmenityEnum.ALL);
                break;
        }
    }

    /**
     * Register new business.
     * 
     * @param register
     * @return
     * @throws MigrateToBusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public Register completeRegistrationInformation(Register register) throws MigrateToBusinessRegistrationException {
        try {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = queueUser.getUsername();

            accountService.updateUserProfile(register.getRegisterUser(), username);
            try {
                BizNameEntity bizName = registerBusinessDetails(register);
                /*
                 * Even though it is a new business, using
                 * {@link com.noqapp.service.BusinessUserService#findBusinessUser(String, String)} way its safe to
                 * create a new Merchant Admin or update same account when it was rejected/flagged previously. This
                 * enforces we are updating the correct record.
                 *
                 * If we use {@link com.noqapp.service.BusinessUserService#loadBusinessUser()}, there is a likelihood
                 * of finding existing user with a different role. In future,
                 * {@link com.noqapp.service.BusinessUserService#loadBusinessUser()} will load multiple business user
                 * associated with QID.
                 */
                BusinessUserEntity businessUser = businessUserService.findBusinessUser(
                        register.getRegisterUser().getQueueUserId(),
                        register.getRegisterBusiness().getBizId());

                if (null == businessUser) {
                    businessUser = BusinessUserEntity.newInstance(
                            register.getRegisterUser().getQueueUserId(),
                            UserLevelEnum.M_ADMIN
                    ).setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);
                }
                businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

                businessUserService.save(businessUser);
                register.getRegisterBusiness().setBusinessUser(businessUser);
                return register;
            } catch (Exception e) {
                LOG.error("Error adding business qid={} reason={}",
                        register.getRegisterUser().getQueueUserId(), e.getLocalizedMessage(), e);
                throw new MigrateToBusinessRegistrationException("Error adding business", e);
            }
        } catch (Exception e) {
            LOG.error("Error updating business user profile qid={} reason={}",
                    register.getRegisterUser().getQueueUserId(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error updating profile", e);
        }
    }

    @SuppressWarnings ("unused")
    public Register updateRegistrationInformation(Register register) throws MigrateToBusinessRegistrationException {
        registerBusinessDetails(register);
        return register;
    }

    /**
     * Register new store.
     *
     * @param registerBusiness
     * @return
     * @throws MigrateToBusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public RegisterBusiness completeRegistrationInformation(RegisterBusiness registerBusiness) throws MigrateToBusinessRegistrationException {
        try {
            return registerBusinessDetails(registerBusiness);
        } catch (Exception e) {
            LOG.error("Error adding business qid={} reason={}", registerBusiness.getBusinessUser().getQueueUserId(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error adding business", e);
        }
    }
}
