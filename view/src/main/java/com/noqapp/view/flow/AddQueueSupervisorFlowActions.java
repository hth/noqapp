package com.noqapp.view.flow;

import static java.util.concurrent.Executors.newCachedThreadPool;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.InviteQueueSupervisor;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.MailService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.utils.Formatter;
import com.noqapp.view.flow.exception.InviteSupervisorException;
import com.noqapp.view.flow.utils.WebFlowUtils;

import java.util.concurrent.ExecutorService;

/**
 * User: hitender
 * Date: 7/14/17 9:05 AM
 */
@Component
public class AddQueueSupervisorFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AddQueueSupervisorFlowActions.class);

    private int queueLimit;
    private WebFlowUtils webFlowUtils;
    private BizService bizService;
    private AccountService accountService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private TokenQueueService tokenQueueService;
    private MailService mailService;

    private ExecutorService service;

    @Autowired
    public AddQueueSupervisorFlowActions(
            @Value ("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            WebFlowUtils webFlowUtils,
            BizService bizService,
            AccountService accountService,
            BusinessUserService businessUserService,
            BusinessUserStoreService businessUserStoreService,
            TokenQueueService tokenQueueService,
            MailService mailService
    ) {
        this.queueLimit = queueLimit;

        this.webFlowUtils = webFlowUtils;
        this.bizService = bizService;
        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.tokenQueueService = tokenQueueService;
        this.mailService = mailService;

        this.service = newCachedThreadPool();
    }

    @SuppressWarnings ("all")
    public InviteQueueSupervisor inviteSupervisorStart(ExternalContext externalContext) {
        LOG.info("InviteSupervisorStart");
        String bizStoreId = (String) webFlowUtils.getFlashAttribute(externalContext, "bizStoreId");
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);

        InviteQueueSupervisor inviteQueueSupervisor = new InviteQueueSupervisor();
        inviteQueueSupervisor.setBizStoreId(bizStoreId);
        inviteQueueSupervisor.setCountryShortName(bizStore.getCountryShortName());
        inviteQueueSupervisor.setCountryCode(Formatter.findCountryCodeFromCountryShortCode(bizStore.getCountryShortName()));

        return inviteQueueSupervisor;
    }

    @SuppressWarnings ("unused")
    public InviteQueueSupervisor completeInvite(InviteQueueSupervisor inviteQueueSupervisor, MessageContext messageContext) {
        String internationalFormat;
        try {
            internationalFormat = Formatter.phoneInternationalFormat(
                    inviteQueueSupervisor.getPhoneNumber(),
                    inviteQueueSupervisor.getCountryShortName());
            
            LOG.debug("International phone number={}", internationalFormat);
        } catch (Exception e) {
            LOG.error("Failed parsing international format phone={} countryShortName={}",
                    inviteQueueSupervisor.getPhoneNumber(),
                    inviteQueueSupervisor.getCountryShortName());

            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("Phone number " + inviteQueueSupervisor.getPhoneNumber() + " not valid.")
                            .build());

            throw new InviteSupervisorException("Phone number not valid");
        }

        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(Formatter.phoneCleanup(internationalFormat));
        if (null == userProfile) {
            /* Find based on invitee code, in case the numbers don't match. */
            userProfile = accountService.findProfileByInviteCode(inviteQueueSupervisor.getInviteeCode());
            if (null != userProfile) {
                if (!userProfile.getCountryShortName().equalsIgnoreCase(inviteQueueSupervisor.getCountryShortName())) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("inviteQueueSupervisor.phoneNumber")
                                    .defaultText("Store Located in "
                                            + inviteQueueSupervisor.getCountryShortName()
                                            + " and Invitee Located in "
                                            + userProfile.getCountryShortName()
                                            + ". Please contact customer support since they are in different countries.")
                                    .build());

                    LOG.warn("Store Located={} and Invitee={} are from two different countries storeId={}",
                            inviteQueueSupervisor.getCountryShortName(),
                            userProfile.getCountryShortName(),
                            inviteQueueSupervisor.getBizStoreId());

                    throw new InviteSupervisorException("Store Location and Invitee are from two different countries");
                } else {
                    /* If they have same country, then we still show this error because the phone number did not match. */
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("inviteQueueSupervisor.phoneNumber")
                                    .defaultText("Could not find user with matching phone number or invitee code. Please re-confirm.")
                                    .build());

                    throw new InviteSupervisorException("User does not exists or Invitee code does not match");
                }
            } else {
                /* Show when phone number and invitee code both did not match. */
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("Could not find user with matching phone number or invitee code. Please re-confirm.")
                                .build());

                throw new InviteSupervisorException("User does not exists or Invitee code does not match");
            }
        }

        if (!userProfile.getInviteCode().equals(inviteQueueSupervisor.getInviteeCode())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("User of phone number " + inviteQueueSupervisor.getPhoneNumber() + " does not exists or Invitee code does not match.")
                            .build());

            throw new InviteSupervisorException("User does not exists or Invitee code does not match");
        }

        BizStoreEntity bizStore = bizService.getByStoreId(inviteQueueSupervisor.getBizStoreId());
        int supervisorCount = businessUserStoreService.getQueues(userProfile.getQueueUserId()).size();
        if (supervisorCount > queueLimit) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("User of phone number " + inviteQueueSupervisor.getPhoneNumber() + " already manages " + queueLimit + " queues. Please ask user to un-subscribe from other queues.")
                            .build());

            throw new InviteSupervisorException("User already manages " + queueLimit + " queues. Please ask user to un-subscribe from other queues.");
        }

        boolean userExists = businessUserService.doesBusinessUserExists(userProfile.getQueueUserId(), bizStore.getId());
        if (userExists) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("inviteQueueSupervisor.phoneNumber")
                            .defaultText("User of phone number " + inviteQueueSupervisor.getPhoneNumber() + " already a Supervisor for this queue.")
                            .build());

            throw new InviteSupervisorException("User already a Supervisor for this queue");
        }

        userProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getQueueUserId(),
                userProfile.getLevel());
        accountService.save(userAccount);

        BusinessUserEntity businessUser = BusinessUserEntity.newInstance(userProfile.getQueueUserId(), userProfile.getLevel());
        if (StringUtils.isBlank(userProfile.getAddress()) || userProfile.getQueueUserId().endsWith("mail.noqapp.com")) {
            businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
        } else {
            businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);
        }
        businessUser.setBizName(bizStore.getBizName());
        businessUserService.save(businessUser);

        BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
                userProfile.getQueueUserId(),
                bizStore.getId(),
                bizStore.getBizName().getId(),
                bizStore.getCodeQR());

        /*
         * Marked as inactive until user signs and agrees to be a queue supervisor.
         * Will be active upon approval.
         */
        businessUserStore.inActive();
        businessUserStoreService.save(businessUserStore);
        final String qid = userProfile.getQueueUserId();

        /* Send personal FCM notification. */
        service.submit(() -> tokenQueueService.sendInviteToNewQueueSupervisor(
                qid,
                bizStore.getDisplayName(),
                bizStore.getBizName().getBusinessName()));

        /* Also send mail to the invitee. */
        mailService.sendQueueSupervisorInvite(
                userAccount.getUserId(),
                userProfile.getName(),
                bizStore.getBizName().getBusinessName(),
                bizStore.getDisplayName()
        );

        return inviteQueueSupervisor;
    }
}
