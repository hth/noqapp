package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.MailEntity;
import com.noqapp.domain.StatsCronEntity;
import com.noqapp.domain.types.MailStatusEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.repository.MailManager;
import com.noqapp.service.StatsCronService;

import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * User: hitender
 * Date: 12/10/16 7:59 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class MailProcess {
    private static final Logger LOG = LoggerFactory.getLogger(MailProcess.class);

    private final String doNotReplyEmail;
    private final String emailAddressName;
    private final String devSentTo;
    private final String mailInviteSubject;
    private final String googleSmall;
    private final String facebookSmall;
    private final String appStore;
    private final String googlePlay;
    private final String emailSwitch;
    private final int sendAttempt;
    private final String dkimPath;

    private JavaMailSenderImpl mailSender;
    private MailManager mailManager;
    private StatsCronService statsCronService;

    @Autowired
    public MailProcess(
        @Value ("${do.not.reply.email}")
        String doNotReplyEmail,

        @Value ("${email.address.name}")
        String emailAddressName,

        @Value ("${dev.sent.to}")
        String devSentTo,

        @Value ("${mail.invite.subject}")
        String mailInviteSubject,

        @Value ("${mail.googleSmall:..//jsp//images//smallGoogle.jpg}")
        String googleSmall,

        @Value ("${mail.googlePlay:..//jsp//images//googlePlay151x47.jpg}")
        String googlePlay,

        @Value ("${mail.facebookSmall:..//jsp//images//smallFacebook.jpg}")
        String facebookSmall,

        @Value ("${mail.appStore:..//jsp//images//app-store151x48.jpg}")
        String appStore,

        @Value ("${MailProcess.emailSwitch}")
        String emailSwitch,

        @Value ("${MailProcess.sendAttempt}")
        int sendAttempt,

        @Value ("${MailProcess.dkim.der.path}")
        String dkimPath,

        JavaMailSenderImpl mailSender,
        MailManager mailManager,
        StatsCronService statsCronService
    ) {
        this.doNotReplyEmail = doNotReplyEmail;
        this.emailAddressName = emailAddressName;
        this.devSentTo = devSentTo;
        this.mailInviteSubject = mailInviteSubject;
        this.googleSmall = googleSmall;
        this.googlePlay = googlePlay;
        this.facebookSmall = facebookSmall;
        this.appStore = appStore;
        this.emailSwitch = emailSwitch;
        this.sendAttempt = sendAttempt;
        this.dkimPath = dkimPath;

        this.mailSender = mailSender;
        this.mailManager = mailManager;
        this.statsCronService = statsCronService;
    }

    @Scheduled (fixedDelayString = "${loader.MailProcess.sendMail}")
    public void sendMail() {
        StatsCronEntity statsCron = new StatsCronEntity(
            MailProcess.class.getName(),
            "sendMail",
            emailSwitch);

        if ("OFF".equalsIgnoreCase(emailSwitch)) {
            LOG.warn("Email sending is {}", emailSwitch);
            return;
        }

        List<MailEntity> mails = mailManager.pendingMails();
        if (mails.isEmpty()) {
            /* No documents to upload. */
            return;
        } else {
            LOG.info("Mail to send, count={}", mails.size());
        }

        int success = 0, failure = 0, skipped = 0;
        try {
            for (MailEntity mail : mails) {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = populateMessageBody(mail, message);
                    MailStatusEnum mailStatus = sendMail(mail, message, helper);
                    mailManager.updateMail(mail.getId(), mailStatus);

                    switch (mailStatus) {
                        case S:
                            success++;
                            break;
                        case F:
                            failure++;
                            break;
                        default:
                            LOG.error("Reached unsupported condition={}", mailStatus);
                            throw new UnsupportedOperationException("Reached unsupported condition " + mailStatus);
                    }
                } catch (MessagingException | UnsupportedEncodingException | NoSuchMethodError e) {
                    /* NoSuchMethodError normally happens when DKIM issue. */
                    LOG.error("Failure sending email={} subject={} reason={}", mail.getToMail(), mail.getSubject(), e.getLocalizedMessage(), e);
                    if (sendAttempt < mail.getAttempts()) {
                        mailManager.updateMail(mail.getId(), MailStatusEnum.N);
                        failure++;
                    } else {
                        mailManager.updateMail(mail.getId(), MailStatusEnum.F);
                        skipped++;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error sending mail reason={}", e.getLocalizedMessage(), e);
        } finally {
            if (0 < skipped) {
                LOG.error("Skipped sending mail. Number of attempts exceeded. Take a look.");
            }
            saveUploadStats(statsCron, success, failure, skipped, mails.size());
        }
    }

    private MailStatusEnum sendMail(
        MailEntity mail,
        MimeMessage message,
        MimeMessageHelper helper
    ) throws MessagingException {
        /* Use the true flag to indicate the text included is HTML. */
        helper.setText(mail.getMessage(), true);
        helper.setSubject(mail.getSubject());

        if (mail.getSubject().startsWith(mailInviteSubject)) {
            /* Attach image always at the end. */
            helper.addInline("googlePlus.logo", getFileSystemResource(googleSmall));
            helper.addInline("facebook.logo", getFileSystemResource(facebookSmall));
            helper.addInline("ios.logo", getFileSystemResource(appStore));
            helper.addInline("android.logo", getFileSystemResource(googlePlay));
        }

        try {
            int count = 0;
            boolean connected = false;

            /* Try connecting to mail server. */
            while (!connected && count < PaginationEnum.TEN.getLimit()) {
                count++;
                try {
                    mailSender.testConnection();
                    connected = true;
                } catch (MessagingException m) {
                    LOG.error("Failed to connect with mail server count={} reason={}", count, m.getLocalizedMessage(), m);
                }
            }

            count = 0;
            boolean noAuthenticationException = false;
            mailManager.save(mail);

            /* Number of times send mail has to be tried. */
            while (!noAuthenticationException && count < PaginationEnum.ONE.getLimit()) {
                count++;
                try {
                    MimeMessage dkimSignedMessage = dkimSignMessage(message, dkimPath, "noqapp.com", "noqapp");
                    mailSender.send(dkimSignedMessage);
                    noAuthenticationException = true;
                    LOG.info("Mail success... subject={} to={}", mail.getSubject(), mail.getToMail());
                    return MailStatusEnum.S;
                } catch (MailAuthenticationException | MailSendException e) {
                    LOG.error("Failed to send mail server count={} reason={}", count, e.getLocalizedMessage(), e);
                } catch (Exception e) {
                    LOG.error(e.getLocalizedMessage());
                }
                LOG.warn("Mail fail... subject={} to={}", mail.getSubject(), mail.getToMail());
            }
        } catch (MailSendException mailSendException) {
            LOG.error("Mail send exception={}", mailSendException.getLocalizedMessage());
            throw new MessagingException(mailSendException.getLocalizedMessage(), mailSendException);
        }

        return MailStatusEnum.F;
    }

    private FileSystemResource getFileSystemResource(String location) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        if (null == url) {
            try {
                File file = new File(location);
                if (file.exists()) {
                    url = file.toURI().toURL();
                }
            } catch (MalformedURLException e) {
                LOG.error("URL for file at location={} reason={}", location, e.getLocalizedMessage(), e);
            }
        }
        Assert.notNull(url, "File not found at location " + location);
        return new FileSystemResource(url.getPath());
    }

    private MimeMessageHelper populateMessageBody(MailEntity mail, MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        /* Use the true flag to indicate you need a multipart message. */
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        if (StringUtils.isBlank(mail.getFromMail())) {
            helper.setFrom(new InternetAddress(doNotReplyEmail, emailAddressName));
        } else {
            helper.setFrom(new InternetAddress(mail.getFromMail(), mail.getFromName()));
        }

        String sentTo = StringUtils.isBlank(devSentTo) ? mail.getToMail() : devSentTo;
        if (sentTo.equalsIgnoreCase(devSentTo)) {
            helper.setTo(new InternetAddress(devSentTo, emailAddressName));
        } else {
            helper.setTo(new InternetAddress(mail.getToMail(), mail.getToName()));
        }
        return helper;
    }

    /**
     * Signing message with dkim.
     *
     * Save it like
     * Name: noqapp._domainkey
     * Value: v=DKIM1;g=*;k=rsa;p=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf4lvV
     *        llV2eoDqxartI0bUiJXDv+TVhFoGcheKocQyLGrTi8BKamhoDt8yKiecpCm1rZ/n
     *        llV2eoDqxartI0bUiJXDv+TVhFoGcheKocQyLGrTi8BKamhoDt8yKiecpCm1rZ/n
     *        hwcR1hzavGeY/AVxpEeIvixQNmunxkdaqHCLuQIDAQAB;s=email;t=s
     *
     * @param message
     * @param dkimPath
     * @param signingDomain
     * @param selector
     * @return
     * @throws Exception
     */
    private MimeMessage dkimSignMessage(MimeMessage message, String dkimPath, String signingDomain, String selector) throws Exception {
        DkimSigner dkimSigner = new DkimSigner(signingDomain, selector, getDkimPrivateKeyFileForSender(dkimPath));
        dkimSigner.setIdentity(doNotReplyEmail);
        dkimSigner.setHeaderCanonicalization(Canonicalization.SIMPLE);
        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
        dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA256_WITH_RSA);
        dkimSigner.setLengthParam(true);
        dkimSigner.setCopyHeaderFields(false);
        return new DkimMessage(message, dkimSigner);
    }

    private InputStream getDkimPrivateKeyFileForSender(String dkimPath) {
        return this.getClass().getClassLoader().getResourceAsStream(dkimPath);
    }

    private void saveUploadStats(StatsCronEntity statsCron, int success, int failure, int skipped, int size) {
        statsCron.addStats("success", success);
        statsCron.addStats("skipped", skipped);
        statsCron.addStats("failure", failure);
        statsCron.addStats("found", size);
        statsCronService.save(statsCron);

        LOG.info("Mail sent success={} skipped={} failure={} total={}", success, skipped, failure, size);
    }
}
