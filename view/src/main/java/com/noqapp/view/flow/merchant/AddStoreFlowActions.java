package com.noqapp.view.flow.merchant;

import com.noqapp.search.elastic.service.BizStoreElasticService;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * User: hitender
 * Date: 6/25/17 12:07 AM
 */
@Component
public class AddStoreFlowActions extends RegistrationFlowActions {

    private BusinessUserService businessUserService;

    @SuppressWarnings ("unused")
    @Autowired
    public AddStoreFlowActions(
            ExternalService externalService,
            BizService bizService,
            BusinessUserService businessUserService,
            TokenQueueService tokenQueueService,
            BizStoreElasticService bizStoreElasticService
    ) {
        super(externalService, bizService, tokenQueueService, bizStoreElasticService);
        this.businessUserService = businessUserService;
    }

    @SuppressWarnings ("unused")
    public RegisterBusiness createStoreRegistration() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String qid = queueUser.getQueueUserId();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(qid);
        if (null == businessUser) {
            return null;
        }

        RegisterBusiness registerBusiness = new RegisterBusiness();
        registerBusiness.setBusinessUser(businessUser);
        registerBusiness.setName(new ScrubbedInput(businessUser.getBizName().getBusinessName()));

        return registerBusiness;
    }
}
