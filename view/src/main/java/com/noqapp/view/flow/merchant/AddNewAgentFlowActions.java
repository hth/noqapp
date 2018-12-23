package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.RandomString;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.MailService;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.MerchantRegistrationForm;

import org.apache.commons.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Add new users to business. These users don't have a phone number and hence will not have CLIENT role.
 * Rather they would have specific roles. Also, their email has to be validated before persisting these new agents.
 * hitender
 * 2018-12-13 16:44
 */
@Component
public class AddNewAgentFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewAgentFlowActions.class);

    private MailService mailService;
    private BusinessUserService businessUserService;

    @Autowired
    public AddNewAgentFlowActions(MailService mailService, BusinessUserService businessUserService) {
        this.mailService = mailService;
        this.businessUserService = businessUserService;
    }

    /**
     * detail page is similar to registration page.
     * @return
     */
    @SuppressWarnings("unused")
    public MerchantRegistrationForm createNewAgentUser() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        return MerchantRegistrationForm.newInstance();
    }

    @SuppressWarnings("unused")
    public String sendMailVerificationOTP(MerchantRegistrationForm merchantRegistration) {
        String mailOTP = RandomString.newInstance(6).nextString().toUpperCase();
        mailService.sendValidationMailBeforeAccountCreation(
            merchantRegistration.getMail().getText(),
            WordUtils.capitalize(merchantRegistration.getFirstName().getText()) + " " + WordUtils.capitalize(merchantRegistration.getLastName().getText()),
            mailOTP);
        return mailOTP;
    }

    @SuppressWarnings("unused")
    public String validateMail(MerchantRegistrationForm merchantRegistration, String mailOTP) {
        LOG.info("MailOTP={}", mailOTP);
        if (merchantRegistration.getCode().equals(mailOTP)) {
            return LandingController.SUCCESS;
        }
        return "failure";
    }
}
