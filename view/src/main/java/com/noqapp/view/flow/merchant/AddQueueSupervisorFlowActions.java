package com.noqapp.view.flow.merchant;

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
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.view.flow.merchant.exception.InviteSupervisorException;
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
    private String quickDataEntryByPassSwitch;

    private WebFlowUtils webFlowUtils;
    private BizService bizService;
    private AccountService accountService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private TokenQueueService tokenQueueService;
    private MailService mailService;

    private ExecutorService executorService;

    @Autowired
    public AddQueueSupervisorFlowActions(
            @Value ("${BusinessUserStoreService.queue.limit}")
            int queueLimit,

            @Value("${QuickDataEntryByPassSwitch}")
            String quickDataEntryByPassSwitch,

            WebFlowUtils webFlowUtils,
            BizService bizService,
            AccountService accountService,
            BusinessUserService businessUserService,
            BusinessUserStoreService businessUserStoreService,
            TokenQueueService tokenQueueService,
            MailService mailService
    ) {
        this.queueLimit = queueLimit;
        this.quickDataEntryByPassSwitch = quickDataEntryByPassSwitch;

        this.webFlowUtils = webFlowUtils;
        this.bizService = bizService;
        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.tokenQueueService = tokenQueueService;
        this.mailService = mailService;

        this.executorService = newCachedThreadPool();
    }

    @SuppressWarnings ("all")
    public InviteQueueSupervisor inviteSupervisorStart(ExternalContext externalContext) {
        LOG.info("InviteSupervisorStart");
        String bizStoreId = (String) webFlowUtils.getFlashAttribute(externalContext, "bizStoreId");
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);

        InviteQueueSupervisor inviteQueueSupervisor = new InviteQueueSupervisor()
                .setBizStoreId(bizStoreId)
                .setCountryShortName(bizStore.getCountryShortName())
                .setCountryCode(Formatter.findCountryCodeFromCountryShortCode(bizStore.getCountryShortName()));

        return inviteQueueSupervisor;
    }

    @SuppressWarnings ("unused")
    public InviteQueueSupervisor completeInvite(InviteQueueSupervisor inviteQueueSupervisor, MessageContext messageContext) {
        String internationalFormat;
        try {
            internationalFormat = Formatter.phoneInternationalFormat(
                    inviteQueueSupervisor.getPhoneNumber(),
                    inviteQueueSupervisor.getCountryShortName());
            
            LOG.info("International phone number={}", internationalFormat);
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
                /* Check if invitee and store are not in the same country. */
                if (!userProfile.getCountryShortName().equalsIgnoreCase(inviteQueueSupervisor.getCountryShortName())) {
                    return messageWhenStoreAndInviteeAreFromDifferentCountry(inviteQueueSupervisor, messageContext, userProfile);
                } else {
                    /* If they have same country, then we still show this error because the phone number did not match. */
                    return messageWhenCannotFindInviteeWithPhoneNumber(messageContext);
                }
            } else {
                /* Show when phone number and invitee code both did not match. */
                return messageWhenCannotFindInviteeWithPhoneNumber(messageContext);
            }
        }

        /* Check if invitee and store are not in the same country. */
        if (!userProfile.getCountryShortName().equalsIgnoreCase(inviteQueueSupervisor.getCountryShortName())) {
            return messageWhenStoreAndInviteeAreFromDifferentCountry(inviteQueueSupervisor, messageContext, userProfile);
        }

        if (!userProfile.getInviteCode().equals(inviteQueueSupervisor.getInviteeCode())) {

            UserProfileEntity userProfileOfInviteeCode = null;
            if ("ON".equalsIgnoreCase(quickDataEntryByPassSwitch)) {
                userProfileOfInviteeCode = accountService.findProfileByInviteCode(inviteQueueSupervisor.getInviteeCode());
            }

            if (null == userProfileOfInviteeCode) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("User of phone number " + inviteQueueSupervisor.getPhoneNumber() + " does not exists or Invitee code does not match.")
                                .build());

                throw new InviteSupervisorException("User does not exists or Invitee code does not match");
            } else {
                LOG.warn("QuickDataEntryByPassSwitch used by bizStoreId={} for phone={} of uid={}",
                        inviteQueueSupervisor.getBizStoreId(),
                        inviteQueueSupervisor.getPhoneNumber(),
                        userProfile.getQueueUserId());
            }
        }

        BizStoreEntity bizStore = bizService.getByStoreId(inviteQueueSupervisor.getBizStoreId());
        switch (userProfile.getLevel()) {
            case CLIENT:
                LOG.info("Continue invite for qid={} with role={}", userProfile.getQueueUserId(), userProfile.getLevel());
                break;
            case M_ADMIN:
                LOG.warn("Failed invite for qid={} with role={} and being invited by business name={} id={}",
                        userProfile.getQueueUserId(),
                        userProfile.getLevel(),
                        bizStore.getBizName().getBusinessName(),
                        bizStore.getBizName().getId());

                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("This user cannot be invited to supervise queue. Please email with details at contact@noqapp.com")
                                .build());
                throw new InviteSupervisorException("Cannot invite this person");
            case Q_SUPERVISOR:
            case S_MANAGER:
                LOG.warn("Failed invite for qid={} with role={} and being invited by business name={} id={}",
                        userProfile.getQueueUserId(),
                        userProfile.getLevel(),
                        bizStore.getBizName().getBusinessName(),
                        bizStore.getBizName().getId());

                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("This user cannot be invited to supervise queue. Please email with details at contact@noqapp.com")
                                .build());
                throw new InviteSupervisorException("Cannot invite this person");
            default:
                LOG.error("Failed Invite as reached condition for qid={} with role={} for business name={} id={}",
                        userProfile.getQueueUserId(),
                        userProfile.getLevel(),
                        bizStore.getBizName().getBusinessName(),
                        bizStore.getBizName().getId());

                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("This user cannot be invited to supervise queue. Please email with details at contact@noqapp.com")
                                .build());
                throw new InviteSupervisorException("Reached unsupported condition");
        }

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

        if (userProfile.getLevel().getValue() < UserLevelEnum.Q_SUPERVISOR.getValue()) {
            userProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
        }
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getQueueUserId(),
                userProfile.getLevel());
        accountService.save(userAccount);

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(userProfile.getQueueUserId());
        if (null == businessUser) {
            LOG.info("Creating new businessUser qid={}", userProfile.getQueueUserId());
            businessUser = BusinessUserEntity.newInstance(userProfile.getQueueUserId(), userProfile.getLevel());
            if (StringUtils.isBlank(userProfile.getAddress()) || userProfile.getQueueUserId().endsWith("mail.noqapp.com")) {
                businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.I);
            } else {
                businessUser.setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);
            }
            businessUser.setBizName(bizStore.getBizName());
            businessUserService.save(businessUser);
        }

        final String qid = userProfile.getQueueUserId();
        businessUserStoreService.addToBusinessUserStore(
                qid,
                bizStore,
                businessUser.getBusinessUserRegistrationStatus());

        if (BusinessUserRegistrationStatusEnum.V == businessUser.getBusinessUserRegistrationStatus()) {
            /* Send FCM notification. */
            executorService.submit(() -> tokenQueueService.sendMessageToSpecificUser(
                    "Added to supervise Queue: " + bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName() + " has added you to supervise a new queue.",
                    qid));

            /*
             * Send mail to the supervisor after adding them to queue
             * as the supervisor has already been validated.
             */
            mailService.addedAsQueueSupervisorNotifyMail(
                    userAccount.getUserId(),
                    userProfile.getName(),
                    bizStore.getBizName().getBusinessName(),
                    bizStore.getDisplayName()
            );
        } else {
            /* Send FCM notification. */
            executorService.submit(() -> tokenQueueService.sendMessageToSpecificUser(
                    "Invitation to supervise: " + bizStore.getDisplayName(),
                    bizStore.getBizName().getBusinessName() + " has sent an invite. Please login at https://noqapp.com to complete your profile.",
                    qid));

            /* Also send mail to the invitee. */
            mailService.sendQueueSupervisorInvite(
                    userAccount.getUserId(),
                    userProfile.getName(),
                    bizStore.getBizName().getBusinessName(),
                    bizStore.getDisplayName()
            );
        }

        return inviteQueueSupervisor;
    }

    private InviteQueueSupervisor messageWhenCannotFindInviteeWithPhoneNumber(MessageContext messageContext) {
        messageContext.addMessage(
                new MessageBuilder()
                        .error()
                        .source("inviteQueueSupervisor.phoneNumber")
                        .defaultText("Could not find user with matching phone number or invitee code. Please re-confirm.")
                        .build());

        throw new InviteSupervisorException("User does not exists or Invitee code does not match");
    }

    private InviteQueueSupervisor messageWhenStoreAndInviteeAreFromDifferentCountry(
            InviteQueueSupervisor inviteQueueSupervisor,
            MessageContext messageContext,
            UserProfileEntity userProfile
    ) {
        String storeCountry = CommonUtil.getCountryNameFromIsoCode(inviteQueueSupervisor.getCountryShortName());
        String inviteeCountry = CommonUtil.getCountryNameFromIsoCode(userProfile.getCountryShortName());

        messageContext.addMessage(
                new MessageBuilder()
                        .error()
                        .source("inviteQueueSupervisor.phoneNumber")
                        .defaultText("Store Located in "
                                + storeCountry
                                + " and Invitee Located in "
                                + inviteeCountry
                                + ". As they are in different countries, please contact customer support.")
                        .build());

        LOG.warn("Store Located={} and Invitee={} are from two different countries storeId={}",
                storeCountry,
                inviteeCountry,
                inviteQueueSupervisor.getBizStoreId());

        throw new InviteSupervisorException("Store Location and Invitee are from two different countries");
    }
}
