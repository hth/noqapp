package com.noqapp.service;

import static com.noqapp.common.utils.RandomString.MAIL_NOQAPP_COM;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.MailEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.MailStatusEnum;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.repository.MailManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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
    private MailManager mailManager;

    private ExecutorService executorService;

    private String parentHost;
    private String devSentTo;
    private String domain;
    private String https;
    private String mailInviteQueueSupervisorSubject;
    private String mailRecoverSubject;
    private String mailValidateSubject;
    private String accountNotFoundSubject;
    private String doNotReplyEmail;

    @Autowired
    public MailService(
            @Value("${parentHost}")
            String parentHost,

            @Value ("${dev.sent.to}")
            String devSentTo,

            @Value ("${domain}")
            String domain,

            @Value ("${https}")
            String https,

            @Value ("${mail.invite.queue.supervisor.subject}")
            String mailInviteQueueSupervisorSubject,

            @Value ("${mail.recover.subject}")
            String mailRecoverSubject,

            @Value ("${mail.validate.subject}")
            String mailValidateSubject,

            @Value ("${mail.account.not.found.subject}")
            String accountNotFoundSubject,

            @Value ("${do.not.reply.email}")
            String doNotReplyEmail,

            AccountService accountService,

            FreemarkerService freemarkerService,
            EmailValidateService emailValidateService,
            MailManager mailManager
    ) {
        this.parentHost = parentHost;
        this.devSentTo = devSentTo;
        this.domain = domain;
        this.https = https;
        this.mailInviteQueueSupervisorSubject = mailInviteQueueSupervisorSubject;
        this.mailRecoverSubject = mailRecoverSubject;
        this.mailValidateSubject = mailValidateSubject;
        this.accountNotFoundSubject = accountNotFoundSubject;
        this.doNotReplyEmail = doNotReplyEmail;

        this.accountService = accountService;
        this.freemarkerService = freemarkerService;
        this.emailValidateService = emailValidateService;
        this.mailManager = mailManager;

        this.executorService = newCachedThreadPool();
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
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("to", name);
        rootMap.put("contact_email", userId);
        rootMap.put("link", auth);
        rootMap.put("domain", domain);
        rootMap.put("https", https);
        rootMap.put("parentHost", parentHost);

        try {
            LOG.info("Account validation sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(name)
                    .setSubject(mailValidateSubject)
                    .setMessage(freemarkerService.freemarkerToString("mail/self-signup.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Failed validation email for={}", userId, exception);
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

            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("contact_email", userId);
            rootMap.put("domain", domain);
            rootMap.put("https", https);
            rootMap.put("parentHost", parentHost);

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

        if (userAccount.isAccountValidated()) {
            ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(
                    userAccount.getQueueUserId());

            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("to", userAccount.getName());
            rootMap.put("link", forgotRecoverEntity.getAuthenticationKey());
            rootMap.put("domain", domain);
            rootMap.put("https", https);
            rootMap.put("parentHost", parentHost);

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
            /* Since account is not validated, send account validation email. */
            EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                    userAccount.getQueueUserId(),
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

    public MailTypeEnum sendQueueSupervisorInvite(
            String userId,
            String profileName,
            String businessName,
            String displayName
    ) {
        LOG.info("Invitation mail businessName={} to userId={} by displayName={}", businessName, userId, displayName);
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("businessName", businessName);
        rootMap.put("displayName", displayName);
        rootMap.put("profileName", profileName);
        rootMap.put("parentHost", parentHost);

        try {
            LOG.info("Send Queue Supervisor Mail sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(profileName)
                    .setSubject(mailInviteQueueSupervisorSubject + " " + businessName + " invites you for supervising queue " + displayName)
                    .setMessage(freemarkerService.freemarkerToString("mail/invited-queue-supervisor.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Failed validation email for={}", userId, exception);
            return MailTypeEnum.FAILURE;
        }
        return MailTypeEnum.SUCCESS;
    }

    public MailTypeEnum addedAsQueueSupervisorNotifyMail(
            String userId,
            String profileName,
            String businessName,
            String displayName
    ) {
        LOG.info("Added to supervise notify mail businessName={} to userId={} by displayName={}",
                businessName,
                userId,
                displayName);

        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("businessName", businessName);
        rootMap.put("displayName", displayName);
        rootMap.put("profileName", profileName);
        rootMap.put("parentHost", parentHost);

        try {
            LOG.info("Added Queue Supervisor Mail sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(profileName)
                    .setSubject(mailInviteQueueSupervisorSubject + " " + businessName + " added you for supervising queue " + displayName)
                    .setMessage(freemarkerService.freemarkerToString("mail/added-queue-supervisor.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Failed validation email for={}", userId, exception);
            return MailTypeEnum.FAILURE;
        }
        return MailTypeEnum.SUCCESS;
    }

    public MailTypeEnum registrationStatusMail(
            long awaitingBusinessApproval,
            long registeredUser,
            long deviceRegistered,
            long androidDeviceRegistered,
            Map<String, Long> androidFlavoredDevices,
            long iPhoneDeviceRegistered,
            Map<String, Long> iPhoneFlavoredDevices
    ) {
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("registeredUser", Long.toString(registeredUser));
        rootMap.put("awaitingBusinessApproval", Long.toString(awaitingBusinessApproval));
        rootMap.put("deviceRegistered", Long.toString(deviceRegistered));
        rootMap.put("androidDeviceRegistered", Long.toString(androidDeviceRegistered));
        rootMap.put("androidFlavoredDevices", androidFlavoredDevices);
        rootMap.put("iPhoneDeviceRegistered", Long.toString(iPhoneDeviceRegistered));
        rootMap.put("iPhoneFlavoredDevices", iPhoneFlavoredDevices);
        rootMap.put("parentHost", parentHost);

        try {
            LOG.info("Daily Registration Status Mail sent to={}", doNotReplyEmail);
            MailEntity mail = new MailEntity()
                    .setToMail(doNotReplyEmail)
                    .setToName("NoQueue Inc")
                    .setSubject("Daily Registration Status")
                    .setMessage(freemarkerService.freemarkerToString("mail/registration-status.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Failed validation email for={}", devSentTo, exception);
            return MailTypeEnum.FAILURE;
        }
        return MailTypeEnum.SUCCESS;
    }

    /**
     * Send account validation email when mail is not blank or mail address does not ends with mail.noqapp.com.
     *
     * @param userId
     * @param qid
     * @param name
     */
    public void sendValidationMailOnAccountCreation(String userId, String qid, String name) {
        if (StringUtils.isNotBlank(userId) && !userId.endsWith(MAIL_NOQAPP_COM)) {
            EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(qid, userId);
            executorService.submit(() -> accountValidationMail(userId, name, accountValidate.getAuthenticationKey()));
        }
    }

    @Async
    public MailTypeEnum sendAnyMail(
        String userId,
        String profileName,
        String subject,
        Map<String, Object> rootMap,
        String locationOfFTL
    ) {
        try {
            rootMap.put("profileName", profileName);
            rootMap.put("parentHost", parentHost);

            MailEntity mail = new MailEntity()
                .setToMail(userId)
                .setToName(profileName)
                .setSubject(subject)
                .setMessage(freemarkerService.freemarkerToString(locationOfFTL, rootMap))
                .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Failed sending email for={}", userId, exception);
            return MailTypeEnum.FAILURE;
        }
        return MailTypeEnum.SUCCESS;
    }
}
