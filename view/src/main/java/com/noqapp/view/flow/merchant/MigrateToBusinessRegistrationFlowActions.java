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
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.FetcherService;
import com.noqapp.service.MailService;
import com.noqapp.service.StoreProductService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.emp.EmpLandingService;
import com.noqapp.view.flow.merchant.exception.MigrateToBusinessRegistrationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/9/16 1:20 PM
 */
@Component
public class MigrateToBusinessRegistrationFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessRegistrationFlowActions.class);

    private String autoApproveBusinessTurnedOn;

    private FetcherService fetcherService;
    private AccountService accountService;
    private BusinessUserService businessUserService;
    private EmpLandingService empLandingService;

    @SuppressWarnings("all")
    @Autowired
    public MigrateToBusinessRegistrationFlowActions(
        @Value("${BusinessRegister.autoApprove.turnedOn:ON}")
        String autoApproveBusinessTurnedOn,

        Environment environment,
        FetcherService fetcherService,
        AccountService accountService,
        BusinessUserService businessUserService,
        BizService bizService,
        ExternalService externalService,
        TokenQueueService tokenQueueService,
        BizStoreElasticService bizStoreElasticService,
        MailService mailService,
        EmpLandingService empLandingService,
        StoreProductService storeProductService,
        BusinessUserStoreService businessUserStoreService,
        AddNewAgentFlowActions addNewAgentFlowActions
    ) {
        super(
            environment,
            externalService,
            bizService,
            tokenQueueService,
            bizStoreElasticService,
            accountService,
            mailService,
            storeProductService,
            businessUserService,
            businessUserStoreService,
            addNewAgentFlowActions);

        this.autoApproveBusinessTurnedOn = autoApproveBusinessTurnedOn;

        this.empLandingService = empLandingService;
        this.fetcherService = fetcherService;
        this.accountService = accountService;
        this.businessUserService = businessUserService;
    }

    public Set<String> findAllDistinctBizName(String bizName) {
        return fetcherService.findAllDistinctBizName(bizName);
    }

    @SuppressWarnings("unused")
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
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
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

    @SuppressWarnings("unused")
    public String isRegistrationComplete(Register register) {
        return isBusinessUserRegistrationComplete(register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus());
    }

    @SuppressWarnings("unused")
    public Register populateBusiness(String bizId) {
        if (StringUtils.isBlank(bizId)) {
            return createBusinessRegistration();
        } else {
            return editBusiness(bizId);
        }
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public void additionalAttributes(RegisterBusiness registerBusiness, String modelType) {
        switch (modelType) {
            case "bizName":
                addAvailableAmenities(registerBusiness, registerBusiness.getBusinessType(), modelType);
                addAvailableFacilities(registerBusiness, registerBusiness.getBusinessType(), modelType);
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
            case FT:
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
            case RS:
                registerBusiness.setAmenitiesAvailable(AmenityEnum.GROCERY);
                break;
            case FT:
                //registerBusiness.setAmenitiesAvailable(null);
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
    @SuppressWarnings("unused")
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
                 * create a new Business Admin or update same account when it was rejected/flagged previously. This
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

                if (autoApproveBusinessTurnedOn.equalsIgnoreCase("ON")) {
                    LOG.info("Approving business as default {}", autoApproveBusinessTurnedOn);
                    empLandingService.approveBusiness(businessUser.getId(), queueUser.getQueueUserId());
                    register.setAutoApproveBusinessTurnedOn(autoApproveBusinessTurnedOn);
                    updateSessionRoleOnBusinessApproved();
                }
                return register;
            } catch (Exception e) {
                LOG.error("Error adding business qid={} reason={}",
                    register.getRegisterUser().getQueueUserId(),
                    e.getLocalizedMessage(),
                    e);
                throw new MigrateToBusinessRegistrationException("Error adding business", e);
            }
        } catch (Exception e) {
            LOG.error("Error updating business user profile qid={} reason={}",
                register.getRegisterUser().getQueueUserId(),
                e.getLocalizedMessage(),
                e);
            throw new MigrateToBusinessRegistrationException("Error updating profile", e);
        }
    }

    /** Upgrade ROLE without logging out. */
    private void updateSessionRoleOnBusinessApproved() {
        if (autoApproveBusinessTurnedOn.equalsIgnoreCase("ON")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_CLIENT.name()));
            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_Q_SUPERVISOR.name()));
            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_S_MANAGER.name()));
            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_M_ACCOUNTANT.name()));
            updatedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ROLE_M_ADMIN.name()));

            QueueUser queueUser = (QueueUser) auth.getPrincipal();
            UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());
            QueueUser updateQueueUser = new QueueUser(
                queueUser.getUsername(),
                userAccount.getUserAuthentication().getPassword(),
                updatedAuthorities,
                userAccount.getQueueUserId(),
                UserLevelEnum.M_ADMIN,
                userAccount.isActive(),
                userAccount.isAccountValidated(),
                queueUser.getCountryShortName(),
                userAccount.getDisplayName()
            );

            Authentication newAuth = new UsernamePasswordAuthenticationToken(updateQueueUser, auth.getCredentials(), updatedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public RegisterBusiness completeRegistrationInformation(RegisterBusiness registerBusiness) throws MigrateToBusinessRegistrationException {
        try {
            return registerBusinessDetails(registerBusiness);
        } catch (Exception e) {
            LOG.error("Error adding business qid={} reason={}", registerBusiness.getBusinessUser().getQueueUserId(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error adding business", e);
        }
    }
}
