package com.noqapp.service;

import com.noqapp.domain.MailEntity;
import com.noqapp.domain.types.MailStatusEnum;
import com.noqapp.repository.MailManager;
import com.noqapp.repository.UserProfileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 2019-07-15 22:00
 */
@Service
public class SystemMailNotificationService {

    private FirebaseMessageService firebaseMessageService;
    private UserProfileManager userProfileManager;
    private MailManager mailManager;
    private Environment environment;

    @Autowired
    public SystemMailNotificationService(
        FirebaseMessageService firebaseMessageService,
        UserProfileManager userProfileManager,
        MailManager mailManager,
        Environment environment
    ) {
        this.firebaseMessageService = firebaseMessageService;
        this.userProfileManager = userProfileManager;
        this.mailManager = mailManager;
        this.environment = environment;
    }

    public void sentAlertMail(String subject, String message) {
        if (environment.getProperty("build.env").equalsIgnoreCase("prod")) {
            MailEntity mail = new MailEntity()
                .setFromName("NoQueue System")
                .setFromMail("corp@noqapp.com")
                .setToName("NoQueue Inc")
                .setToMail("corp@noqapp.com")
                .setSubject(subject)
                .setMessage(message)
                .setMailStatus(MailStatusEnum.N);

            mailManager.save(mail);
        }
    }
}
