package com.noqapp.service;

import com.noqapp.domain.EmailValidateEntity;
import com.noqapp.repository.EmailValidateManager;
import com.noqapp.utils.HashText;
import com.noqapp.utils.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 11/25/16 10:03 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class EmailValidateService {

    private EmailValidateManager emailValidateManager;

    @Autowired
    public EmailValidateService(EmailValidateManager emailValidateManager) {
        this.emailValidateManager = emailValidateManager;
    }

    public EmailValidateEntity saveAccountValidate(String queueUserId, String userId) {
        String authenticationKey = HashText.computeBCrypt(RandomString.newInstance().nextString());
        EmailValidateEntity emailValidate = EmailValidateEntity.newInstance(queueUserId, userId, authenticationKey);
        saveEmailValidateEntity(emailValidate);
        return emailValidate;
    }

    void saveEmailValidateEntity(EmailValidateEntity emailValidate) {
        emailValidateManager.save(emailValidate);
    }

    public EmailValidateEntity findByAuthenticationKey(String key) {
        return emailValidateManager.findByAuthenticationKey(key);
    }

    void invalidateAllEntries(String queueUserId) {
        emailValidateManager.invalidateAllEntries(queueUserId);
    }
}
