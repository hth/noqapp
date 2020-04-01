package com.noqapp.view.flow.merchant;

import static com.noqapp.common.utils.RandomString.MAIL_NOQAPP_COM;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.InviteQueueSupervisor;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.MailService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.TokenQueueService;
import com.noqapp.view.flow.merchant.exception.InviteSupervisorException;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.flow.utils.WebFlowUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

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
    private ProfessionalProfileService professionalProfileService;

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
        MailService mailService,
        ProfessionalProfileService professionalProfileService
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
        this.professionalProfileService = professionalProfileService;

        this.executorService = newCachedThreadPool();
    }

    @SuppressWarnings ("all")
    public InviteQueueSupervisor inviteSupervisorStart(ExternalContext externalContext) {
        LOG.info("InviteSupervisorStart");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        String bizStoreId = (String) webFlowUtils.getFlashAttribute(externalContext, "bizStoreId");
        BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId);

        InviteQueueSupervisor inviteQueueSupervisor = new InviteQueueSupervisor()
                .setBizStoreId(bizStoreId)
                .setCountryShortName(bizStore.getCountryShortName())
                .setCountryCode(Formatter.findCountryCodeFromCountryShortCode(bizStore.getCountryShortName()))
                .setBusinessType(bizStore.getBusinessType());

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
            userProfile = accountService.findProfileByInviteCode(inviteQueueSupervisor.getInviteeCode().getText());
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

        UserProfileEntity userProfileOfInviteeCode = null;
        if (!userProfile.getInviteCode().equals(inviteQueueSupervisor.getInviteeCode().getText())) {
            if ("ON".equalsIgnoreCase(quickDataEntryByPassSwitch)) {
                userProfileOfInviteeCode = accountService.findProfileByInviteCode(inviteQueueSupervisor.getInviteeCode().getText());
                UserAccountEntity userAccount = accountService.findByQueueUserId(userProfile.getQueueUserId());

                /* Force email address validation. */
                if (!userAccount.isAccountValidated()) {
                    LOG.warn("Force email validation={} quickDataEntryByPassSwitch={}", userAccount.getUserId(), quickDataEntryByPassSwitch);
                    userAccount.setAccountValidated(true);
                    accountService.save(userAccount);
                }

                if (userProfile.getQueueUserId().endsWith(MAIL_NOQAPP_COM) || !userAccount.isAccountValidated()) {
                    messageContext.addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("inviteQueueSupervisor.phoneNumber")
                                    .defaultText("This process requires a valid email address. Since user with "
                                            + inviteQueueSupervisor.getPhoneNumber()
                                            + " has not provided email address, you would need to enter user's invitee code and not yours.")
                                    .build());

                    throw new InviteSupervisorException("Override failed as user has not registered with valid email address.");
                }
            }

            /* To avoid inner if logic. UserProfileOfInviteeCode will be null when quickDataEntryByPassSwitch is OFF. */
            if (null == userProfileOfInviteeCode) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("inviteQueueSupervisor.phoneNumber")
                                .defaultText("User of phone number "
                                        + inviteQueueSupervisor.getPhoneNumber()
                                        + " does not exists or Invitee code does not match.")
                                .build());

                throw new InviteSupervisorException("User does not exists or Invitee code does not match");
            } else {
                LOG.warn("QuickDataEntryByPassSwitch used by bizStoreId={} for user phone={} by uid={}",
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
                /* User already has a role set to Q_SUPERVISOR or S_MANAGER, and hence could not be invited. */
                ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(userProfile.getQueueUserId());
                if (null != professionalProfile) {
                    break;
                }

                LOG.warn("Failed invite for qid={} with role={} and is invited by business name={} id={}",
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

        int supervisorCount = businessUserStoreService.getAssignedTokenAndQueues(userProfile.getQueueUserId()).size();
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

        if (Boolean.parseBoolean(inviteQueueSupervisor.getDoctor().getText())) {
            if (!userProfile.getInviteCode().equals(inviteQueueSupervisor.getInviteeCode().getText())) {
                if ("ON".equalsIgnoreCase(quickDataEntryByPassSwitch)) {
                    /* Set MANAGER when its a by-pass. Make sure it matches previous condition. */
                    userProfile.setLevel(UserLevelEnum.S_MANAGER);
                }
            } else {
                /* When set as Supervisor, they get a profile process to migrate. As Manager, there is no profile to migrate. */
                userProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
            }

            /* Create a health care professional profile when selected as a doctor. Mark profile as Store/Queue Manager. */
            professionalProfileService.createProfessionalProfile(userProfile.getQueueUserId());
        } else if (userProfile.getLevel().getValue() < UserLevelEnum.Q_SUPERVISOR.getValue()) {
            userProfile.setLevel(UserLevelEnum.Q_SUPERVISOR);
        }
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getQueueUserId(),
                userProfile.getLevel());
        accountService.save(userAccount);

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(userProfile.getQueueUserId(), bizStore.getBizName().getId());
        if (null == businessUser) {
            LOG.info("Creating new businessUser qid={}", userProfile.getQueueUserId());
            businessUser = BusinessUserEntity.newInstance(userProfile.getQueueUserId(), userProfile.getLevel());
            if (StringUtils.isBlank(userProfile.getAddress()) || userProfile.getQueueUserId().endsWith(MAIL_NOQAPP_COM)) {
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
                businessUser.getBusinessUserRegistrationStatus(),
                userProfile.getLevel());

        if (BusinessUserRegistrationStatusEnum.V == businessUser.getBusinessUserRegistrationStatus()) {
            String title = "Added to supervise Queue: " + bizStore.getDisplayName();
            String body = bizStore.getBizName().getBusinessName() + " has added you to supervise a new queue.";
            /* Send FCM notification. */
            executorService.submit(() -> tokenQueueService.sendMessageToSpecificUser(title, body, qid, MessageOriginEnum.D));

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
            String title = "Invitation to supervise: " + bizStore.getDisplayName();
            String body = bizStore.getBizName().getBusinessName() + " has sent an invite. Please login at https://noqapp.com to complete your profile.";
            executorService.submit(() -> tokenQueueService.sendMessageToSpecificUser(title, body, qid, MessageOriginEnum.D));

            /* Also send mail to the invitee. */
            mailService.sendQueueSupervisorInvite(
                    userAccount.getUserId(),
                    userProfile.getName(),
                    bizStore.getBizName().getBusinessName(),
                    bizStore.getDisplayName()
            );
        }

        /*
         * Conscious decision to let the whole process run before this condition.
         * Override scenario does not mean to skip steps of notifying users.
         */
        if (null != userProfileOfInviteeCode
                && "ON".equalsIgnoreCase(quickDataEntryByPassSwitch)
                && userAccount.isAccountValidated()) {

            BusinessUserEntity businessUserOfInviteeCode = businessUserService.findBusinessUser(userProfileOfInviteeCode.getQueueUserId(), bizStore.getBizName().getId());
            RegisterUser registerUser = new RegisterUser()
                    .setEmail(new ScrubbedInput(userProfile.getEmail()))
                    .setAddress(new ScrubbedInput(businessUserOfInviteeCode.getBizName().getAddress()))
                    .setCountryShortName(new ScrubbedInput(userProfile.getCountryShortName()))
                    .setPhone(new ScrubbedInput(userProfile.getPhoneRaw()))
                    .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
                    .setBirthday(new ScrubbedInput(userProfile.getBirthday()))
                    .setAddressOrigin(businessUserOfInviteeCode.getBizName().getAddressOrigin())
                    .setFirstName(new ScrubbedInput(userProfile.getFirstName()))
                    .setLastName(new ScrubbedInput(userProfile.getLastName()))
                    .setGender(userProfile.getGender())
                    .setQueueUserId(userProfile.getQueueUserId());

            accountService.updateUserProfile(registerUser, userProfile.getEmail());
            businessUserService.markBusinessUserProfileCompleteOnProfileUpdate(userProfile.getQueueUserId(), bizStore.getBizName().getId());

            LOG.warn("Complete process QuickDataEntryByPassSwitch used by bizStoreId={} for user phone={} by uid={}",
                    inviteQueueSupervisor.getBizStoreId(),
                    inviteQueueSupervisor.getPhoneNumber(),
                    userProfile.getQueueUserId());
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
