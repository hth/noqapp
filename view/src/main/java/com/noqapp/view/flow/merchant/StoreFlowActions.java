package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.AppointmentStateEnum;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.InvocationByEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.WalkInStateEnum;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.MailService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: hitender
 * Date: 6/25/17 12:07 AM
 */
@Component
public class StoreFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(StoreFlowActions.class);

    private BusinessUserService businessUserService;
    private BizService bizService;
    private BizStoreElasticService bizStoreElasticService;
    private BusinessUserStoreService businessUserStoreService;
    private ProfessionalProfileService professionalProfileService;

    @SuppressWarnings ("unused")
    @Autowired
    public StoreFlowActions(
        Environment environment,
        ExternalService externalService,
        BizService bizService,
        BusinessUserService businessUserService,
        TokenQueueService tokenQueueService,
        BizStoreElasticService bizStoreElasticService,
        BusinessUserStoreService businessUserStoreService,
        ProfessionalProfileService professionalProfileService,
        AccountService accountService,
        MailService mailService
    ) {
        super(environment, externalService, bizService, tokenQueueService, bizStoreElasticService, accountService, mailService);
        this.businessUserService = businessUserService;
        this.bizService = bizService;
        this.bizStoreElasticService = bizStoreElasticService;
        this.businessUserStoreService = businessUserStoreService;
        this.professionalProfileService = professionalProfileService;
    }

    private RegisterBusiness createStoreRegistration() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        RegisterBusiness registerBusiness = RegisterBusiness.populateWithBizName(businessUser.getBizName());
        registerBusiness.setBusinessUser(businessUser);
        registerBusiness.setName(new ScrubbedInput(businessUser.getBizName().getBusinessName()));
        registerBusiness.setBusinessType(businessUser.getBizName().getBusinessType());
        registerBusiness.setStoreBusinessType(businessUser.getBizName().getBusinessType());
        registerBusiness.setCategories(CommonHelper.getCategories(businessUser.getBizName().getBusinessType(), InvocationByEnum.BUSINESS));
        registerBusiness.setClaimed(businessUser.getBizName().isClaimed());
        if (!businessUser.getBizName().isClaimed()) {
            registerBusiness.setWalkInState(WalkInStateEnum.D);
            registerBusiness.setAppointmentState(AppointmentStateEnum.A);
            registerBusiness.setAppointmentDuration(30);
            registerBusiness.setAppointmentOpenHowFar(1);
        }
        return registerBusiness;
    }

    @SuppressWarnings ("unused")
    public RegisterBusiness populateStore(String bizStoreId) {
        if (StringUtils.isBlank(bizStoreId)) {
            return createStoreRegistration();
        } else {
            return editStoreRegistration(bizStoreId);
        }
    }

    private RegisterBusiness editStoreRegistration(String bizStoreId) {
        RegisterBusiness registerBusiness = createStoreRegistration();
        if (null != registerBusiness) {
            BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);
            registerBusiness.populateWithBizStore(bizStore);
            List<StoreHourEntity> storeHours = bizService.findAllStoreHours(bizStoreId);
            registerBusiness.convertToBusinessHours(storeHours);
        }
        return registerBusiness;
    }

    @SuppressWarnings("unused")
    public void deleteStore(String bizStoreId) {
        LOG.info("Delete storeId={}", bizStoreId);
        cleanUpForProfessionalProfile(bizStoreId);
        bizService.deleteStore(bizStoreId);
        bizStoreElasticService.delete(bizStoreId);
    }

    /** Delete residue from deleting store. */
    private void cleanUpForProfessionalProfile(String bizStoreId) {
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);
        if (BusinessTypeEnum.DO == bizStore.getBusinessType()) {
            List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllManagingStoreWithUserLevel(
                bizStore.getId(),
                UserLevelEnum.S_MANAGER);

            for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
                String qid = businessUserStore.getQueueUserId();
                ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(qid);
                professionalProfile.removeManagerAtStoreCodeQR(bizStore.getCodeQR());
                professionalProfileService.save(professionalProfile);
            }
        }
    }
}
