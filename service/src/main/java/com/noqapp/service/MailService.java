package com.noqapp.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.MailEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.MailStatusEnum;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.repository.MailManager;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 11/27/16 12:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    /** Not expecting more than 100 invites in a minute. */
    private static final int SIZE_100 = 100;

    private AccountService accountService;
    private EmailValidateService emailValidateService;
    private FreemarkerService freemarkerService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private NotificationService notificationService;
    private MailManager mailManager;

    private String devSentTo;
    private String inviteeEmail;
    private String emailAddressName;
    private String domain;
    private String https;
    private String mailInviteSubject;
    private String mailRecoverSubject;
    private String mailValidateSubject;
    private String mailRegistrationActiveSubject;
    private String accountNotFoundSubject;

    private final Cache<String, String> invitees;


    @Autowired
    public MailService(
            @Value ("${dev.sent.to}")
            String devSentTo,

            @Value ("${invitee.email}")
            String inviteeEmail,

            @Value ("${email.address.name}")
            String emailAddressName,

            @Value ("${domain}")
            String domain,

            @Value ("${https}")
            String https,

            @Value ("${mail.invite.subject}")
            String mailInviteSubject,

            @Value ("${mail.recover.subject}")
            String mailRecoverSubject,

            @Value ("${mail.validate.subject}")
            String mailValidateSubject,

            @Value ("${mail.registration.active.subject}")
            String mailRegistrationActiveSubject,

            @Value ("${mail.account.not.found.subject}")
            String accountNotFoundSubject,

            AccountService accountService,

            FreemarkerService freemarkerService,
            EmailValidateService emailValidateService,
            UserProfilePreferenceService userProfilePreferenceService,
            NotificationService notificationService,
            MailManager mailManager,

            @Value ("${MailService.inviteCachePeriod}")
            int inviteCachePeriod
    ) {

        this.devSentTo = devSentTo;
        this.inviteeEmail = inviteeEmail;
        this.emailAddressName = emailAddressName;
        this.domain = domain;
        this.https = https;
        this.mailInviteSubject = mailInviteSubject;
        this.mailRecoverSubject = mailRecoverSubject;
        this.mailValidateSubject = mailValidateSubject;
        this.mailRegistrationActiveSubject = mailRegistrationActiveSubject;
        this.accountNotFoundSubject = accountNotFoundSubject;

        this.accountService = accountService;
        this.freemarkerService = freemarkerService;
        this.emailValidateService = emailValidateService;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.notificationService = notificationService;
        this.mailManager = mailManager;

        invitees = CacheBuilder.newBuilder()
                .maximumSize(SIZE_100)
                .expireAfterWrite(inviteCachePeriod, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Sends out email to validate account.
     *
     * @param userId
     * @param name
     * @param auth   - Authentication key to authenticate user when clicking link in mail
     * @return
     */
    public boolean accountValidationMail(String userId, String name, String auth) {
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("to", name);
        rootMap.put("contact_email", userId);
        rootMap.put("link", auth);
        rootMap.put("domain", domain);
        rootMap.put("https", https);

        try {
            LOG.info("Account validation sent to={}", StringUtils.isEmpty(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(name)
                    .setSubject(mailValidateSubject)
                    .setMessage(freemarkerService.freemarkerToString("mail/self-signup.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
            return false;
        }
        return true;
    }

    /**
     * Send recover email to user of provided email id.
     * http://bharatonjava.wordpress.com/2012/08/27/sending-email-using-java-mail-api/
     *
     * @param userId
     */
    public MailTypeEnum mailRecoverLink(String userId) {
        UserAccountEntity userAccount = accountService.findByUserId(userId);
        if (null == userAccount) {
            LOG.warn("Could not recover user={}", userId);

            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("contact_email", userId);
            rootMap.put("domain", domain);
            rootMap.put("https", https);

            try {
                MailEntity mail = new MailEntity()
                        .setToMail(userId)
                        .setSubject(accountNotFoundSubject)
                        .setMessage(freemarkerService.freemarkerToString("mail/account-recover-unregistered-user.ftl", rootMap))
                        .setMailStatus(MailStatusEnum.N);
                mailManager.save(mail);

                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException exception) {
                LOG.error("Account not found email={}", exception.getLocalizedMessage(), exception);
            }

            return MailTypeEnum.ACCOUNT_NOT_FOUND;
        }

        if (null != userAccount.getProviderId()) {
            /** Cannot change password for social account. Well this condition is checked in Mobile Server too. */
            LOG.warn("Social account user={} tried recovering password", userId);
            return MailTypeEnum.SOCIAL_ACCOUNT;
        }

        if (userAccount.isAccountValidated()) {
            ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(
                    userAccount.getReceiptUserId());

            Map<String, String> rootMap = new HashMap<>();
            rootMap.put("to", userAccount.getName());
            rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
            rootMap.put("domain", domain);
            rootMap.put("https", https);

            try {
                MailEntity mail = new MailEntity()
                        .setToMail(userId)
                        .setToName(userAccount.getName())
                        .setSubject(mailRecoverSubject)
                        .setMessage(freemarkerService.freemarkerToString("mail/account-recover.ftl", rootMap))
                        .setMailStatus(MailStatusEnum.N);
                mailManager.save(mail);

                return MailTypeEnum.SUCCESS;
            } catch (IOException | TemplateException exception) {
                LOG.error("Recovery email={}", exception.getLocalizedMessage(), exception);
                return MailTypeEnum.FAILURE;
            }
        } else {
            /** Since account is not validated, send account validation email. */
            EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                    userAccount.getReceiptUserId(),
                    userAccount.getUserId());

            boolean status = accountValidationMail(
                    userAccount.getUserId(),
                    userAccount.getName(),
                    accountValidate.getAuthenticationKey());

            if (status) {
                return MailTypeEnum.ACCOUNT_NOT_VALIDATED;
            }
            return MailTypeEnum.FAILURE;
        }
    }
}
