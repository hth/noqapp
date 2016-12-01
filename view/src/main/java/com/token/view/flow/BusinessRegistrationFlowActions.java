package com.token.view.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.token.domain.BusinessUserEntity;
import com.token.domain.flow.Register;
import com.token.service.BizService;
import com.token.service.BusinessUserService;
import com.token.service.ExternalService;
import com.token.view.controller.open.LoginController;

/**
 * User: hitender
 * Date: 11/23/16 4:17 PM
 */
@Component
public class BusinessRegistrationFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistrationFlowActions.class);

    private BusinessUserService businessUserService;
    private LoginController loginController;

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @SuppressWarnings ("all")
    @Autowired
    public BusinessRegistrationFlowActions(
            ExternalService externalService,
            BusinessUserService businessUserService,
            BizService bizService,
            LoginController loginController) {
        super(externalService, bizService);
        this.businessUserService = businessUserService;
        this.loginController = loginController;
    }

    @SuppressWarnings ("unused")
    public boolean isRegistrationComplete(Register register) {
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(register.getRegisterUser().getRid());
        if (businessUser == null) {
            return false;
        }

        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case C:
                /**
                 * Likelihood of this happening is zero. Because if its approved, they would never land here.
                 * Once the registration is complete, invite is marked as inactive and it can't be re-used.
                 * Even 'V' condition would not make the user land for registration.
                 */
                LOG.error("Reached unsupported rid={} condition={}", register.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", register.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }
}