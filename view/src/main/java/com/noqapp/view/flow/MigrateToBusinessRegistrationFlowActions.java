package com.noqapp.view.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.MigrateToBusinessRegistration;
import com.noqapp.domain.flow.Register;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.FetcherService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.view.flow.exception.MigrateToBusinessRegistrationException;

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
            FetcherService fetcherService,
            UserProfilePreferenceService userProfilePreferenceService,
            AccountService accountService,
            BusinessUserService businessUserService,
            BizService bizService,
            ExternalService externalService,
            TokenQueueService tokenQueueService
    ) {
        super(externalService, bizService, tokenQueueService);
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

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid);
        if (null == businessUser) {
            businessUser = BusinessUserEntity.newInstance(qid, UserLevelEnum.M_ADMIN);
        }
        businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);

        UserAccountEntity userAccount = accountService.findByReceiptUserId(qid);
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(qid);
        Register register = MigrateToBusinessRegistration.newInstance(businessUser, null);
        register.getRegisterUser().setEmail(userProfile.getEmail())
                .setGender(userProfile.getGender())
                .setBirthday(userProfile.getBirthday())
                .setFirstName(userProfile.getFirstName())
                .setLastName(userProfile.getLastName())
                .setAddress(userProfile.getAddress())
                .setCountryShortName(userProfile.getCountryShortName())
                .setPhone(userProfile.getPhoneRaw())
                .setEmailValidated(userAccount.isAccountValidated())
                .setPhoneValidated(userAccount.isPhoneValidated());
        return register;
    }

    @SuppressWarnings ("unused")
    public boolean isRegistrationComplete(Register register) {
        return isBusinessUserRegistrationComplete(register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus());
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
                BusinessUserEntity businessUser = businessUserService.findBusinessUser(register.getRegisterUser().getQueueUserId());
                if (businessUser == null) {
                    businessUser = BusinessUserEntity.newInstance(register.getRegisterUser().getQueueUserId(), UserLevelEnum.M_ADMIN);
                    businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);
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
            LOG.error("Error adding business qid={} reason={}",
                    registerBusiness.getBusinessUser().getQueueUserId(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error adding business", e);
        }
    }
}