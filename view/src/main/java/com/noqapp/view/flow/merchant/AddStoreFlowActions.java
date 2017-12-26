package com.noqapp.view.flow.merchant;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import org.apache.commons.lang3.StringUtils;
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
public class AddStoreFlowActions extends RegistrationFlowActions {

    private BusinessUserService businessUserService;
    private BizService bizService;

    @SuppressWarnings ("unused")
    @Autowired
    public AddStoreFlowActions(
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
    }

    private RegisterBusiness createStoreRegistration() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid);
        if (null == businessUser) {
            return null;
        }

        RegisterBusiness registerBusiness = new RegisterBusiness();
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

}
