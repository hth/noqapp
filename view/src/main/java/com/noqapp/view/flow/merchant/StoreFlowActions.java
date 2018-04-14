package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.common.utils.ScrubbedInput;

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

    @SuppressWarnings ("unused")
    @Autowired
    public StoreFlowActions(
            Environment environment,
            ExternalService externalService,
            BizService bizService,
            BusinessUserService businessUserService,
            TokenQueueService tokenQueueService,
            BizStoreElasticService bizStoreElasticService
    ) {
        super(environment, externalService, bizService, tokenQueueService, bizStoreElasticService);
        this.businessUserService = businessUserService;
        this.bizService = bizService;
        this.bizStoreElasticService = bizStoreElasticService;
    }

    private RegisterBusiness createStoreRegistration() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        RegisterBusiness registerBusiness = populateWithBizName(businessUser);
        registerBusiness.setBusinessUser(businessUser);
        registerBusiness.setName(new ScrubbedInput(businessUser.getBizName().getBusinessName()));
        registerBusiness.setBusinessTypes(businessUser.getBizName().getBusinessTypes());
        registerBusiness.setCategories(bizService.getBusinessCategoriesAsMap(businessUser.getBizName().getId()));

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
        bizService.deleteStore(bizStoreId);
        bizStoreElasticService.delete(bizStoreId);
    }

    private RegisterBusiness populateWithBizName(BusinessUserEntity businessUser) {
        RegisterBusiness registerBusiness = new RegisterBusiness();
        BizNameEntity bizName = businessUser.getBizName();
        registerBusiness.setBizId(businessUser.getBizName().getId());
        registerBusiness.setName(new ScrubbedInput(bizName.getBusinessName()));
        registerBusiness.setAddress(new ScrubbedInput(bizName.getAddress()));
        registerBusiness.setCountryShortName(new ScrubbedInput(bizName.getCountryShortName()));
        registerBusiness.setPhone(new ScrubbedInput(Formatter.phoneNationalFormat(bizName.getPhoneRaw(), bizName.getCountryShortName())));
        registerBusiness.setTimeZone(new ScrubbedInput(bizName.getTimeZone()));
        registerBusiness.setInviteeCode(bizName.getInviteeCode());
        registerBusiness.setAddressOrigin(bizName.getAddressOrigin());
        registerBusiness.setFoundAddressPlaceId(bizName.getPlaceId());
        return registerBusiness;
    }
}
