package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.RandomString;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.MailService;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.MerchantRegistrationForm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

/**
 * Add new users to business. These users don't have a phone number and hence will not have CLIENT role.
 * Rather they would have specific roles. Also, their email has to be validated before persisting these new agents.
 * hitender
 * 2018-12-13 16:44
 */
@Component
public class AddNewAgentFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewAgentFlowActions.class);

    private AccountService accountService;
    private MailService mailService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private BizService bizService;

    @Autowired
    public AddNewAgentFlowActions(
        AccountService accountService,
        MailService mailService,
        BusinessUserService businessUserService,
        BusinessUserStoreService businessUserStoreService,
        BizService bizService
    ) {
        this.mailService = mailService;
        this.businessUserService = businessUserService;
        this.accountService = accountService;
        this.businessUserStoreService = businessUserStoreService;
        this.bizService = bizService;
    }

    /**
     * Detail page is similar to registration page.
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
    public String checkIfInviteCouldBeSent(MerchantRegistrationForm merchantRegistration) {
        UserAccountEntity userAccount = accountService.findByUserId(merchantRegistration.getMail().getText());
        BusinessUserEntity businessUser = businessUserService.findByQid(userAccount.getQueueUserId());
        if (null != businessUser) {
            return "failure";
        } else {
            return "success";
        }
    }

    @SuppressWarnings("unused")
    public String availableAgent(MerchantRegistrationForm merchantRegistration) {
        UserAccountEntity userAccount = accountService.findByUserId(merchantRegistration.getMail().getText());
        merchantRegistration
            .setFirstName(new ScrubbedInput(userAccount.getFirstName()))
            .setLastName(new ScrubbedInput(userAccount.getLastName()));
        return sendMailVerificationOTP(merchantRegistration);
    }

    @SuppressWarnings("unused")
    public String sendMailVerificationOTP(MerchantRegistrationForm merchantRegistration) {
        String mailOTP = RandomString.newInstance(6).nextString().toUpperCase();
        String name = WordUtils.capitalize(merchantRegistration.getFirstName().getText()) + " " + WordUtils.capitalize(merchantRegistration.getLastName().getText());
        mailService.sendOTPMail(merchantRegistration.getMail().getText(), name, mailOTP, "email address");
        return mailOTP;
    }

    @SuppressWarnings("unused")
    public String sendInviteMailOTP(MerchantRegistrationForm merchantRegistration) {
        String mailOTP = RandomString.newInstance(6).nextString().toUpperCase();
        UserAccountEntity userAccount = accountService.findByUserId(merchantRegistration.getMail().getText());
        mailService.sendOTPMail(merchantRegistration.getMail().getText(), userAccount.getName(), mailOTP, "invite");
        return mailOTP;
    }

    @SuppressWarnings("unused")
    public String createAccountAndInvite(MerchantRegistrationForm merchantRegistration, String mailOTP, String bizStoreId, ExternalContext externalContext) {
        LOG.info("MailOTP={}", mailOTP);
        if (merchantRegistration.getCode().equals(mailOTP) && StringUtils.isNotBlank(bizStoreId)) {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserAccountEntity userAccount = accountService.createNewAgentAccount(
                queueUser.getQueueUserId(),
                merchantRegistration.getFirstName().getText(),
                merchantRegistration.getLastName().getText(),
                merchantRegistration.getMail().getText(),
                merchantRegistration.getBirthday().getText(),
                GenderEnum.valueOf(merchantRegistration.getGender().getText()),
                merchantRegistration.getPassword().getText()
            );
            return addedToStore(bizStoreId, queueUser, userAccount);
        }
        return "failure";
    }

    public String completeInvite(MerchantRegistrationForm merchantRegistration, String mailOTP, String bizStoreId, ExternalContext externalContext) {
        LOG.info("MailOTP={}", mailOTP);
        if (merchantRegistration.getCode().equals(mailOTP) && StringUtils.isNotBlank(bizStoreId)) {
            QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserAccountEntity userAccount = accountService.findByUserId(merchantRegistration.getMail().getText());
            return addedToStore(bizStoreId, queueUser, userAccount);
        }
        return "failure";
    }

    /** Add to store as complete profile. */
    private String addedToStore(String bizStoreId, QueueUser queueUser, UserAccountEntity userAccount) {
        BusinessUserEntity businessUserAdmin = businessUserService.findByQid(queueUser.getQueueUserId());
        BusinessUserEntity businessUser = new BusinessUserEntity()
            .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C)
            .setBizName(businessUserAdmin.getBizName())
            .setQueueUserId(userAccount.getQueueUserId());
        businessUserService.save(businessUser);

        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(userAccount.getQueueUserId());
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);
        businessUserStoreService.addToBusinessUserStore(
            userProfile.getQueueUserId(),
            bizStore,
            businessUser.getBusinessUserRegistrationStatus(),
            userProfile.getLevel());
        return LandingController.SUCCESS;
    }
}
