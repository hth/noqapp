package com.noqapp.view.flow.open;

import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.service.MailService;
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

    private MailService mailService;

    public UserRegistrationFlowActions(MailService mailService) {
        this.mailService = mailService;
    }

    @SuppressWarnings("unused")
    public MerchantRegistrationForm createUserRegistration() {
        return MerchantRegistrationForm.newInstance();
    }

    public void sendPasswordRecoveryMail(MerchantRegistrationForm merchantRegistration) {
        MailTypeEnum mailType = mailService.mailRecoverLink(merchantRegistration.getMail().getText().toLowerCase());
        if (MailTypeEnum.FAILURE == mailType) {
            LOG.error("Failed to send recovery email for user={}", merchantRegistration.getMail());
        }

        MailTypeEnum mailState;
        /* But we show success to user on failure. Not sure if we should show a failure message when mail fails. */
        switch (mailType) {
            case FAILURE:
            case ACCOUNT_NOT_VALIDATED:
            case ACCOUNT_NOT_FOUND:
            case SUCCESS:
                mailState = mailType == MailTypeEnum.ACCOUNT_NOT_VALIDATED ? mailType : MailTypeEnum.SUCCESS;
                break;
            default:
                LOG.error("Reached unreachable condition, user={}", merchantRegistration.getMail().getText().toLowerCase());
                throw new UnsupportedOperationException("Reached un-reachable condition for account recovery");
        }

        merchantRegistration.setMailSendState(mailState);
    }
}
