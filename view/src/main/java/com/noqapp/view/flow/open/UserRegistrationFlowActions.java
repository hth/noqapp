package com.noqapp.view.flow.open;

import com.noqapp.view.form.MerchantRegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 10/06/2017 11:19 AM
 */
@Component
public class UserRegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationFlowActions.class);

    @SuppressWarnings("unused")
    public MerchantRegistrationForm createUserRegistration() {
        MerchantRegistrationForm merchantRegistrationForm = MerchantRegistrationForm.newInstance();
        return merchantRegistrationForm;
    }

}
