package com.noqapp.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.domain.ForgotRecoverEntity;
import com.noqapp.domain.MailEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.types.MailStatusEnum;
import com.noqapp.domain.types.MailTypeEnum;
import com.noqapp.repository.MailManager;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.concurrent.Executors.newCachedThreadPool;

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

    private ExecutorService service;

    private String devSentTo;
    private String domain;
    private String https;
    private String mailInviteQueueSupervisorSubject;
    private String mailRecoverSubject;
    private String mailValidateSubject;
    private String accountNotFoundSubject;

    @Autowired
    public MailService(
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

            AccountService accountService,

            FreemarkerService freemarkerService,
            EmailValidateService emailValidateService,
            MailManager mailManager
    ) {

        this.devSentTo = devSentTo;
        this.domain = domain;
        this.https = https;
        this.mailInviteQueueSupervisorSubject = mailInviteQueueSupervisorSubject;
        this.mailRecoverSubject = mailRecoverSubject;
        this.mailValidateSubject = mailValidateSubject;
        this.accountNotFoundSubject = accountNotFoundSubject;

        this.accountService = accountService;
        this.freemarkerService = freemarkerService;
        this.emailValidateService = emailValidateService;
        this.mailManager = mailManager;

        this.service = newCachedThreadPool();
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
            LOG.info("Account validation sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
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

        if (userAccount.isAccountValidated()) {
            ForgotRecoverEntity forgotRecoverEntity = accountService.initiateAccountRecovery(
                    userAccount.getQueueUserId());

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
        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("businessName", businessName);
        rootMap.put("displayName", displayName);
        rootMap.put("profileName", profileName);

        try {
            LOG.info("Account validation sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(profileName)
                    .setSubject(mailInviteQueueSupervisorSubject + " " + businessName + " invites you for supervising queue " + displayName)
                    .setMessage(freemarkerService.freemarkerToString("mail/inviteAsQueueSupervisor.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
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

        Map<String, String> rootMap = new HashMap<>();
        rootMap.put("businessName", businessName);
        rootMap.put("displayName", displayName);
        rootMap.put("profileName", profileName);

        try {
            LOG.info("Account validation sent to={}", StringUtils.isBlank(devSentTo) ? userId : devSentTo);
            MailEntity mail = new MailEntity()
                    .setToMail(userId)
                    .setToName(profileName)
                    .setSubject(mailInviteQueueSupervisorSubject + " " + businessName + " added you for supervising queue " + displayName)
                    .setMessage(freemarkerService.freemarkerToString("mail/addedAsQueueSupervisor.ftl", rootMap))
                    .setMailStatus(MailStatusEnum.N);
            mailManager.save(mail);
        } catch (IOException | TemplateException exception) {
            LOG.error("Validation failure email for={}", userId, exception);
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
        if (StringUtils.isNotBlank(userId) && !userId.endsWith("mail.noqapp.com")) {
            EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(qid, userId);
            service.submit(() -> accountValidationMail(userId, name, accountValidate.getAuthenticationKey()));
        }
    }
}
