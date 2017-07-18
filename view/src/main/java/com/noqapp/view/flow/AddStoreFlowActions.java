package com.noqapp.view.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.flow.RegisterBusiness;
import com.noqapp.domain.site.TokenUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.ExternalService;
import com.noqapp.service.TokenQueueService;

/**
 * User: hitender
 * Date: 6/25/17 12:07 AM
 */
@Component
public class AddStoreFlowActions extends RegistrationFlowActions {

    private BusinessUserService businessUserService;

    @SuppressWarnings ("all")
    @Autowired
    public AddStoreFlowActions(
            ExternalService externalService,
            BizService bizService,
            BusinessUserService businessUserService,
            TokenQueueService tokenQueueService
    ) {
        super(externalService, bizService, tokenQueueService);
        this.businessUserService = businessUserService;
    }

    @SuppressWarnings ("unused")
    public RegisterBusiness createStoreRegistration() {
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = tokenUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        if (null == businessUser) {
            return null;
        }

        RegisterBusiness registerBusiness = new RegisterBusiness();
        registerBusiness.setBusinessUser(businessUser);
        registerBusiness.setName(businessUser.getBizName().getBusinessName());

        return registerBusiness;
    }
}
